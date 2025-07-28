package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.component.inventory.Equipment;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.service.stockmarket.StockMarketService;

import java.util.Arrays;
import java.util.List;

public class kpRecharging
{
    public static boolean RechargeItem(int[] chargedItemIds, int[] unchargedItemIds, int[] chargeItemIds, boolean applyNotedItems, int amountOfCharges, Equipment.Slot equipmentSlot, StockMarketService stockMarketService)
    {
        Item chargedItem = Inventories.backpack().query().ids(chargedItemIds).results().first();
        chargedItem = equipmentSlot != null && chargedItem == null ? Inventories.equipment().query().ids(chargedItemIds).results().first() : chargedItem;

        if (chargedItem != null)
        {
            Log.debug("Successfully recharged " + chargedItem.getName());
            return false;
        }

        Item unchargedItem = Inventories.backpack().query().ids(unchargedItemIds).results().first();

        if (unchargedItem == null) // If we have no charged item and no uncharged item, just skip recharging for now and wait for us to withdraw the item we would want to even charge
        {
            Item equipmentUnchargedItem = equipmentSlot != null ? Inventories.equipment().query().ids(unchargedItemIds).results().first() : null;

            if (equipmentUnchargedItem != null)
            {
                String equipmentUnchargedItemName = equipmentUnchargedItem.getName();

                if (Inventories.backpack().getEmptySlots() < 1)
                {
                    Log.info("Not enough space for the " + equipmentUnchargedItemName);
                    if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
                    {
                        Log.info("Opening bank");
                        return true;
                    }
                    Log.info("Depositing inventory");
                    Inventories.bank().depositInventory();
                    return true;
                }

                if (kpUtils.CloseInterfacesIfNeeded())
                {
                    return true;
                }

                Log.info("Removing " + equipmentUnchargedItemName + " from equipment");
                equipmentUnchargedItem.interact("Remove");
                return true;
            }

            Log.debug("Uncharged item not found");
            return false;
        }

        if (Inventories.backpack().getEmptySlots() < 1)
        {
            Log.info("Not enough space for the " + unchargedItem.getName());
            if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
            {
                Log.info("Opening bank");
                return true;
            }
            Log.info("Depositing everything except the " + unchargedItem.getName());
            Inventories.bank().depositAllExcept(i -> i.ids(unchargedItemIds).results());
            return true;
        }

        if (kpDialog.Continue())
        {
            Log.info("Continuing dialog");
            return true;
        }

        if (kpDialog.Select(List.of("Yes, combine the Blood shard with the Amulet of fury.")))
        {
            Log.info("Selecting dialog");
            return true;
        }

        if (kpDialog.TypeValue(amountOfCharges)) // TODO All recharge texts
        {
            Log.info("Typing in charges");
            return true;
        }

        for (int chargeItemId : chargeItemIds)
        {
            Item chargeItem = kpUtils.GetItem(new int[]{chargeItemId}, false, amountOfCharges, amountOfCharges, applyNotedItems, stockMarketService);
            if (chargeItem == null)
            {
                Log.warn("Getting " + chargeItemId);
                return true;
            }
        }

        if (Bank.isOpen())
        {
            Log.info("Closing bank");
            Interfaces.closeSubs();
        }

        Item chargeItem = kpUtils.GetItem(chargeItemIds, false, amountOfCharges, amountOfCharges, applyNotedItems, stockMarketService);

        if (chargeItem == null)
        {
            Log.warn("Somehow chargeItem turned null for " + Arrays.toString(chargeItemIds));
            return true;
        }

        Log.info("Using " + chargeItem + " on " + unchargedItem);
        Inventories.backpack().use(chargeItem, unchargedItem);
        return true;
    }
}
