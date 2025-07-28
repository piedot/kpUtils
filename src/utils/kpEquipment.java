package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.config.item.loadout.InventoryLoadout;

import java.util.stream.Collectors;

public class kpEquipment
{
    public static boolean EnsureWeHaveEquipment(EquipmentLoadout equipmentLoadout, InventoryLoadout inventoryLoadout, Runnable restockCallback)
    {
        equipmentLoadout.setOutOfItemListener(entry ->
        {
            Log.info("Out of equipment item " + entry.getKey());
            if (entry.contained(Inventories.backpack()))
            {
                Log.debug("We already have " + entry.getKey() + " in the backpack");
                equipmentLoadout.equip();
                return;
            }
            return;
        });

        Log.info("Missing equipment: " + equipmentLoadout.getMissingEquipmentEntries().stream().map(ItemEntry::getKey).collect(Collectors.toList()));

        if (!equipmentLoadout.isEquipmentValid())
        {
            Log.info("Equipment loadout is not valid, equipping if possible");

            equipmentLoadout.equip(); //

            if (!equipmentLoadout.isBackpackValid())
            {
                Log.info("Withdrawing equipment loadout");
                if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
                {
                    Log.info("Opening bank");
                    return true;
                }
                if (Inventories.backpack().query().filter(Item::isNoted).results().first() != null)
                {
                    Log.info("Depositing noted items");
                    Inventories.bank().depositInventory();
                    return true;
                }
                Log.info("Withdrawing equipment loadout from bank");
                equipmentLoadout.toBackpackLoadout().withdraw();
                return true;
            }

            return true;
        }

        inventoryLoadout.setOutOfItemListener(entry ->
        {
            String key = entry.getKey();
            Log.debug("Out of item " + key);
            if (entry.contained(Inventories.backpack()))
            {
                Log.debug("We already have " + entry.getKey() + " in the backpack");
                return;
            }
            if (entry.getRestockMeta() == null)
            {
                Log.warn("No restock meta for " + entry.getKey());
                return;
            }
            if (!Game.getAccountType().isRegular())
            {
                Log.severe("Ironman cannot restock, stopping script");
                kpStopScript.Stop();
                return;
            }
            Log.info("Submitting restock " + entry.getKey());
            restockCallback.run();
            return;
        });

        Log.info("Missing backpack: " + inventoryLoadout.getMissingBackpackEntries().stream().map(ItemEntry::getKey).collect(Collectors.toList()));

        if (!inventoryLoadout.isBackpackValid())
        {
            Log.info("Withdrawing items from bank");
            if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
            {
                Log.info("Opening bank");
                return true;
            }
            if (Inventories.backpack().query().filter(Item::isNoted).results().first() != null)
            {
                Log.info("Depositing noted items");
                Inventories.bank().depositInventory();
                return true;
            }
            inventoryLoadout.withdraw();
            Log.warn("Missing: " + inventoryLoadout.getMissingBackpackEntries().stream().map(ItemEntry::getKey).collect(Collectors.toList()));
            return true;
        }

        Log.debug("We have our equipment");
        return false;
    }
}
