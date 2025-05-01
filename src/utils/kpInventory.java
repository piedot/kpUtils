package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;

public class kpInventory
{
    public static int GetCount(String itemName)
    {
        return GetCount(itemName, false);
    }

    public static int GetCount(String itemName, boolean noted)
    {
        int inventoryCount = Inventories.backpack().query().names(itemName).filter(i -> i.isNoted() == noted).results().size();
        int stackCount = 0;

        Item item = Inventories.backpack().query().names(itemName).filter(i -> i.isNoted() == noted).results().first();

        if (item != null)
        {
            stackCount = Inventories.backpack().query().names(itemName).filter(i -> i.isNoted() == noted).results().first().getStackSize();
        }

        return Math.max(inventoryCount, stackCount);
    }

    public static int GetCount(int[] itemIds)
    {
        return GetCount(itemIds, false);
    }

    public static int GetCount(int[] itemIds, boolean noted)
    {
        int inventoryCount = Inventories.backpack().query().ids(itemIds).filter(i -> i.isNoted() == noted).results().size();
        int stackCount = 0;

        Item item = Inventories.backpack().query().ids(itemIds).filter(i -> i.isNoted() == noted).results().first();

        if (item != null)
        {
            stackCount = Inventories.backpack().query().ids(itemIds).filter(i -> i.isNoted() == noted).results().first().getStackSize();
        }

        return Math.max(inventoryCount, stackCount);
    }
}
