package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class kpConsumables
{
    // Do we need to know what type it is? how will we retrieve a specific consumable? is it dynamic/hard coded/user chosen? Find out in the next episode!
    int HEALTH = 0b1;
    int PRAYER = 0b10;
    int POISON = 0b100;

    public static class Consumable
    {
        int type;
        private String action;
        private int effectAmount;
        private Callable<Integer> effectFormula;
        private boolean overheal;

        public Consumable(String action, int effectAmount)
        {
            this.action = action;
            this.effectAmount = effectAmount;
            this.effectFormula = null;
            overheal = false;
        }

        public Consumable(String action, Callable<Integer> effectFormula)
        {
            this.action = action;
            this.effectAmount = -1;
            this.effectFormula = effectFormula;
            overheal = false;
        }

        public Consumable(String action, Callable<Integer> effectFormula, boolean overheal)
        {
            this.action = action;
            this.effectAmount = -1;
            this.effectFormula = effectFormula;
            this.overheal = overheal;
        }

        public String getAction()
        {
            return action;
        }

        public int getEffectAmount()
        {
            if (effectAmount == -1)
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

    private static Map<String, Consumable> consumableItems;

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

    public static void InitializeItemInfo()
    {
        consumableItems = new HashMap<>();

        consumableItems.put("Shrimps",                  new Consumable("Eat", 3));
        consumableItems.put("Cooked chicken",           new Consumable("Eat", 3));
        consumableItems.put("Cooked meat",              new Consumable("Eat", 3));
        consumableItems.put("Sardine",                  new Consumable("Eat", 4));
        consumableItems.put("Bread",                    new Consumable("Eat", 5));
        consumableItems.put("Herring",                  new Consumable("Eat", 5));
        consumableItems.put("Trout",                    new Consumable("Eat", 7));
        consumableItems.put("Pike",                     new Consumable("Eat", 8));
        consumableItems.put("Peach",                    new Consumable("Eat", 8));
        consumableItems.put("Salmon",                   new Consumable("Eat", 9));
        consumableItems.put("Tuna",                     new Consumable("Eat", 10));
        consumableItems.put("Redberry pie",             new Consumable("Eat", 5)); // Not supported
        consumableItems.put("Jug of wine",              new Consumable("Drink", 11));
        consumableItems.put("Stew",                     new Consumable("Eat", 11));
        consumableItems.put("Cake",                     new Consumable("Eat", 4)); // Not supported
        consumableItems.put("Meat pie",                 new Consumable("Eat", 6)); // Not supported
        consumableItems.put("Lobster",                  new Consumable("Eat", 12));
        consumableItems.put("Bass",                     new Consumable("Eat", 13));
        consumableItems.put("Plain pizza",              new Consumable("Eat", 7)); // Not supported
        consumableItems.put("Swordfish",                new Consumable("Eat", 14));
        consumableItems.put("Potato with butter",       new Consumable("Eat", 14));
        consumableItems.put("Apple pie",                new Consumable("Eat", 7)); // Not supported
        consumableItems.put("Chocolate cake",           new Consumable("Eat", 5)); // Not supported
        consumableItems.put("Tangled toad's legs",      new Consumable("Eat", 15));
        consumableItems.put("Chocolate bomb",           new Consumable("Eat", 15));
        consumableItems.put("Potato with cheese",       new Consumable("Eat", 16));
        consumableItems.put("Meat pizza",               new Consumable("Eat", 8)); // Not supported
        consumableItems.put("Monkfish",                 new Consumable("Eat", 16));
        consumableItems.put("Anchovy pizza",            new Consumable("Eat", 9)); // Not supported
        consumableItems.put("Cooked karambwan",         new Consumable("Eat", 18));
        consumableItems.put("Curry",                    new Consumable("Eat", 19));
        consumableItems.put("Guthix rest",              new Consumable("Drink", 5));
        consumableItems.put("Shark",                    new Consumable("Eat", 20));
        consumableItems.put("Sea turtle",               new Consumable("Eat", 21));
        consumableItems.put("Pineapple pizza",          new Consumable("Eat", 11)); // Not supported
        consumableItems.put("Summer pie",               new Consumable("Eat", 11)); // Not supported
        consumableItems.put("Wild pie",                 new Consumable("Eat", 11)); // Not supported
        consumableItems.put("Manta ray",                new Consumable("Eat", 22));
        consumableItems.put("Tuna potato",              new Consumable("Eat", 22));
        consumableItems.put("Dark crab",                new Consumable("Eat", 22));
        consumableItems.put("Anglerfish",               new Consumable("Eat", kpConsumables::AnglerfishFormula, true));
        consumableItems.put("Basket of strawberries",   new Consumable("Remove-one", -1));
        consumableItems.put("Strawberry",               new Consumable("Eat", kpConsumables::StrawberryFormula));
        consumableItems.put("Saradomin brew",           new Consumable("Drink", kpConsumables::SaradominBrewFormula, true));

        consumableItems.put("Prayer potion",            new Consumable("Drink", kpConsumables::PrayerPotionFormula));
        consumableItems.put("Super restore",            new Consumable("Drink", kpConsumables::SuperRestoreFormula));
        consumableItems.put("Sanfew serum",             new Consumable("Drink", kpConsumables::SanfewSerumFormula));
    }


}
