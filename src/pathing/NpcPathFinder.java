package pathing;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.movement.pathfinding.Collisions;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Npcs;
import utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Pathfinder is designed for boss fights, not actual path traversal (or web-walking)
 */
public class NpcPathFinder
{
    public enum TYPE
    {
        NOWALK,
        NOPATH, // will be used for the pathfinder, but not walked on by the walker
        NORMAL
    }

    private Npc storedNpc = null;
    private int storedNpcRange = -1;
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
    private boolean foundPath = false;

    public List<Position> getPath()
    {
        return storedPath;
    }

    public NpcPathFinder(final Npc npc, final int npcRange, final Position destination)
    {
        this.storedNpc = npc;
        this.storedNpcRange = npcRange;
        this.storedDestination = destination;
    }

    // TODO this is wrong
    public NpcPathFinder buildPath()
    {
        Position npcPosition = storedNpc.getPosition();
        int npcWidth = storedNpc.getEntityPositionWidth();
        int npcHeight = storedNpc.getEntityPositionHeight();

        // if east or west, move east or west
        // if north or south, move north or south

        List<Position> path = new ArrayList<>();
        path.add(npcPosition);

        Position currentPosition = npcPosition;

        while (!currentPosition.equals(storedDestination))
        {
            int offsetX = 0;
            int offsetY = 0;

            int npcX = currentPosition.getX();
            int npcY = currentPosition.getY();

            int destinationX = storedDestination.getX();
            int destinationY = storedDestination.getY();

            Position offset;
            Position offsetPosition;

            // East & West

            if (npcX < destinationX)
            {
                // East
                offsetX = 1;
            }
            else if (npcX > destinationX)
            {
                // West
                offsetX = -1;
            }

            // There has to be something to optimize here

            offset = new Position(offsetX, offsetY);
            offsetPosition = kpUtils.Add(currentPosition, offset);
            Area offsetArea = kpUtils.GetAreaFrom(offsetPosition, npcWidth - 1, npcHeight - 1);

            boolean failedToMoveEastOrWest = false;

            for (Position position : offsetArea.getTiles())
            {
                if (!Collisions.isReachable(position))
                {
                    failedToMoveEastOrWest = true;
                    offsetX = 0;
                    break;
                }
            }

            // Corner safe spotting

            if (failedToMoveEastOrWest)
            {
                if (
                        kpUtils.GetMeleeTiles(offsetPosition, npcWidth, npcHeight).contains(storedDestination) &&
                        kpUtils.DistanceTo(offsetArea, storedDestination) <= storedNpcRange
                )
                {
                    //Log.info("Distance <= " + storedNpcRange);
                    foundPath = true;
                    break;
                }
            }

            // North & South

            if (npcY < destinationY)
            {
                // North
                offsetY = 1;
            }
            else if (npcY > destinationY)
            {
                // South
                offsetY = -1;
            }

            offset = new Position(offsetX, offsetY);
            offsetPosition = kpUtils.Add(currentPosition, offset);

            for (Position position : kpUtils.GetAreaFrom(offsetPosition, npcWidth - 1, npcHeight - 1).getTiles())
            {
                if (!Collisions.isReachable(position))
                {
                    offsetY = 0;
                    break;
                }
            }

            if (offsetX == 0 && offsetY == 0)
            {
                // Path not found
                foundPath = false;
                break;
            }

            offset = new Position(offsetX, offsetY);

            currentPosition = kpUtils.Add(currentPosition, offset);

            path.add(currentPosition);

            continue;
        }

        storedPath = path;

        return this;
    }

    private boolean isValid(final Position pos)
    {
        for (Position npcPos : kpUtils.GetAreaFrom(pos, storedNpc.getEntityPositionWidth() - 1, storedNpc.getEntityPositionHeight() - 1).getTiles())
        {
            if (!Collisions.isReachable(npcPos))
                return false;
        }

        Node node = nodeMap.get(pos);

        return node == null || node.type != TYPE.NOWALK;
    }

    public boolean found()
    {
        return foundPath;
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
}