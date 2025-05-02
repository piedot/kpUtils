package utils;

import jag.game.scene.RSCollisionMap;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.*;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.tdi.Tab;
import org.rspeer.game.component.tdi.Tabs;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.movement.pathfinding.util.CollisionFlags;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Players;

import java.util.*;

public class kpUtils
{
    // Tiles

    public static Area GetAreaFromTile(Position tile, int size)
    {
        int x = tile.getX();
        int y = tile.getY();
        return Area.rectangular(x - size, y - size, x + size, y + size, tile.getFloorLevel());
    }

    public static double DistanceTo(Area area, Position destination)
    {
        return DistanceTo(area.getTiles(), destination);
    }

    public static double DistanceTo(Collection<Position> positions, Position destination)
    {
        double lowest = Double.MAX_VALUE;

        for (Position position : positions)
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
     * Does not add the floor level together, instead it returns the floor level of the first position.
     * Can be used for subtraction
     */
    public static Position Add(Position p1, Position p2)
    {
        return new Position(p1.getX() + p2.getX(), p1.getY() + p2.getY(), p1.getFloorLevel());
    }

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
            returnTiles.add(new Position(base.getX() + offset.getX(), base.getY() + offset.getY(), base.getFloorLevel()));
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
            case TYPE_24: // POWERED_STAFF
            case TYPE_23: // ? POWERED_WAND ?
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
            case TYPE_25: // BANNER
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
    public static boolean IsInFieldOfView(SceneNode source, SceneNode target)
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

    public static class AreaPositionDistance
    {
        public Position position;
        public double distance; // chebyshev

        public AreaPositionDistance(Position position, double distance)
        {
            this.position = position;
            this.distance = distance;
        }
    }

    public static AreaPositionDistance GetAreaPositionDistance(Position source, Collection<Position> area)
    {
        Position closestPosition = null;
        double lowestDistance = Double.MAX_VALUE;

        for (Position position : area)
        {
            double distance = source.distance(Distance.EUCLIDEAN, position);

            if (distance < lowestDistance)
            {
                closestPosition = position;
                lowestDistance = distance;
            }
        }

        return new AreaPositionDistance(closestPosition, source.distance(Distance.CHEBYSHEV, closestPosition));
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

    public static boolean CanAttack(Collection<Position> sourceAttackPositions, Collection<Position> targetAttackPositions, int weaponRange)
    {
        for (Position sourceAttackPosition : sourceAttackPositions)
        {
            AreaPositionDistance npcAreaPositionDistance = GetAreaPositionDistance(sourceAttackPosition, targetAttackPositions);

            if (IsInFieldOfView(sourceAttackPosition, npcAreaPositionDistance.position) && npcAreaPositionDistance.distance <= weaponRange)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean CanAttack(Position source, Npc npc, int weaponRange)
    {
        if (npc == null)
            return false;

        return CanAttack(Arrays.asList(source), npc.getArea().getTiles(), weaponRange);
    }

    public static Position GetClosestPosition(Position source, Area destination)
    {
        double lowestDistance = Double.MAX_VALUE;
        Position closestPosition = null;

        for (Position position : destination.getTiles())
        {
            double distance = source.distance(Distance.EUCLIDEAN, position); // TODO verify

            if (distance < lowestDistance)
            {
                lowestDistance = distance;
                closestPosition = position;
            }
        }

        return closestPosition;
    }

    public static Area GetAreaFrom(Position position, int width, int height)
    {
        return Area.rectangular(position, new Position(position.getX() + width, position.getY() + height));
    }

    public static List<Position> GetMeleeTiles(Position entityPosition, int entityWidth, int entityHeight)
    {
        int entityPositionX = entityPosition.getX();
        int entityPositionY = entityPosition.getY();
        int entityPositionLevel = entityPosition.getFloorLevel();

        List<Position> meleeTiles = new ArrayList<>();

        for (int x = 0; x < entityWidth; x++)
        {
            int entityX = entityPositionX + x;

            meleeTiles.add(new Position(entityX, entityPositionY - 1, entityPositionLevel));
            meleeTiles.add(new Position(entityX, entityPositionY + entityHeight, entityPositionLevel));
        }

        for (int y = 0; y < entityHeight; y++)
        {
            int entityY = entityPositionY + y;

            meleeTiles.add(new Position(entityPositionX - 1, entityY, entityPositionLevel));
            meleeTiles.add(new Position(entityPositionX + entityWidth, entityY, entityPositionLevel));
        }

        return meleeTiles;
    }

    // Tries to guess if we are interacting with a scene object
    public static boolean IsInteractingWith(SceneObject sceneObject)
    {
        if (sceneObject == null)
            return false;

        int width = sceneObject.getEntityPositionWidth();
        int height = sceneObject.getEntityPositionHeight();

        Player localPlayer = Players.self();

        if (localPlayer == null)
            return false;

        if (!localPlayer.isMoving()) // Moving check
            return false;

        Position localPosition = localPlayer.getPosition();

        if (localPosition == null)
            return false;

        List<Position> meleeTiles = GetMeleeTiles(sceneObject.getPosition(), width, height);

        for (Position position : meleeTiles) // You can only interact with an object from its "melee tiles"
        {
            if (position.equals(localPosition) || position.equals(Movement.getDestination()))
            {
                Log.info("Interacting with object " + sceneObject.getName());
                return true;
            }
        }

        return false;
    }

    // Tries to guess if we are interacting with a pickable
    public static boolean IsInteractingWith(Pickable pickable)
    {
        if (pickable == null)
            return false;

        Player localPlayer = Players.self();

        if (localPlayer == null)
            return false;

        if (!localPlayer.isMoving()) // Moving check
            return false;

        Position localPosition = localPlayer.getPosition();

        if (localPosition == null)
            return false;

        Position pickablePosition = pickable.getPosition();

        if (pickablePosition.equals(Movement.getDestination()))
            return true;

        return false;
    }

    /**
     * Same formatting as OSRS
     */
    public static String FormatOSRSValue(long value)
    {
        if (value >= 10_000_000) // 10M
        {
            return (value / 1000000) + "M";
        }
        else if (value >= 10_000) // 10K
        {
            return (value / 1000) + "K";
        }
        else
        {
            return String.valueOf(value);
        }
    }

    public static String FormatValueForMillions(long value)
    {
        double millions = value / 1000000.0;
        return String.format("%.2fM", millions);
    }


}