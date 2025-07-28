package utils;

import Bosses.Misc.Weapons;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.VarComposite;
import org.rspeer.game.Vars;
import org.rspeer.game.adapter.component.inventory.Equipment;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;

import java.util.HashMap;
import java.util.Map;

public class kpWeapons
{
    private static class WeaponStyle
    {
        private final int attackIndex;
        private final int strengthIndex;
        private final int defenseIndex;
        private final int attackVar;
        private final int strengthVar;
        private final int defenseVar;

        public WeaponStyle(int attackIndex, int strengthIndex, int defenseIndex, int attackVar, int strengthVar, int defenceVar)
        {
            this.attackIndex = attackIndex;
            this.strengthIndex = strengthIndex;
            this.defenseIndex = defenseIndex;
            this.attackVar = attackVar;
            this.strengthVar = strengthVar;
            this.defenseVar = defenceVar;
        }

        public int getAttackIndex()
        {
            return attackIndex;
        }

        public int getStrengthIndex()
        {
            return strengthIndex;
        }

        public int getDefenseIndex()
        {
            return defenseIndex;
        }

        public int getAttackVar()
        {
            return attackVar;
        }

        public int getStrengthVar()
        {
            return strengthVar;
        }

        public int getDefenseVar()
        {
            return defenseVar;
        }
    }

    // For "Slash"
    private static final Map<String, WeaponStyle> weaponAttackStyleMap = new HashMap<>();

    static
    {
        // Main vardorvis weapons
        weaponAttackStyleMap.put("Soulreaper axe",          new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Scythe of vitur",         new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Blade of saeldor",        new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Noxious halberd",         new WeaponStyle(1, 1, 1, 1, 1, 1)); // Only slash option
        weaponAttackStyleMap.put("Abyssal tentacle",        new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Abyssal whip",            new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Zombie axe",              new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Dragon scimitar",         new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Rune scimitar",           new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Leaf-bladed battleaxe",   new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Saradomin sword",         new WeaponStyle(0, 1, 3, 0, 1, 3));
        // Spec
        weaponAttackStyleMap.put("Dragon claws",            new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Burning claws",           new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Voidwaker",               new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Ancient godsword",        new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Saradomin godsword",      new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Ursine chainmace",        new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Abyssal dagger",          new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Abyssal dagger(p)",       new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Abyssal dagger(p+)",      new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Abyssal dagger(p++)",     new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Dragon dagger",           new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Dragon dagger(p)",        new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Dragon dagger(p+)",       new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Dragon dagger(p++)",      new WeaponStyle(0, 1, 3, 0, 1, 3));
        weaponAttackStyleMap.put("Arkan blade",             new WeaponStyle(0, 1, 3, 0, 1, 3));
    }

    public static void HandleAttackStyles()
    {
        Item currentWeapon = Inventories.equipment().getItemAt(Equipment.Slot.MAINHAND);

        if (currentWeapon == null)
            return;

        String weaponName = currentWeapon.getName();

        WeaponStyle weaponStyle = weaponAttackStyleMap.get(weaponName);

        if (weaponStyle == null)
        {
            Log.debug(weaponName + " is not supported for smart leveling");
            return;
        }

        int indexToUse;
        int varToUse;

        // Is it smarter to level each one at like 5 levels each or something?
        // Could also account for level 82 for fang, but fang is not viable at vardorvis anymore so just get 99 strength first
        if (Skills.getLevel(Skill.STRENGTH) < 99)
        {
            indexToUse = weaponStyle.getStrengthIndex();
            varToUse = weaponStyle.getStrengthVar();
        }
        else if (Skills.getLevel(Skill.ATTACK) < 99)
        {
            indexToUse = weaponStyle.getAttackIndex();
            varToUse = weaponStyle.getAttackVar();
        }
        else if (Skills.getLevel(Skill.DEFENCE) < 99)
        {
            indexToUse = weaponStyle.getDefenseIndex();
            varToUse = weaponStyle.getDefenseVar();
        }
        else
        {
            indexToUse = weaponStyle.getStrengthIndex();
            varToUse = weaponStyle.getStrengthVar();
        }

        int attackStyleVarBit = Vars.get(VarComposite.ATTACK_STYLE);
        Log.debug("Attack styles: VarBit  - " + attackStyleVarBit + " Name - " + Combat.getSelectedStyle() + " ordinal (useless) - " + Combat.getSelectedStyle().ordinal());

        if (attackStyleVarBit != varToUse)
        {
            Log.info("Selecting style index " + indexToUse + " var " + varToUse);
            Combat.select(indexToUse);
        }
    }

    public static void HandleAutoRetaliate()
    {
        if (Combat.isAutoRetaliateOn())
        {
            Log.info("Disabling auto retaliate");
            Combat.toggleAutoRetaliate(false);
        }
    }
}
