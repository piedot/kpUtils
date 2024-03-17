package utils;

import jag.game.scene.RSCollisionMap;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.type.Interactable;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.InterfaceComposite;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.tdi.Tab;
import org.rspeer.game.component.tdi.Tabs;
import org.rspeer.game.movement.pathfinding.util.CollisionFlags;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.Projection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class kpUtils
{
    // Tiles

    public static Area GetAreaFromTile(Position tile, int size)
    {
        int x = tile.getX();
        int y = tile.getY();
        return Area.rectangular(x - size, y - size, x + size, y + size, tile.level);
    }

    public static double DistanceTo(Area area, Position destination)
    {
        double lowest = Double.MAX_VALUE;

        for (Position position : area.getTiles())
        {
            double distance = position.distance(Distance.CHEBYSHEV, destination);

            if (distance < lowest)
            {
                lowest = distance;
            }
        }

        return lowest;
    }

    /**
     * Does not add the floor level together, instead it returns the floor level of the first position
     */
    public static Position Add(Position p1, Position p2)
    {
        return new Position(p1.getX() + p2.getX(), p1.getY() + p2.getY(), p1.getFloorLevel());
    }

    /**
     * Does not subtract the floor level, instead it returns the floor level of the first position
     */
    public static Position Subtract(Position p1, Position p2)
    {
        return new Position(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getFloorLevel());
    }

    public static Position GetInstancePosition(Position globalPosition)
    {
        return globalPosition.getInstancePositions().stream()
                .filter(x -> x.getFloorLevel() == globalPosition.getFloorLevel())
                .min(Comparator.comparingInt(x -> (int) x.distance(Players.self())))
                .orElse(globalPosition);
    }

    public static ArrayList<Position> GetTilesFromOffsets(Position base, List<Position> offsets)
    {
        ArrayList<Position> returnTiles = new ArrayList<>();

        for (Position offset : offsets)
        {
            returnTiles.add(new Position(base.getX() + offset.getX(), base.getY() + offset.getY(), base.level));
        }

        return returnTiles;
    }

    // Tabs

    public static void OpenTabIfNeeded(Tab tab)
    {
        if (!Tabs.isOpen(tab))
        {
            Tabs.open(tab);
        }
    }

    // Misc

    public static int GetBit(int pos, int val)
    {
        return (pos >> val) & 1;
    }

    public enum COMBAT_STYLE
    {
        INVALID,
        MELEE,
        RANGED,
        MAGIC
    }

    public static COMBAT_STYLE GetCombatStyle()
    {
        Combat.WeaponType weaponType = Combat.getWeaponType();

        switch (weaponType)
        {
            case TYPE_3: // BOW
            case TYPE_5: // CROSSBOW
            case TYPE_7: // CHINCHOMPA
            case TYPE_19: // THROWN (AND BLOWPIPE)
                return COMBAT_STYLE.RANGED;
            case TYPE_18: // STAFF
            case TYPE_21: // BLADED STAFF
            case TYPE_23: // POWERED_STAFF
                return COMBAT_STYLE.MAGIC;
            case TYPE_0: // UNARMED
            case TYPE_1: // AXE
            case TYPE_2: // BLUNT
            case TYPE_4: // CLAW
            case TYPE_6: // SALAMANDER
            case TYPE_8: // GUN
            case TYPE_9: // SLASH_SWORD
            case TYPE_10: // TWO_HANDED_SWORD
            case TYPE_11: // PICKAXE
            case TYPE_12: // POLEARM
            case TYPE_13: // POLESTAFF
            case TYPE_14: // SCYTHE/PARTISAN
            case TYPE_15: // SPEAR
            case TYPE_16: // SPIKED
            case TYPE_17: // STAB_SWORD
            case TYPE_20: // WHIP
            case TYPE_22: // SCYTHE/PARTISAN
            case TYPE_24: // BANNER
            case TYPE_25: // ?
            case TYPE_26: // BLUDGEON
            case TYPE_27: // BULWARK
                return COMBAT_STYLE.MELEE;
            default:
                //
        }

        return COMBAT_STYLE.INVALID;
    }

    // e.g. entering an instance, when your screen fades black
    // works with: Scurrius
    // doesn't work with: The whisperer blackstone
    public static boolean IsFadingBlack()
    {
        InterfaceComponent component = Interfaces.query(174).results().first();

        return component != null && component.isVisible();
    }

    /**
     * @param source source position
     * @param target target position
     * @return If the target is in our field of view
     */
    public static boolean isInFieldOfView(SceneNode source, SceneNode target)
    {
        if (source.getFloorLevel() != target.getFloorLevel())
        {
            return false;
        }

        Position sourcePosition = source.getPosition().toScene();
        Position targetPosition = target.getPosition().toScene();

        if (sourcePosition.getX() == targetPosition.getX() && sourcePosition.getY() == targetPosition.getY())
        {
            return true;
        }

        if (!sourcePosition.isInScene() || !targetPosition.isInScene())
        {
            return false;
        }

        RSCollisionMap[] collisionMap = Game.getClient().getCollisionMaps();

        if (collisionMap == null)
        {
            return false;
        }

        int[][] flags = collisionMap[source.getFloorLevel()].getFlags();

        int dx = targetPosition.getX() - sourcePosition.getX();
        int dy = targetPosition.getY() - sourcePosition.getY();
        int dxAbs = Math.abs(dx);
        int dyAbs = Math.abs(dy);

        int xFlags = CollisionFlags.SOLID;
        int yFlags = CollisionFlags.SOLID;

        if (dx < 0)
        {
            xFlags |= CollisionFlags.FOV_EAST;
        }
        else
        {
            xFlags |= CollisionFlags.FOV_WEST;
        }

        if (dy < 0)
        {
            yFlags |= CollisionFlags.FOV_NORTH;
        }
        else
        {
            yFlags |= CollisionFlags.FOV_SOUTH;
        }

        if (dxAbs > dyAbs)
        {
            int x = sourcePosition.getX();

            int yBig = sourcePosition.getY() << 16; // The y position is represented as a bigger number to handle rounding

            int slope = (dy << 16) / dxAbs;

            yBig += 0x8000; // Add half of a tile

            if (dy < 0)
            {
                yBig--; // For correct rounding
            }

            int direction = dx < 0 ? -1 : 1;

            while (x != targetPosition.getX())
            {
                x += direction;
                int y = yBig >>> 16;

                if ((flags[x][y] & xFlags) != 0)
                {
                    // Collision while traveling on the x axis
                    return false;
                }

                yBig += slope;
                int nextY = yBig >>> 16;

                if (nextY != y && (flags[x][nextY] & yFlags) != 0)
                {
                    // Collision while traveling on the y axis
                    return false;
                }
            }
        }
        else // if (dxAbs < dyAbs)
        {
            int y = sourcePosition.getY();

            int xBig = sourcePosition.getX() << 16; // The x position is represented as a bigger number to handle rounding

            int slope = (dx << 16) / dyAbs;

            xBig += 0x8000; // Add half of a tile

            if (dx < 0)
            {
                xBig--; // For correct rounding
            }

            int direction = dy < 0 ? -1 : 1;

            while (y != targetPosition.getY())
            {
                y += direction;
                int x = xBig >>> 16;
                if ((flags[x][y] & yFlags) != 0)
                {
                    // Collision while traveling on the y axis
                    return false;
                }

                xBig += slope;

                int nextX = xBig >>> 16;

                if (nextX != x && (flags[nextX][y] & xFlags) != 0)
                {
                    // Collision while traveling on the x axis
                    return false;
                }
            }
        }
        /* Not sure why this was placed here
        else
        {
            // adjacent diagonal
            int x = sourcePosition.getX();
            int y = sourcePosition.getY();
            int directionX = Integer.compare(targetPosition.getX(), sourcePosition.getX());
            int directionY = Integer.compare(targetPosition.getY(), sourcePosition.getY());

            while (x != targetPosition.getX() || y != targetPosition.getY())
            {
                int xNext = x + directionX;
                int yNext = y + directionY;

                // Check for collisions
                if ((flags[x][y] & xFlags) != 0 || (flags[xNext][yNext] & yFlags) != 0)
                {
                    return false;
                }

                x = xNext;
                y = yNext;
            }
        }
        */

        // No collision
        return true;
    }

    public static class NpcPositionDistance
    {
        public Position position;
        public int distance; // chebyshev

        public NpcPositionDistance(Position position, int distance)
        {
            this.position = position;
            this.distance = distance;
        }
    }

    public static NpcPositionDistance GetNpcPositionDistance(Position source, Npc npc)
    {
        if (source == null || npc == null)
            return null;

        Position closestPosition = null;
        double lowestDistance = Double.MAX_VALUE;

        for (Position position : npc.getArea().getTiles())
        {
            double distance = source.distance(Distance.EUCLIDEAN, position);

            if (distance < lowestDistance)
            {
                closestPosition = position;
                lowestDistance = distance;
            }
        }

        return new NpcPositionDistance(closestPosition, (int)lowestDistance);
    }

    public static double GetDistance(Position source, Npc npc)
    {
        double lowest = Double.MAX_VALUE;

        if (source == null || npc == null)
            return lowest;

        Position npcPosition = npc.getPosition();

        if (npcPosition == null)
            return lowest;

        int x = npcPosition.getX();
        int y = npcPosition.getY();
        int width = npc.getEntityPositionWidth();
        int height = npc.getEntityPositionHeight();

        Position[] threeCorners = new Position[]{new Position(x + width, y), new Position(x + width, y + height), new Position(x, y + height)};

        lowest = source.distance(Distance.CHEBYSHEV, npcPosition); // South west

        for (Position corner : threeCorners)
        {
            double distance = source.distance(Distance.CHEBYSHEV, corner);

            if (distance < lowest)
            {
                lowest = distance;
            }
        }

        return lowest;
    }

    public static boolean CanAttack(Npc npc, int weaponRange)
    {
        if (npc == null)
            return false;

        Player localPlayer = Players.self();

        if (localPlayer == null)
            return false;

        Position localPosition = localPlayer.getPosition();

        if (localPosition == null)
            return false;

        NpcPositionDistance npcPositionDistance = GetNpcPositionDistance(localPosition, npc);

        return isInFieldOfView(localPosition, npcPositionDistance.position) && npcPositionDistance.distance <= weaponRange;
    }

    private static Position GetClosestPosition(Position source, Area destination)
    {
        double lowestDistance = Double.MAX_VALUE;
        Position closestPosition = null;

        for (Position position : destination.getTiles())
        {
            double distance = source.distance(Distance.CHEBYSHEV, position); // TODO verify

            if (distance < lowestDistance)
            {
                lowestDistance = distance;
                closestPosition = position;
            }
        }

        return closestPosition;
    }

}