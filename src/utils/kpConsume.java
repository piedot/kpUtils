package utils;

import ids.ItemId;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Equipment;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.tdi.Prayers;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;
import org.rspeer.game.effect.Health;

import java.awt.*;

public class kpConsume
{
    private static final int NORMAL_ATTACK_DELAY = 3;
    private static final int FAST_ATTACK_DELAY = 2;
    private static final int FOOD_DELAY = 3;
    private static final int HUNTER_FOOD_DELAY = 8;

    // Public for debugging
    public static int hardFoodDelay = 0; // Hard food and fast food share the same EAT delay, fast food has a faster ATTACK delay
    public static int fastFoodDelay = 0;
    public static int potionDelay = 0;
    public static int hunterFoodDelay = 0;

    private static int currentHealth = 0;
    private static int currentPrayer = 0;
    private static boolean eat = false;
    private static boolean drinkPrayer = false;

    public static void Run(boolean freeToEat, int healthToEatAt, int prayerToDrinkAt)
    {
        if (hardFoodDelay > 0) hardFoodDelay--;
        if (fastFoodDelay > 0) fastFoodDelay--;
        if (potionDelay > 0) potionDelay--;
        if (hunterFoodDelay > 0) hunterFoodDelay--;

        eat = false;
        drinkPrayer = false;

        currentHealth = Health.getCurrent();
        if (currentHealth <= healthToEatAt)
        {
            eat = true;
        }
        if (hunterFoodDelay > 0)
            currentHealth += 12; // Hunter meat adds a secondary heal (moonlight antelope is the highest healing at 12), so we can account for it like this, a little dangerous though
        Item mainHandWeapon = Inventories.equipment().getItemAt(Equipment.Slot.MAINHAND);
        if (mainHandWeapon != null && mainHandWeapon.getId() == ItemId.SOULREAPER)
            currentHealth += 8; // During downtime, we will heal eight health from the soulreaper passive

        currentPrayer = Prayers.getPoints();
        if (currentPrayer <= prayerToDrinkAt)
        {
            drinkPrayer = true;
        }

        Log.debug(" eat: " + eat + " freeToEat " + freeToEat + " drinkPrayer: " + drinkPrayer + " currentHealth: " + currentHealth + " currentPrayer: " + currentPrayer);
        Log.debug("Max health: " + Health.getLevel() + " Total prayer: " + Prayers.getTotalPoints());
        Log.debug("Hard food delay: " + hardFoodDelay + " fast food delay: " + fastFoodDelay + " potion delay: " + potionDelay);

        for (Item item : Inventories.backpack().query().results().asList())
        {
            kpConsumables.Consumable consumable = kpConsumables.GetHardFood(item.getName());
            HandleConsumable(consumable, item, freeToEat);
        }

        for (Item item : Inventories.backpack().query().results().asList())
        {
            kpConsumables.Consumable consumable = kpConsumables.GetPotion(item.getName());
            HandleConsumable(consumable, item, freeToEat);
        }

        for (Item item : Inventories.backpack().query().results().asList())
        {
            kpConsumables.Consumable consumable = kpConsumables.GetFastFood(item.getName());
            HandleConsumable(consumable, item, freeToEat);
        }

        return;
    }

    private static void HandleConsumable(kpConsumables.Consumable consumable, Item item, boolean freeToEat)
    {
        if (consumable == null || item == null)
            return;

        int effectAmount = consumable.getEffectAmount();

        for (kpConsumables.Consumable.TYPE type : consumable.getTypes())
        {
            switch (type)
            {
                case LATE_RESTORE_FOOD:
                {
                    if ((eat || freeToEat) && hardFoodDelay == 0 && hunterFoodDelay == 0 && currentHealth + effectAmount < Health.getLevel())
                    {
                        Log.info("Eating hunter meat");
                        item.interact(consumable.getAction());
                        currentHealth += effectAmount;
                        hunterFoodDelay = HUNTER_FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(NORMAL_ATTACK_DELAY);
                    }
                    break;
                }
                case HARD_FOOD:
                {
                    if ((eat || freeToEat) && hardFoodDelay == 0 && currentHealth + effectAmount < Health.getLevel())
                    {
                        Log.info("Eating hard food");
                        item.interact(consumable.getAction());
                        currentHealth += effectAmount;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(NORMAL_ATTACK_DELAY);
                    }
                    break;
                }
                case FAST_FOOD:
                {
                    if (eat && fastFoodDelay == 0 && currentHealth + effectAmount < Health.getLevel())
                    {
                        Log.info("Eating fast food");
                        item.interact(consumable.getAction());
                        currentHealth += effectAmount;
                        fastFoodDelay = FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(FAST_ATTACK_DELAY);
                    }
                    break;
                }
                case POTION_FOOD: // Brews
                {
                    if (eat && potionDelay == 0 && currentHealth <= Health.getCurrent()) // Can overheal
                    {
                        Log.info("Drinking brew");
                        item.interact(consumable.getAction());
                        currentHealth += effectAmount;
                        potionDelay = FOOD_DELAY;
                        fastFoodDelay = FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(0);
                    }
                    break;
                }
                case PRAYER_POTION:
                {
                    if (drinkPrayer && potionDelay == 0 && currentPrayer + effectAmount < Prayers.getTotalPoints())
                    {
                        Log.info("Drinking prayer potion");
                        item.interact(consumable.getAction());
                        potionDelay = FOOD_DELAY;
                        fastFoodDelay = FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(0);
                    }
                    break;
                }
                case RESTORE_POTION: // TODO: Brews & Combat potions & Don't drink at the same time & Don't drink combat if our stats are drained
                {

                    break;
                }
                case POISON_POTION:
                {
                    if (potionDelay == 0 && (Combat.isPoisoned() || Combat.isEnvenomed()))
                    {
                        Log.info("Drinking anti poison");
                        item.interact(consumable.getAction());
                        potionDelay = FOOD_DELAY;
                        fastFoodDelay = FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(0);
                    }
                    break;
                }
                case ATTACK_POTION:
                case STRENGTH_POTION:
                case DEFENCE_POTION:
                case RANGED_POTION:
                case MAGIC_POTION:
                {
                    Skill skill = type.equals(kpConsumables.Consumable.TYPE.ATTACK_POTION) ? Skill.ATTACK :
                            type.equals(kpConsumables.Consumable.TYPE.STRENGTH_POTION) ? Skill.STRENGTH :
                                    type.equals(kpConsumables.Consumable.TYPE.DEFENCE_POTION) ? Skill.DEFENCE :
                                            type.equals(kpConsumables.Consumable.TYPE.RANGED_POTION) ? Skill.RANGED :
                                                    Skill.MAGIC;

                    int skillBoostedBy = Skills.getCurrentLevel(skill) - Skills.getLevel(skill);

                    // TODO Brews & Restores
                    if (potionDelay == 0 && skillBoostedBy < consumable.getEffectAmount() / 1.3)
                    {
                        Log.info("Drinking " + skill + " potion");
                        item.interact(consumable.getAction());
                        potionDelay = FOOD_DELAY;
                        fastFoodDelay = FOOD_DELAY;
                        hardFoodDelay = FOOD_DELAY;
                        kpGameData.AddToAttackCooldown(0);
                    }

                    break;
                }
            } // switch (type)
        } // for consumable types
    }

    public static int GetFoodHealingLeft()
    {
        int healingLeft = 0;
        for (Item item : Inventories.backpack().query().results())
        {
            if (item == null)
                continue;

            String consumableName = item.getName();
            kpConsumables.Consumable consumable = kpConsumables.GetConsumable(consumableName);
            if (consumable == null)
                continue;

            for (kpConsumables.Consumable.TYPE type : consumable.getTypes())
            {
                if (type == kpConsumables.Consumable.TYPE.HARD_FOOD || type == kpConsumables.Consumable.TYPE.FAST_FOOD || type == kpConsumables.Consumable.TYPE.LATE_RESTORE_FOOD)
                {
                    healingLeft += consumable.getEffectAmount();
                    break;
                }
                if (type == kpConsumables.Consumable.TYPE.POTION_FOOD)
                {
                    int doses = consumableName.contains("(1)") ? 1 :
                            consumableName.contains("(2)") ? 2 :
                                    consumableName.contains("(3)") ? 3 : 4;
                    healingLeft += consumable.getEffectAmount() * doses;
                    break;
                }
            }
        }

        Log.debug("Healing left: " + healingLeft);
        return healingLeft;
    }

    public static int GetPrayerLeft()
    {
        int prayerLeft = 0;
        for (Item item : Inventories.backpack().query().results())
        {
            if (item == null)
                continue;

            String consumableName = item.getName();
            kpConsumables.Consumable consumable = kpConsumables.GetConsumable(consumableName);
            if (consumable == null)
                continue;

            for (kpConsumables.Consumable.TYPE type : consumable.getTypes())
            {
                if (type == kpConsumables.Consumable.TYPE.PRAYER_POTION)
                {
                    int doses = consumableName.contains("(1)") ? 1 :
                            consumableName.contains("(2)") ? 2 :
                                    consumableName.contains("(3)") ? 3 : 4;
                    prayerLeft += consumable.getEffectAmount() * doses;
                    break;
                }
            }
        }

        Log.debug("Prayer left: " + prayerLeft);
        return prayerLeft;
    }

    public static void Paint(Graphics2D g2d)
    {
        int i = 0;
        kpPaint.DrawString(g2d, "Hard food delay: " + hardFoodDelay, 1000, 24 + 16 * i, Color.GREEN); i++;
        kpPaint.DrawString(g2d, "Fast food delay: " + fastFoodDelay, 1000, 24 + 16 * i, Color.GREEN); i++;
        kpPaint.DrawString(g2d, "Potion delay: " + potionDelay, 1000, 24 + 16 * i, Color.GREEN); i++;
        kpPaint.DrawString(g2d, "Hunter food delay: " + hunterFoodDelay, 1000, 24 + 16 * i, Color.GREEN); i++;

        return;
    }
}
