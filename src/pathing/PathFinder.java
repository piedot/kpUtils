package pathing;

import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.movement.pathfinding.Collisions;
import org.rspeer.game.position.Position;

import java.util.*;

/**
 * This Pathfinder is designed for boss fights, not actual path traversal (or web-walking)
 */
public class PathFinder
{
    public enum TYPE
    {
        NOWALK,
        NOPATH, // will be used for the pathfinder, but not walked on by the walker
        NORMAL
    }

    private Position storedStart = null;
    private Position storedDestination = null;

    static public class Node
    {
        TYPE type;
        Position lastTile = null;

        public Node(TYPE state, Position lastTile)
        {
            this.type = state;
            this.lastTile = lastTile;
        }
    }

    private List<Position> storedPath = new ArrayList<>();
    private Map<Position, Node> nodeMap = new HashMap<>();

    public List<Position> getPath()
    {
        return storedPath;
    }

    public PathFinder(final Position start, final Position destination)
    {
        this.storedStart = start;
        this.storedDestination = destination;
    }

    public PathFinder buildPath()
    {
        storedPath = bfs(storedStart, storedDestination);

        return this;
    }

    public PathFinder setPositionTypes(List<Position> positions, TYPE type)
    {
        if (storedStart == null || storedDestination == null)
        {
            Log.warn("setPositionTypes(" + type + ") - no stored start/destination");
            return this;
        }

        for (Position position : positions)
        {
            nodeMap.put(position, new Node(type, null));
        }

        return this;
    }

    public List<Position> bfs(Position start, Position destination)
    {
        Log.info("bfs");

        List<Position> returnPath = new ArrayList<>();

        if (start == null || destination == null)
        {
            Log.warn("Path was not set");
            return returnPath;
        }

        if (start.equals(destination))
        {
            returnPath.add(start);
            return returnPath;
        }

        ArrayList<Position> path = new ArrayList<>(); //
        path.add(start);
        nodeMap.put(start, new Node(TYPE.NORMAL, new Position(0, 0)));

        for (int i = 0; i < path.size(); i++)
        {
            if (i > 64 * 64)
            {
                Log.warn("PathFinder - reached max length " + i);
                break;
            }

            Position currentTile = path.get(i);

            for (Position neighbor : getNeighbors(currentTile))
            {
                Node currentNode = nodeMap.get(neighbor);
                TYPE type = TYPE.NORMAL;

                if (currentNode != null)
                {
                    if (currentNode.lastTile != null) // Already searched
                    {
                        continue;
                    }

                    type = currentNode.type;
                }

                nodeMap.put(neighbor, new Node(type, currentTile));

                if (neighbor.equals(destination)) // Found
                {
                    nodeMap.remove(start);
                    Position tracer = destination;
                    Node node;

                    while (true)
                    {
                        node = nodeMap.get(tracer);

                        if (node == null)
                        {
                            break;
                        }

                        returnPath.add(tracer);
                        tracer = node.lastTile;
                    }

                    returnPath.add(start);

                    Collections.reverse(returnPath);

                    return returnPath;
                }

                path.add(neighbor);
            }
        }

        return returnPath;
    }

    /**
     * @return Returns the neighbors of the input tile in the order OSRS does
     */
    private ArrayList<Position> getNeighbors(final Position currentTile)
    {
        ArrayList<Position> neighbours = new ArrayList<>();

        boolean s = false,  e = false,  n = false,  w = false,
                sw = false, se = false, ne = false, nw = false;

        int x = currentTile.getX();
        int y = currentTile.getY();
        int level = currentTile.getFloorLevel();

        Position westPos    = new Position(x - 1, y, level);
        Position eastPos    = new Position(x + 1, y, level);
        Position southPos   = new Position(x, y - 1, level);
        Position northPos   = new Position(x, y + 1, level);
        Position swPos      = new Position(x - 1, y - 1, level);
        Position sePos      = new Position(x + 1, y - 1, level);
        Position nwPos      = new Position(x - 1, y + 1, level);
        Position nePos      = new Position(x + 1, y + 1, level);

        if (isValid(westPos))
        {
            neighbours.add(westPos);
            w = true;
        }

        if (isValid(eastPos))
        {
            neighbours.add(eastPos);
            e = true;
        }

        if (isValid(southPos))
        {
            neighbours.add(southPos);
            s = true;
        }

        if (isValid(northPos))
        {
            neighbours.add(northPos);
            n = true;
        }

        sw = s && w;
        se = s && e;
        nw = n && w;
        ne = n && e;

        if (sw && isValid(swPos))
        {
            neighbours.add(swPos);
        }

        if (se && isValid(sePos))
        {
            neighbours.add(sePos);
        }

        if (nw && isValid(nwPos))
        {
            neighbours.add(nwPos);
        }

        if (ne && isValid(nePos))
        {
            neighbours.add(nePos);
        }

        return neighbours;
    }

    private boolean isValid(final Position pos)
    {
        Node node = nodeMap.get(pos);
        return (pos.equals(storedDestination) || Collisions.isReachable(pos)) && (node == null || node.type != TYPE.NOWALK);
    }

    public boolean found()
    {
        return !storedPath.isEmpty();
    }

    /**
     * @return the length of the path in tiles, or -1 if the path is empty
     */
    public int length()
    {
        if (!storedPath.isEmpty())
        {
            return storedPath.size() - 1; // -1 due to our path containing the start position
        }

        return -1;
    }

    /**
     * Steps to the furthest tile in the list that we can reach in one step
     */
    public boolean walk()
    {
        Position nextWalkPosition = getNextWalkPosition();

        if (nextWalkPosition == null)
        {
            Log.warn("nextWalkPosition was null");
            return false;
        }

        Log.info("Step on " + nextWalkPosition);

        Movement.walkTowards(nextWalkPosition);

        return true;
    }

    /**
     * @return the furthest tile in the list that we can reach in one step
     */
    public Position getNextWalkPosition() // inshallah i todo this one day
    {
        //for (int i = storedPath.size() - 1; i > -1; i--)
        //{
        //    Position currentPosition = storedPath.get(i);
//
        //    List<Position> pathToPosition = bfs(storedStart, currentPosition);
//
        //    boolean goodPath = true;
        //    for (Position position : pathToPosition)
        //    {
        //        if (!storedPath.contains(position))
        //        {
        //            goodPath = false;
        //            break;
        //        }
        //    }
//
        //    if (goodPath)
        //        return currentPosition;
        //}

        for (int i = storedPath.size() - 1; i > -1; i--)
        {
            Position position = storedPath.get(i);
            Node node = nodeMap.get(position);

            if (storedStart.distance(Distance.CHEBYSHEV, position) > 2 || node.type == TYPE.NOPATH)
                continue;

            return position;
        }

        return null;
    }

    public static int distance(Position start, Position destination)
    {
        int length = new PathFinder(start, destination).buildPath().length();

        return length == -1 ? Integer.MAX_VALUE : length;
    }
}