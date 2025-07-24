package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.kpAttackAnimations;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Equipment;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.scene.Players;

import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class kpGameData
{
    private static int wastedTicks = 0;
    private static int canAttackIn = 0; //
    private static boolean resetAttack = false; // We are going to interrupt the attack this tick, so re-attack now

    public static class AttackRange
    {
        private final int accurate;
        private final int rapid;
        private final int longRange;

        public AttackRange(int range)
        {
            this.accurate = range;
            this.rapid = range;
            this.longRange = range;
        }

        public AttackRange(int accurate, int rapid, int longRange)
        {
            this.accurate = accurate;
            this.rapid = rapid;
            this.longRange = longRange;
        }

        public int getRange()
        {
            return accurate; // For melee/magic attacks
        }

        public int getLongRange()
        {
            return longRange; // For ranged and magic attacks
        }

        public int getRapid()
        {
            return rapid; // For ranged attacks
        }

        public int getAccurate()
        {
            return accurate; // For ranged attacks
        }
    }

    public static class AttackSpeed
    {
        private final int accurate;
        private final int rapid;
        private final int longRange;

        public AttackSpeed(int speed)
        {
            this.accurate = speed;
            this.rapid = speed;
            this.longRange = speed;
        }

        public AttackSpeed(int accurate, int rapid, int longRange)
        {
            this.accurate = accurate;
            this.rapid = rapid;
            this.longRange = longRange;
        }

        public int getAccurate()
        {
            return accurate;
        }

        public int getRapid()
        {
            return rapid;
        }

        public int getLongRange()
        {
            return longRange;
        }
    }

    private static final String WEAPON_RANGES_JSON_FILE_NAME = "weapon_ranges.json";
    private static final String WEAPON_SPEEDS_JSON_FILE_NAME = "weapon_speeds.json";
    private static final Map<String, AttackRange> ATTACK_RANGES = new HashMap<>();
    private static final Map<String, AttackSpeed> ATTACK_SPEEDS = new HashMap<>();

    public static boolean Initialize()
    {
        // TODO Load all weapons including attack ranges json like before/split into load functions to fit into java bytecode limit, backed up to git on 18/07/2025
        ATTACK_SPEEDS.put("Soulreaper axe", new AttackSpeed(5));
        ATTACK_SPEEDS.put("of vitur", new AttackSpeed(5));
        ATTACK_SPEEDS.put("Blade of saeldor", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Noxious halberd", new AttackSpeed(5));
        ATTACK_SPEEDS.put("Abyssal tentacle", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Abyssal whip", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Zombie axe", new AttackSpeed(5));
        ATTACK_SPEEDS.put("Dragon scimitar", new AttackSpeed(4));

        ATTACK_SPEEDS.put("Dragon claws", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Burning claws", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Voidwaker", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Ancient godsword", new AttackSpeed(6));
        ATTACK_SPEEDS.put("Saradomin godsword", new AttackSpeed(6));
        ATTACK_SPEEDS.put("Ursine chainmace", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Abyssal dagger", new AttackSpeed(4));
        ATTACK_SPEEDS.put("Dragon dagger", new AttackSpeed(4));
        return true;
    }

    public static void Update()
    {
        canAttackIn--;

        return;
    }

    public static void Reset()
    {
        // Called at the end of OnTick
        resetAttack = false;
    }

    public static int GetAttackCooldown()
    {
        return canAttackIn;
    }

    public static boolean CanAttack()
    {
        return canAttackIn == 0;
    }

    public static void ResetAttack()
    {
        resetAttack = true;
    }

    public static boolean ShouldResetAttack()
    {
        return resetAttack;
    }

    public static void SetAttackCooldown(int ticks)
    {
        canAttackIn = ticks;
    }

    public static void AddToAttackCooldown(int ticks)
    {
        resetAttack = true; // Is there any time where we don't want to reset the attack?
        canAttackIn += ticks;
    }

    private static String storedWeaponName = "";

    /**
     * Sets the weapon name for us to use (Used for allowing knowledge of future weapon switches, next tick)
     * @param name the weapon name we are going to switch to next tick
     */
    public static void SetWeaponName(String name)
    {
        storedWeaponName = name;
    }

    public static void ResetWeaponName()
    {
        storedWeaponName = null;
    }

    private static String GetWeaponName()
    {
        if (storedWeaponName == null)
        {
            Item weapon = Inventories.equipment().getItemAt(Equipment.Slot.MAINHAND);
            if (weapon == null)
            {
                Log.warn("No weapon found in main hand, using unarmed");
                return "Unarmed"; // Fallback
            }
            return weapon.getName();
        }
        return storedWeaponName;
    }

    public static int GetWastedTicks()
    {
        return wastedTicks;
    }

    public static void UpdateWastedTicks()
    {
        if (canAttackIn < 0)
        {
            wastedTicks++;
        }
    }

    public static void ResetWastedTicks()
    {
        wastedTicks = 0;
    }

    public static int GetHitDelay(double distance)
    {
        int intDistance = (int)distance;
        return GetHitDelayBowsAndCrossBows(intDistance); // TODO if needed
    }

    // Hit delay (travel)
    /*
    The hit delay of distance attacks is typically variable based on the Chebyshev distance between the entity dealing the attack
     and the entity receiving the attack. The distance is typically measured edge-to-edge in game squares,
     using the same edge for both entities. I.e. distance will be calculated using an NPC's closest edge to the player,
     and the player's furthest edge from the NPC. However,
     barrage spells are a notable exception in that they calculate distance from the player to an NPC's south-west tile,
     which causes abnormally long hit delay when attacking a large NPC from the north or east.
     */

    public static int GetHitDelayBowsAndCrossBows(int distance)
    {
        return 1 + ((3 + distance) / 6);
    }

    // Dark bow second projectile is different, but who cares

    public static int GetHitDelayThrown(int distance)
    {
        return 1 + (distance / 6);
    }

    // Ballistae are different
    // Tonalztics of Ralos have a fixed 2 tick delay

    // The

    public static int GetHitDelayMagic(int distance)
    {
        return 1 + ((1 + distance) / 3);
    }

    // Attacks with Tumeken's shadow are always 1 tick slower than the magic calculation above

    public static int GetHitDelayWithTumekensShadow(int distance)
    {
        return 2 + ((1 + distance) / 3);
    }

    // However, grasp and demonbane spells from the Arceuus spellbook, as well as the special attacks of the volatile and eldritch nightmare staves,
    // have a hit delay of 2 ticks regardless of the distance between the caster and target.

    public static class WeaponInfo
    {
        private final String name;
        private final int attackRange;
        private final int attackSpeed;

        public WeaponInfo(String name, int attackRange, int attackSpeed)
        {
            this.name = name;
            this.attackRange = attackRange;
            this.attackSpeed = attackSpeed;
        }

        public String getName()
        {
            return name;
        }

        public int getAttackRange()
        {
            return attackRange;
        }

        public int getAttackSpeed()
        {
            return attackSpeed;
        }
    }

    public static int GetAttackRange()
    {
        String weaponName = GetWeaponName();
        AttackRange range = ATTACK_RANGES.get(weaponName);
        if (range == null)
        {
            Log.warn("No attack range found for weapon: " + weaponName);
            return 1; // Melee TODO
        }
        return range.getRange();
    }

    public static int GetAttackSpeed()
    {
        String weaponName = GetWeaponName();
        AttackSpeed speed = ATTACK_SPEEDS.get(weaponName);
        if (speed == null)
        {
            Log.severe("No attack speed found for weapon: " + weaponName);
            return 4; // Fallback
        }
        return speed.getRapid();
    }

    public static void OnPlayerAnimation(Player player, int animationId)
    {
        if (player == null || !player.equals(Players.self()))
        {
            return;
        }

        if (kpAttackAnimations.getIds().contains(animationId))
        {
            kpGameData.SetAttackCooldown(kpGameData.GetAttackSpeed());
        }
    }

    public static void Paint(Graphics2D g2d)
    {
        int i = 0;
        kpPaint.DrawString(g2d, "Attack Cooldown: " + canAttackIn,              500, 16 + 16 * i, Color.WHITE); i++;
        //kpPaint.DrawString(g2d, "Weapon Name: " + GetWeaponName(),              500, 16 + 16 * i, Color.WHITE); i++;
        //kpPaint.DrawString(g2d, "Attack Range: " + GetAttackRange(),            500, 16 + 16 * i, Color.WHITE); i++;
        //kpPaint.DrawString(g2d, "Attack Speed: " + GetAttackSpeed(),            500, 16 + 16 * i, Color.WHITE); i++;
        kpPaint.DrawString(g2d, "Can Attack: " + CanAttack(),                   500, 16 + 16 * i, Color.WHITE); i++;
        kpPaint.DrawString(g2d, "Should Reset Attack: " + ShouldResetAttack(),  500, 16 + 16 * i, Color.WHITE); i++;
    }
}
