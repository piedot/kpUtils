package utils;

import org.rspeer.commons.ItemPrice;
import org.rspeer.commons.PriceCheck;
import org.rspeer.commons.logging.Log;

public class kpPrice
{
    public static int Get(int itemId)
    {
        ItemPrice itemPrice = PriceCheck.lookup(itemId);
        if (itemPrice == null)
        {
            Log.severe("PriceCheck failed to find price for item " + itemId);
            // Handle ourselves
            PriceCheck.resetCache();
            return 0;
        }
        return itemPrice.getAverage();
    }

    public static int GetHigh(int itemId)
    {
        ItemPrice itemPrice = PriceCheck.lookup(itemId);
        if (itemPrice == null)
        {
            Log.severe("PriceCheck failed to find price for item " + itemId);
            // Handle ourselves
            PriceCheck.resetCache();
            return 0;
        }
        return itemPrice.getHigh();
    }
}
