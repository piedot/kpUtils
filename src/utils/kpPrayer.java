package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.tdi.Prayer;
import org.rspeer.game.component.tdi.Prayers;
import org.rspeer.game.movement.Movement;

import java.util.List;

public class kpPrayer
{
    public static boolean IsActive()
    {
        return !Prayers.getActive().isEmpty();
    }

    public static boolean IsActive(Prayer.Modern prayer)
    {
        return Prayers.isActive(prayer);
    }

    public static void DisableAll()
    {
        for (Prayer prayer : Prayers.getActive())
        {
            Prayers.select(true, prayer);
        }
    }

    public static void Disable(Prayer.Modern ... prayers)
    {
        for (Prayer prayer : prayers)
        {
            if (Prayers.isActive(prayer))
            {
                Log.info("Disabling " + prayer.toString());
                Prayers.select(true, prayer);
            }
        }
    }

    public static void Select(Prayer.Modern ... prayers)
    {
        for (Prayer.Modern prayer : prayers)
        {
            Log.info("Selecting " + prayer.toString());
            Prayers.select(true, prayer);
        }
    }
}
