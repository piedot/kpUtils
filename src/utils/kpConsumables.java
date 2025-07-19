package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class kpConsumables
{
    public static class Consumable
    {
        public enum TYPE
        {
            HARD_FOOD,
            FAST_FOOD,
            POTION_FOOD,
            LATE_RESTORE_FOOD, // The new hunter food
            PRAYER_POTION,
            RESTORE_POTION,
            POISON_POTION,

            ATTACK_POTION,
            STRENGTH_POTION,
            DEFENCE_POTION,
            RANGED_POTION,
            MAGIC_POTION,
        }

        List<TYPE> types;
        private int effectAmount;
        private Callable<Integer> effectFormula;
        private boolean overheal;

        public Consumable(List<TYPE> types)
        {
            this.types = types;
            this.effectAmount = -1;
            this.effectFormula = null;
            overheal = false;
        }

        public Consumable(List<TYPE> types, int effectAmount)
        {
            this.types = types;
            this.effectAmount = effectAmount;
            this.effectFormula = null;
            overheal = false;
        }

        public Consumable(List<TYPE> types, Callable<Integer> effectFormula)
        {
            this.types = types;
            this.effectAmount = -1;
            this.effectFormula = effectFormula;
            overheal = false;
        }

        public Consumable(List<TYPE> types, Callable<Integer> effectFormula, boolean overheal)
        {
            this.types = types;
            this.effectAmount = -1;
            this.effectFormula = effectFormula;
            this.overheal = overheal;
        }

        public List<TYPE> getTypes()
        {
            return types;
        }

        public String getAction()
        {
            return types.contains(TYPE.HARD_FOOD) || types.contains(TYPE.FAST_FOOD) || types.contains(TYPE.LATE_RESTORE_FOOD) ? "Eat" : "Drink";
        }

        public int getEffectAmount()
        {
            if (effectAmount == -1 && effectFormula != null)
            {
                try
                {
                    return effectFormula.call();
                }
                catch (Exception exception)
                {
                    Log.severe("Healing formula caused an exception - " + exception.getMessage());
                }
            }

            return effectAmount;
        }

        public boolean DoesOverheal()
        {
            return overheal;
        }

    }

    private static Integer AnglerfishFormula()
    {
        int hp = Skills.getLevel(Skill.HITPOINTS);
        return (int) (Math.floor(hp / 10.0) + 2 * Math.floor(hp / 25.0) + 5 * Math.floor(hp / 93.0) + 2);
    }

    private static Integer StrawberryFormula()
    {
        int hp = Skills.getLevel(Skill.HITPOINTS);
        return (int) (1.0 + hp * 0.06);
    }

    private static Integer SaradominBrewFormula()
    {
        int hp = Skills.getLevel(Skill.HITPOINTS);
        return (int) (hp * 15.0 / 100.0 + 2.0);
    }

    private static Integer PrayerPotionFormula()
    {
        int prayer = Skills.getLevel(Skill.PRAYER);
        return (int) (prayer / 4.0 + 7.0);
    }

    private static Integer SuperRestoreFormula()
    {
        int prayer = Skills.getLevel(Skill.PRAYER);

        return (int) (prayer / 4.0 + 8.0);
    }

    private static Integer SanfewSerumFormula()
    {
        int prayer = Skills.getLevel(Skill.PRAYER);
        return (int) (prayer * 3.0 / 10.0 + 4.0);
    }

    private static Integer AttackPotionFormula()
    {
        int attack = Skills.getLevel(Skill.ATTACK);
        return (int)(3 + Math.floor(attack * 0.10));
    }

    private static Integer StrengthPotionFormula()
    {
        int str = Skills.getLevel(Skill.STRENGTH);
        return (int)(3 + Math.floor(str * 0.10));
    }

    private static Integer DefencePotionFormula()
    {
        int def = Skills.getLevel(Skill.DEFENCE);
        return (int)(3 + Math.floor(def * 0.10));
    }

    private static Integer SuperAttackFormula()
    {
        int attack = Skills.getLevel(Skill.ATTACK);
        return (int)(5 + Math.floor(attack * 0.15));
    }

    private static Integer SuperStrengthFormula()
    {
        int str = Skills.getLevel(Skill.STRENGTH);
        return (int)(5 + Math.floor(str * 0.15));
    }

    private static Integer SuperDefenceFormula()
    {
        int def = Skills.getLevel(Skill.DEFENCE);
        return (int)(5 + Math.floor(def * 0.15));
    }

    private static Integer CombatPotionAttackFormula()
    {
        int attack = Skills.getLevel(Skill.ATTACK);
        return (int)(3 + Math.floor(attack * 0.10));
    }

    private static Integer CombatPotionStrengthFormula()
    {
        int str = Skills.getLevel(Skill.STRENGTH);
        return (int)(3 + Math.floor(str * 0.10));
    }

    private static Integer SuperCombatPotionAttackFormula()
    {
        int attack = Skills.getLevel(Skill.ATTACK);
        return (int)(5 + Math.floor(attack * 0.15));
    }
    private static Integer SuperCombatPotionStrengthFormula()
    {
        int str = Skills.getLevel(Skill.STRENGTH);
        return (int)(5 + Math.floor(str * 0.15));
    }
    private static Integer SuperCombatPotionDefenceFormula()
    {
        int def = Skills.getLevel(Skill.DEFENCE);
        return (int)(5 + Math.floor(def * 0.15));
    }

    private static Integer RangingPotionFormula()
    {
        int range = Skills.getLevel(Skill.RANGED);
        return (int)(4 + Math.floor(range * 0.10));
    }

    private static Integer MagicPotionFormula()
    {
        return 4;
    }

    private static Integer DivineMagicPotionFormula()
    {
        int magic = Skills.getLevel(Skill.MAGIC);
        return (int)(5 + Math.floor(magic * 0.15));
    }

    public static Map<String, Consumable> hardFood = new HashMap<>();
    public static Map<String, Consumable> potions = new HashMap<>();
    public static Map<String, Consumable> fastFood = new HashMap<>();

    static
    {
        // Hard food
        hardFood.put("Shrimps", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 3));
        hardFood.put("Cooked chicken", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 3));
        hardFood.put("Cooked meat", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 3));
        hardFood.put("Sardine", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 4));
        hardFood.put("Bread", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 5));
        hardFood.put("Herring", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 5));
        hardFood.put("Trout", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 7));
        hardFood.put("Pike", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 8));
        hardFood.put("Peach", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 8));
        hardFood.put("Salmon", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 9));
        hardFood.put("Tuna", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 10));
        hardFood.put("Redberry pie", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 5)); // Not supported
        hardFood.put("Jug of wine", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 11));
        hardFood.put("Stew", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 11));
        hardFood.put("Cake", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 4)); // Not supported
        hardFood.put("Meat pie", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 6)); // Not supported
        hardFood.put("Lobster", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 12));
        hardFood.put("Bass", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 13));
        hardFood.put("Plain pizza", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 7)); // Not supported
        hardFood.put("Swordfish", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 14));
        hardFood.put("Potato with butter", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 14));
        hardFood.put("Apple pie", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 7)); // Not supported
        hardFood.put("Chocolate cake", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 5)); // Not supported
        hardFood.put("Tangled toad's legs", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 15));
        hardFood.put("Chocolate bomb", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 15));
        hardFood.put("Potato with cheese", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 16));
        hardFood.put("Meat pizza", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 8)); // Not supported
        hardFood.put("Monkfish", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 16));
        hardFood.put("Anchovy pizza", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 9)); // Not supported
        hardFood.put("Curry", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 19));
        hardFood.put("Guthix rest", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 5));
        hardFood.put("Shark", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 20));
        hardFood.put("Sea turtle", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 21));
        hardFood.put("Pineapple pizza", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 11)); // Not supported
        hardFood.put("Summer pie", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 11)); // Not supported
        hardFood.put("Wild pie", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 11)); // Not supported
        hardFood.put("Manta ray", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 22));
        hardFood.put("Tuna potato", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 22));
        hardFood.put("Dark crab", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), 22));
        hardFood.put("Anglerfish", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), kpConsumables::AnglerfishFormula, true));
        hardFood.put("Strawberry", new Consumable(List.of(Consumable.TYPE.HARD_FOOD), kpConsumables::StrawberryFormula));

        hardFood.put("Cooked wild kebbit", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 4 + 4));
        hardFood.put("Cooked larupia", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 6 + 5));
        hardFood.put("Cooked barb-tailed kebbit", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 7 + 5));
        hardFood.put("Cooked graahk", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 8 + 6));
        hardFood.put("Cooked kyatt", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 9 + 8));
        hardFood.put("Cooked pyre fox", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 11 + 8));
        hardFood.put("Cooked sunlight antelope", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 12 + 9));
        hardFood.put("Cooked dashing kebbit", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 13 + 10));
        hardFood.put("Cooked moonlight antelope", new Consumable(List.of(Consumable.TYPE.LATE_RESTORE_FOOD), 14 + 12));

        // Potions
        potions.put("Saradomin brew", new Consumable(List.of(Consumable.TYPE.POTION_FOOD), kpConsumables::SaradominBrewFormula, true));
        potions.put("Prayer potion", new Consumable(List.of(Consumable.TYPE.PRAYER_POTION), kpConsumables::PrayerPotionFormula));
        potions.put("Super restore", new Consumable(List.of(Consumable.TYPE.PRAYER_POTION, Consumable.TYPE.RESTORE_POTION), kpConsumables::SuperRestoreFormula));
        potions.put("Sanfew serum", new Consumable(List.of(Consumable.TYPE.PRAYER_POTION, Consumable.TYPE.POISON_POTION), kpConsumables::SanfewSerumFormula));

        potions.put("Antipoison", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));
        potions.put("Superantipoison", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));
        potions.put("Antidote++", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));
        potions.put("Anti-venom", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));
        potions.put("Anti-venom+", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));
        potions.put("Extended anti-venom+", new Consumable(List.of(Consumable.TYPE.POISON_POTION)));

        potions.put("Attack potion", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION), kpConsumables::AttackPotionFormula));
        potions.put("Strength potion", new Consumable(List.of(Consumable.TYPE.STRENGTH_POTION), kpConsumables::StrengthPotionFormula));
        potions.put("Defence potion", new Consumable(List.of(Consumable.TYPE.DEFENCE_POTION), kpConsumables::DefencePotionFormula));
        potions.put("Super attack", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION), kpConsumables::SuperAttackFormula));
        potions.put("Super strength", new Consumable(List.of(Consumable.TYPE.STRENGTH_POTION), kpConsumables::SuperStrengthFormula));
        potions.put("Super defence", new Consumable(List.of(Consumable.TYPE.DEFENCE_POTION), kpConsumables::SuperDefenceFormula));
        potions.put("Combat potion", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION, Consumable.TYPE.STRENGTH_POTION, Consumable.TYPE.DEFENCE_POTION), () -> (CombatPotionAttackFormula() + CombatPotionStrengthFormula()) / 2));
        potions.put("Super combat potion", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION, Consumable.TYPE.STRENGTH_POTION, Consumable.TYPE.DEFENCE_POTION), kpConsumables::SuperAttackFormula));
        potions.put("Divine super attack", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION), kpConsumables::SuperAttackFormula));
        potions.put("Divine super strength", new Consumable(List.of(Consumable.TYPE.STRENGTH_POTION), kpConsumables::SuperStrengthFormula));
        potions.put("Divine super defence", new Consumable(List.of(Consumable.TYPE.DEFENCE_POTION), kpConsumables::SuperDefenceFormula));
        potions.put("Divine super combat potion", new Consumable(List.of(Consumable.TYPE.ATTACK_POTION, Consumable.TYPE.STRENGTH_POTION, Consumable.TYPE.DEFENCE_POTION), kpConsumables::SuperAttackFormula));
        potions.put("Ranging potion", new Consumable(List.of(Consumable.TYPE.RANGED_POTION), kpConsumables::RangingPotionFormula));
        potions.put("Divine ranging potion", new Consumable(List.of(Consumable.TYPE.RANGED_POTION), kpConsumables::RangingPotionFormula));
        potions.put("Magic potion", new Consumable(List.of(Consumable.TYPE.MAGIC_POTION), kpConsumables::MagicPotionFormula));
        potions.put("Divine magic potion", new Consumable(List.of(Consumable.TYPE.MAGIC_POTION), kpConsumables::MagicPotionFormula));

        // Fast food
        fastFood.put("Cooked karambwan", new Consumable(List.of(Consumable.TYPE.FAST_FOOD), 18));
    }

    public static Consumable GetHardFood(String name)
    {
        return hardFood.get(name.split("\\(")[0].trim());
    }

    public static Consumable GetPotion(String name)
    {
        return potions.get(name.split("\\(")[0].trim());
    }

    public static Consumable GetFastFood(String name)
    {
        return fastFood.get(name.split("\\(")[0].trim());
    }

    public static Consumable GetConsumable(String name)
    {
        name = name.split("\\(")[0].trim();
        Consumable consumable = GetHardFood(name);
        if (consumable != null)
            return consumable;

        consumable = GetPotion(name);
        if (consumable != null)
            return consumable;

        return GetFastFood(name);
    }

    public static List<Map<String, Consumable>> GetAllConsumables()
    {
        return List.of(hardFood, potions, fastFood);
    }

    public static List<String> GetAllConsumableNames()
    {
        return GetAllConsumables().stream().flatMap(map -> map.keySet().stream()).collect(Collectors.toList());
    }
}
