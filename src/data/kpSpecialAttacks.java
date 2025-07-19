package data;

import org.rspeer.game.Definitions;
import org.rspeer.game.adapter.definition.ItemDefinition;
import org.rspeer.game.combat.Combat;

import java.util.HashMap;
import java.util.Map;

public class kpSpecialAttacks
{
    // Weapon name to special attack energy cost
    private static Map<String, Integer> SPECIAL_ATTACK_COSTS = new HashMap<>();

    // Thank you to the OSRS Wiki (https://oldschool.runescape.wiki/w/Special_attacks)

    static
    {
        SPECIAL_ATTACK_COSTS.put("Dragon axe", 100);
        SPECIAL_ATTACK_COSTS.put("Infernal axe", 100);
        SPECIAL_ATTACK_COSTS.put("3rd age axe", 100);
        SPECIAL_ATTACK_COSTS.put("Crystal axe", 100);
        SPECIAL_ATTACK_COSTS.put("Dragon felling axe", 100);
        SPECIAL_ATTACK_COSTS.put("3rd age felling axe", 100);
        SPECIAL_ATTACK_COSTS.put("Crystal felling axe", 100);

        SPECIAL_ATTACK_COSTS.put("Dragon harpoon", 100);
        SPECIAL_ATTACK_COSTS.put("Infernal harpoon", 100);
        SPECIAL_ATTACK_COSTS.put("Crystal harpoon", 100);

        SPECIAL_ATTACK_COSTS.put("Dragon pickaxe", 100);
        SPECIAL_ATTACK_COSTS.put("Infernal pickaxe", 100);
        SPECIAL_ATTACK_COSTS.put("3rd age pickaxe", 100);
        SPECIAL_ATTACK_COSTS.put("Crystal pickaxe", 100);

        SPECIAL_ATTACK_COSTS.put("Ancient godsword", 50);

        SPECIAL_ATTACK_COSTS.put("Eldritch nightmare staff", 55);

        SPECIAL_ATTACK_COSTS.put("Keris partisan of the sun", 75);

        SPECIAL_ATTACK_COSTS.put("Purging staff", 25);

        SPECIAL_ATTACK_COSTS.put("Toxic blowpipe", 50);

        SPECIAL_ATTACK_COSTS.put("Saradomin godsword", 50);

        SPECIAL_ATTACK_COSTS.put("Brine sabre", 75);

        SPECIAL_ATTACK_COSTS.put("Dragon battleaxe", 100);

        SPECIAL_ATTACK_COSTS.put("Excalibur", 100);

        SPECIAL_ATTACK_COSTS.put("Dinh's bulwark", 50);

        SPECIAL_ATTACK_COSTS.put("Dragon crossbow", 60);

        SPECIAL_ATTACK_COSTS.put("Dragon halberd", 30);
        SPECIAL_ATTACK_COSTS.put("Crystal halberd", 30);

        SPECIAL_ATTACK_COSTS.put("Dragon 2h sword", 60);

        SPECIAL_ATTACK_COSTS.put("Rune thrownaxe", 50); // 10% for each hit; up to 50%

        SPECIAL_ATTACK_COSTS.put("Vesta's spear", 50);

        SPECIAL_ATTACK_COSTS.put("Abyssal whip", 50);

        SPECIAL_ATTACK_COSTS.put("Accursed sceptre", 50);

        SPECIAL_ATTACK_COSTS.put("Ancient mace", 100);

        SPECIAL_ATTACK_COSTS.put("Bandos godsword", 50);

        SPECIAL_ATTACK_COSTS.put("Barrelchest anchor", 50);

        SPECIAL_ATTACK_COSTS.put("Bone dagger", 75);
        SPECIAL_ATTACK_COSTS.put("Bone dagger (p)", 75);
        SPECIAL_ATTACK_COSTS.put("Bone dagger (p+)", 75);
        SPECIAL_ATTACK_COSTS.put("Bone dagger (p++)", 75);

        SPECIAL_ATTACK_COSTS.put("Darklight", 50);
        SPECIAL_ATTACK_COSTS.put("Arclight", 50);
        SPECIAL_ATTACK_COSTS.put("Emberlight", 50);

        SPECIAL_ATTACK_COSTS.put("Dorgeshuun crossbow", 75);

        SPECIAL_ATTACK_COSTS.put("Dragon scimitar", 55);

        SPECIAL_ATTACK_COSTS.put("Dragon warhammer", 50);

        SPECIAL_ATTACK_COSTS.put("Statius's warhammer", 35);

        SPECIAL_ATTACK_COSTS.put("Elder maul", 50);

        SPECIAL_ATTACK_COSTS.put("Morrigan's throwing axe", 50);

        SPECIAL_ATTACK_COSTS.put("Seercull", 100);

        SPECIAL_ATTACK_COSTS.put("Staff of the dead", 100);
        SPECIAL_ATTACK_COSTS.put("Toxic staff of the dead", 100);
        SPECIAL_ATTACK_COSTS.put("Staff of light", 100);
        SPECIAL_ATTACK_COSTS.put("Staff of balance", 100);

        SPECIAL_ATTACK_COSTS.put("Tonalztics of ralos", 50);

        SPECIAL_ATTACK_COSTS.put("Abyssal bludgeon", 50);

        SPECIAL_ATTACK_COSTS.put("Armadyl crossbow", 50);

        SPECIAL_ATTACK_COSTS.put("Armadyl godsword", 50);

        SPECIAL_ATTACK_COSTS.put("Blue moon spear", 50);

        SPECIAL_ATTACK_COSTS.put("Dawnbringer", 35);

        SPECIAL_ATTACK_COSTS.put("Dragon hasta", 5); // Boosts the next hit's accuracy and damage based on how much energy the user has available.

        SPECIAL_ATTACK_COSTS.put("Dragon longsword", 25);

        SPECIAL_ATTACK_COSTS.put("Dragon mace", 25);

        SPECIAL_ATTACK_COSTS.put("Dragon sword", 40);

        SPECIAL_ATTACK_COSTS.put("Dragon thrownaxe", 25);

        SPECIAL_ATTACK_COSTS.put("Dual macuahuitl", 25);

        SPECIAL_ATTACK_COSTS.put("Eclipse atlatl", 50);

        SPECIAL_ATTACK_COSTS.put("Granite hammer", 60);

        SPECIAL_ATTACK_COSTS.put("Keris partisan of corruption", 75);

        SPECIAL_ATTACK_COSTS.put("Light ballista", 65);
        SPECIAL_ATTACK_COSTS.put("Heavy ballista", 65);

        SPECIAL_ATTACK_COSTS.put("Magic longbow", 35);
        SPECIAL_ATTACK_COSTS.put("Magic comp bow", 35);

        SPECIAL_ATTACK_COSTS.put("Noxious halberd", 50);

        SPECIAL_ATTACK_COSTS.put("Osmumten's fang", 25);

        SPECIAL_ATTACK_COSTS.put("Rune claws", 25);

        SPECIAL_ATTACK_COSTS.put("Saradomin's blessed sword", 65);

        SPECIAL_ATTACK_COSTS.put("Soulflame horn", 25);

        SPECIAL_ATTACK_COSTS.put("Vesta's longsword", 25);
        SPECIAL_ATTACK_COSTS.put("Vesta's blighted longsword", 25);

        SPECIAL_ATTACK_COSTS.put("Voidwaker", 50);

        SPECIAL_ATTACK_COSTS.put("Volatile nightmare staff", 55);

        SPECIAL_ATTACK_COSTS.put("Zaryte crossbow", 75);

        SPECIAL_ATTACK_COSTS.put("Abyssal dagger", 25);
        SPECIAL_ATTACK_COSTS.put("Abyssal dagger (p)", 25);
        SPECIAL_ATTACK_COSTS.put("Abyssal dagger (p+)", 25);
        SPECIAL_ATTACK_COSTS.put("Abyssal dagger (p++)", 25);

        SPECIAL_ATTACK_COSTS.put("Burning claws", 30);

        SPECIAL_ATTACK_COSTS.put("Dragon claws", 50);

        SPECIAL_ATTACK_COSTS.put("Dragon dagger", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon dagger (p)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon dagger (p+)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon dagger (p++)", 25);

        SPECIAL_ATTACK_COSTS.put("Dragon knife", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon knife (p)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon knife (p+)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon knife (p++)", 25);

        SPECIAL_ATTACK_COSTS.put("Granite maul", 60); // 50 with an ornate handle

        SPECIAL_ATTACK_COSTS.put("Magic shortbow", 55);
        SPECIAL_ATTACK_COSTS.put("Magic shortbow (i)", 50);

        SPECIAL_ATTACK_COSTS.put("Saradomin sword", 100);

        SPECIAL_ATTACK_COSTS.put("Vesta's spear (bh)", 50);

        SPECIAL_ATTACK_COSTS.put("Webweaver bow", 50);

        SPECIAL_ATTACK_COSTS.put("Abyssal tentacle", 50);

        SPECIAL_ATTACK_COSTS.put("Dragon spear", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon spear (p)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon spear (p+)", 25);
        SPECIAL_ATTACK_COSTS.put("Dragon spear (p++)", 25);

        SPECIAL_ATTACK_COSTS.put("Zamorakian hasta", 25);
        SPECIAL_ATTACK_COSTS.put("Zamorakian spear", 25);

        SPECIAL_ATTACK_COSTS.put("Rod of Ivandis", 10);
        SPECIAL_ATTACK_COSTS.put("Ivandis flail", 10);
        SPECIAL_ATTACK_COSTS.put("Blisterwood flail", 10);

        SPECIAL_ATTACK_COSTS.put("Scorching bow", 25);

        SPECIAL_ATTACK_COSTS.put("Ursine chainmace", 50);

        SPECIAL_ATTACK_COSTS.put("Zamorak godsword", 50);

        SPECIAL_ATTACK_COSTS.put("Ancient wyvern shield", 0); // None; usable once every two minutes
        SPECIAL_ATTACK_COSTS.put("Dragonfire shield", 0); // None; usable once every two minutes
        SPECIAL_ATTACK_COSTS.put("Dragonfire ward", 0); // None; usable once every two minutes

        SPECIAL_ATTACK_COSTS.put("Soulreaper axe", 0); // None, consumes Soul Stacks instead

        // Leagues items
        /*
        SPECIAL_ATTACK_COSTS.put("Trailblazer axe", 100);
        SPECIAL_ATTACK_COSTS.put("Echo axe", 100);

        SPECIAL_ATTACK_COSTS.put("Trailblazer harpoon", 100);
        SPECIAL_ATTACK_COSTS.put("Echo harpoon", 100);

        SPECIAL_ATTACK_COSTS.put("Trailblazer pickaxe", 100);
        SPECIAL_ATTACK_COSTS.put("Echo pickaxe", 100);

        SPECIAL_ATTACK_COSTS.put("Crystal dagger (perfected)", 25);
        SPECIAL_ATTACK_COSTS.put("The dogsword", 50);
        SPECIAL_ATTACK_COSTS.put("Sunlight spear", 100); // None, consumes 7 Sunlight Stacks instead
        SPECIAL_ATTACK_COSTS.put("Thunder khopesh", 50);
        */
    }

    public static int GetSpecialAttackCost(String itemName)
    {
        return SPECIAL_ATTACK_COSTS.getOrDefault(itemName, -1);
    }
}
