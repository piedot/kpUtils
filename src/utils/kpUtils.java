package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.type.Interactable;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.InterfaceComposite;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.tdi.Tab;
import org.rspeer.game.component.tdi.Tabs;
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

    // Interacting

    public static void Interact(Item item, String action)
    {
        OpenTabIfNeeded(Tab.INVENTORY);

        item.interact(action);
    }

    public static void Interact(Interactable interactable, String action)
    {
        interactable.interact(action);

        return;
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

    public static void StopScript()
    {

    }

}