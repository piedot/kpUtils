package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;

import java.util.List;

public class kpInventory
{
    public static int GetCount(String itemName, Boolean noted)
    {
        return GetCount(new String[]{itemName}, noted);
    }

    /**
     * Includes both noted and unnoted?
     * @param itemNames
     * @return
     */
    public static int GetCount(String[] itemNames, Boolean noted)
    {
        return Inventories.backpack().query()
                .filter(i -> noted == null || i.isNoted() == noted)
                .names(itemNames)
                .results()
                .stream()
                .map(Item::getStackSize)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static int GetCount(int itemId, Boolean noted)
    {
        return GetCount(new int[]{itemId}, noted);
    }

    public static int GetCount(int[] itemIds, Boolean noted)
    {
        return Inventories.backpack().query()
                .filter(i -> noted == null || i.isNoted() == noted)
                .ids(itemIds)
                .results()
                .stream()
                .map(Item::getStackSize)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
