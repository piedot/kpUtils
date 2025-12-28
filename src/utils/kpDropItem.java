package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;

import java.util.List;

public class kpDropItem
{
    /**
     * Tries to drop a consumable item to make space in the backpack
     * @return true if successful, false if no consumable item found
     */
    public static boolean AnyConsumable()
    {
        List<String> consumableNames = kpConsumables.GetAllConsumableNames();

        // Drop a consumable item if we are full
        Item itemToDrop = Inventories.backpack().query().nameContains(consumableNames.toArray(new String[0])).results().first();

        if (itemToDrop == null)
        {
            Log.warn("Dropping a consumable failed, no consumable item to drop");
            return false;
        }

        String itemToDropName = itemToDrop.getName();
        if (consumableNames.contains(itemToDropName))
        {
            Log.info("");
        }

        Log.info("Dropping " + itemToDrop.getName());
        itemToDrop.interact("Drop");
        return true;
    }
}
