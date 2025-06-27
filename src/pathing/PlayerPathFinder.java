package pathing;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;

import java.util.*;

/**
 * A BFS-based PlayerPathFinder that uses a predefined set of valid tiles
 * and multiple possible destinations.
 * Positions not in the provided validTiles set are treated as walls/voids.
 */
public class PlayerPathFinder
{
    private final Position start;
    private final Set<Position> destinations;
    private final Set<Position> validTiles;
    private List<Position> storedPath = Collections.emptyList();

    /**
     * Constructs a PlayerPathFinder.
     *
     * @param start The starting position.
     * @param destinations A collection of target positions; BFS stops at the first reached.
     * @param validTiles A set of all walkable tiles.
     */
    public PlayerPathFinder(Position start, Collection<Position> destinations, Set<Position> validTiles)
    {
        this.start = Objects.requireNonNull(start, "Start cannot be null");
        this.destinations = Collections.unmodifiableSet(new HashSet<>(
                Objects.requireNonNull(destinations, "Destinations cannot be null")
        ));
        this.validTiles = Objects.requireNonNull(validTiles, "Valid tiles set cannot be null");
    }

    /**
     * Runs the pathfinding and stores the result.
     *
     * @return This Pathfinder, for chaining.
     */
    public PlayerPathFinder buildPath()
    {
        this.storedPath = bfs(start, destinations);
        return this;
    }

    /**
     * @return The computed path, including start and destination. Empty if none found.
     */
    public List<Position> getPath()
    {
        return storedPath;
    }

    /**
     * @return True if a path was found to any destination.
     */
    public boolean found()
    {
        return !storedPath.isEmpty();
    }

    /**
     * @return The number of steps between tiles, or -1 if no path.
     */
    public int length()
    {
        return found() ? storedPath.size() - 1 : -1;
    }

    /**
     * Walks towards the next reachable tile in the stored path.
     *
     * @return True if a step was issued; false otherwise.
     */
    public boolean walk()
    {
        Position next = getNextWalkPosition();
        if (next == null)
        {
            Log.warn("No reachable tile to walk to");
            return false;
        }
        Log.info("Stepping towards " + next);
        Movement.walkTowards(next);
        return true;
    }

    /**
     * Finds the furthest tile in the path within one step (Chebyshev distance ≤ 2).
     */
    public Position getNextWalkPosition()
    {
        for (int i = storedPath.size() - 1; i >= 0; i--)
        {
            Position pos = storedPath.get(i);
            if (start.distance(org.rspeer.commons.math.Distance.CHEBYSHEV, pos) <= 2)
            {
                return pos;
            }
        }
        return null;
    }

    /**
     * Performs BFS over valid tiles to find the shortest path to any destination.
     */
    private List<Position> bfs(Position start, Set<Position> destinations)
    {
        List<Position> result = new ArrayList<>();
        if (destinations.contains(start))
        {
            result.add(start);
            return result;
        }

        Queue<Position> queue = new LinkedList<>();
        Map<Position, Position> parent = new HashMap<>();

        queue.add(start);
        parent.put(start, null);

        while (!queue.isEmpty())
        {
            Position current = queue.poll();
            for (Position neighbor : getNeighbors(current))
            {
                if (!parent.containsKey(neighbor) && validTiles.contains(neighbor))
                {
                    parent.put(neighbor, current);
                    if (destinations.contains(neighbor))
                    {
                        // Reconstruct path to this destination
                        Position step = neighbor;
                        while (step != null)
                        {
                            result.add(step);
                            step = parent.get(step);
                        }
                        Collections.reverse(result);
                        return result;
                    }
                    queue.add(neighbor);
                }
            }
        }
        return result;
    }

    /**
     * Returns OSRS-ordered neighboring tiles (4-way then 4 diagonals).
     */
    private List<Position> getNeighbors(Position tile)
    {
        List<Position> neighbors = new ArrayList<>(8);
        int x = tile.getX();
        int y = tile.getY();
        int z = tile.getFloorLevel();

        Position w = new Position(x - 1, y, z);
        Position e = new Position(x + 1, y, z);
        Position s = new Position(x, y - 1, z);
        Position n = new Position(x, y + 1, z);
        boolean west = validTiles.contains(w);
        boolean east = validTiles.contains(e);
        boolean south = validTiles.contains(s);
        boolean north = validTiles.contains(n);

        if (west)
        {
            neighbors.add(w);
        }
        if (east)
        {
            neighbors.add(e);
        }
        if (south)
        {
            neighbors.add(s);
        }
        if (north)
        {
            neighbors.add(n);
        }

        // Diagonals only if both adjacent orthogonals are valid
        if (south && west)
        {
            neighbors.add(new Position(x - 1, y - 1, z));
        }
        if (south && east)
        {
            neighbors.add(new Position(x + 1, y - 1, z));
        }
        if (north && west)
        {
            neighbors.add(new Position(x - 1, y + 1, z));
        }
        if (north && east)
        {
            neighbors.add(new Position(x + 1, y + 1, z));
        }

        return neighbors;
    }

    /**
     * Convenience: computes distance as path length or Integer.MAX_VALUE if unreachable.
     */
    public static int distance(Position start, Collection<Position> destinations, Set<Position> validTiles)
    {
        PlayerPathFinder pf = new PlayerPathFinder(start, destinations, validTiles)
                .buildPath();
        int len = pf.length();
        return len >= 0 ? len : Integer.MAX_VALUE;
    }
}
