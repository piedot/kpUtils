package pathing;

import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Players;
import utils.kpMovement;

import java.util.*;

public class NewPathFinder
{
    public enum TileType
    {
        NOWALK,    // Tile can be traversed but should be avoided
        NOPATH,    // Pathfinder can use, but walker cannot step on
        NORMAL     // Fully walkable
    }

    private Position start;
    private Position destination;
    private final Set<Position> walkableSet;
    private Map<Position, TileType> tileTypes = new HashMap<>();
    private List<Position> path = new ArrayList<>();
    private Map<Position, Position> cameFrom = new HashMap<>();
    private boolean avoidDanger = true; // Controls whether to avoid NOWALK tiles

    public NewPathFinder(Position start, Position destination, Set<Position> walkableSet)
    {
        this.start = start;
        this.destination = destination;
        this.walkableSet = walkableSet;
    }

    public void setTileTypes(Map<Position, TileType> tileTypes)
    {
        this.tileTypes = tileTypes;
    }

    public List<Position> getPath()
    {
        return path;
    }

    public boolean calculatePath()
    {
        // First try: Avoid dangerous tiles
        avoidDanger = true;
        if (calculatePathInternal()) {
            return true;
        }

        // Second try: Allow dangerous tiles
        avoidDanger = false;
        return calculatePathInternal();
    }

    private boolean calculatePathInternal()
    {
        path.clear();
        cameFrom.clear();

        if (!walkableSet.contains(start) || !walkableSet.contains(destination))
        {
            return false;
        }
        if (start.equals(destination))
        {
            path.add(start);
            return true;
        }

        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        cameFrom.put(start, null);

        int searchLimit = 128 * 128;
        int processed = 0;

        while (!queue.isEmpty() && processed < searchLimit)
        {
            Position current = queue.poll();
            processed++;

            if (current.equals(destination))
            {
                reconstructPath();
                return true;
            }

            for (Position neighbor : getValidNeighbors(current))
            {
                if (!visited.contains(neighbor))
                {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    private List<Position> getValidNeighbors(Position tile)
    {
        List<Position> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0},  // West
                {1, 0},   // East
                {0, -1},  // South
                {0, 1},   // North
                {-1, -1}, // SW
                {1, -1},  // SE
                {-1, 1},  // NW
                {1, 1}    // NE
        };

        int x = tile.getX();
        int y = tile.getY();
        int level = tile.getFloorLevel();

        // Check cardinal directions first
        for (int i = 0; i < 4; i++)
        {
            Position neighbor = new Position(x + directions[i][0], y + directions[i][1], level);
            if (isValidTile(neighbor))
            {
                neighbors.add(neighbor);
            }
        }

        // Check diagonal directions with adjacency validation
        for (int i = 4; i < 8; i++)
        {
            Position neighbor = new Position(x + directions[i][0], y + directions[i][1], level);
            Position adj1 = new Position(x + directions[i][0], y, level);
            Position adj2 = new Position(x, y + directions[i][1], level);

            if (isValidTile(neighbor) && isValidTile(adj1) && isValidTile(adj2))
            {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    private boolean isValidTile(Position pos)
    {
        if (!walkableSet.contains(pos)) {
            return false;
        }

        if (avoidDanger) {
            TileType type = tileTypes.getOrDefault(pos, TileType.NORMAL);
            return type != TileType.NOWALK;
        }
        return true; // Allow all tiles when not avoiding danger
    }

    private void reconstructPath()
    {
        Position current = destination;
        while (current != null)
        {
            path.add(0, current);
            current = cameFrom.get(current);
        }
    }

    public static int distance(Position start, Position end, Set<Position> walkableSet)
    {
        NewPathFinder finder = new NewPathFinder(start, end, walkableSet);
        return finder.calculatePath() ? finder.path.size() - 1 : Integer.MAX_VALUE;
    }

    public Position getNextWalkPosition()
    {
        if (path.isEmpty())
        {
            Log.warn("walk - path was empty");
            return null;
        }

        List<Position> reversedPath = new ArrayList<>(path);
        Collections.reverse(reversedPath);
        for (Position position : reversedPath)
        {
            if (tileTypes.getOrDefault(position, TileType.NORMAL) != TileType.NORMAL)
                continue;

            // Really expensive but will do this until a better solution for checking two tile steps
            NewPathFinder walkFinder = new NewPathFinder(start, position, walkableSet);
            walkFinder.calculatePath();

            int pathSize = walkFinder.pathSize();
            if (pathSize > 1 && pathSize < 4)
            {
                return position;
            }
        }

        int indexToUse = Math.min(2, path.size() - 1);
        Log.info("Using index " + indexToUse + " for next walk position");
        return path.get(indexToUse);
    }

    public boolean walk()
    {
        Position nextPosition = getNextWalkPosition();

        if (nextPosition == null)
        {
            Log.warn("walk - nextPosition was null");
            return false;
        }

        kpMovement.Step(nextPosition);
        return true;
    }

    public int pathSize()
    {
        return path.size();
    }
}