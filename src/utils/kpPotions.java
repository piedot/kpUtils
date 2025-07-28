package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;

import java.util.HashMap;
import java.util.Map;

public class kpPotions
{
    private static String StripPotionName(String name)
    {
        int index = name.indexOf('(');
        return index != -1 ? name.substring(0, index).trim() : name;
    }

    private static int GetDosage(String name)
    {
        int start = name.indexOf('(');
        if (start != -1)
        {
            int end = name.indexOf(')');
            if (end != -1)
            {
                try
                {
                    return Integer.parseInt(name.substring(start + 1, end));
                }
                catch (NumberFormatException e)
                {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * Drops vials and decants potions if needed
     */
    public static void DropAndDecantPotions()
    {
        for (Item vial : Inventories.backpack().query().names("Vial").results().asList())
        {
            Log.info("Dropping vial");
            vial.interact("Drop");
        }

        Map<String, Item> potionsToDecant = new HashMap<>();

        for (Item item : Inventories.backpack().query().results().asList())
        {
            String name = item.getName();
            int dosage = GetDosage(name);

            if (dosage > 0 && dosage < 4)
            {
                String baseName = StripPotionName(name);
                if (potionsToDecant.containsKey(baseName))
                {
                    Log.info("Decanting " + name);
                    Inventories.backpack().use(item, potionsToDecant.get(baseName));
                }
                else
                {
                    potionsToDecant.put(baseName, item);
                }
            }
        }
    }
}
