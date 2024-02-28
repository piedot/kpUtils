package kpUtils.src.utils;


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
            kpTaskQueue.AddTask(new kpTaskQueue.Task("Disabling " + prayer.toString(), 20, () -> {
                Prayers.select(true, prayer);
            }), 0);
        }
    }

    public static void Disable(Prayer.Modern ... prayers)
    {
        for (Prayer prayer : prayers)
        {
            if (Prayers.isActive(prayer))
            {
                Prayers.select(true, prayer);
            }
        }
    }

    // Pass this into TaskQueue
    public static void Select(Prayer.Modern ... prayers)
    {
        for (Prayer.Modern prayer : prayers)
        {
            Prayers.select(true, prayer);
        }
    }
}
