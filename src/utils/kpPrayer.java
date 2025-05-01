package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.tdi.Prayer;
import org.rspeer.game.component.tdi.Prayers;
import org.rspeer.game.movement.Movement;

import java.util.ArrayList;
import java.util.List;

public class kpPrayer
{
    public static boolean IsActive()
    {
        return !Prayers.getActive().isEmpty();
    }

    public static boolean IsActive(Prayer prayer)
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

    public static void Disable(Prayer ... prayers)
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

    public static void DisableAllExcept(Prayer ... prayers)
    {
        for (Prayer activePrayer : Prayers.getActive())
        {
            boolean shouldDisable = true;

            for (Prayer prayer : prayers)
            {
                if (activePrayer.equals(prayer))
                {
                    shouldDisable = false;
                    break;
                }
            }

            if (shouldDisable)
            {
                Prayers.select(true, activePrayer);
            }
        }
    }

    public static void Enable(Prayer ... prayers)
    {
        for (Prayer prayer : prayers)
        {
            if (!Prayers.isActive(prayer))
            {
                Log.info("Enabling " + prayer.toString());
                Prayers.select(true, prayer);
            }
        }
    }

    public static void Select(Prayer ... prayers)
    {
        for (Prayer prayer : prayers)
        {
            Log.info("Selecting " + prayer.toString());
            Prayers.select(true, prayer);
        }
    }

    public static List<Prayer> GetOffensivePrayers(boolean includeDefensive)
    {
        List<Prayer> returnList = new ArrayList<>();

        switch (kpUtils.GetCombatStyle())
        {
            case MELEE:
            {
                if (Prayers.isUnlocked(Prayer.Modern.PIETY))
                {
                    returnList.add(Prayer.Modern.PIETY);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.CHIVALRY))
                {
                    returnList.add(Prayer.Modern.CHIVALRY);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.ULTIMATE_STRENGTH) && Prayers.isUnlocked(Prayer.Modern.INCREDIBLE_REFLEXES))
                {
                    if (includeDefensive) returnList.add(Prayer.Modern.STEEL_SKIN);
                    returnList.add(Prayer.Modern.ULTIMATE_STRENGTH);
                    //returnList.add(Prayer.Modern.INCREDIBLE_REFLEXES); // A real human probably won't flick two prayers at once lol
                }

                // Should not be lower than this, we should have protection prayers unlocked

                break;
            }
            case RANGED:
            {
                if (Prayers.isUnlocked(Prayer.Modern.RIGOUR))
                {
                    returnList.add(Prayer.Modern.RIGOUR);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.EAGLE_EYE))
                {
                    if (includeDefensive) returnList.add(Prayer.Modern.STEEL_SKIN);
                    returnList.add(Prayer.Modern.EAGLE_EYE);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.HAWK_EYE))
                {
                    returnList.add(Prayer.Modern.HAWK_EYE);
                }

                // Should not be lower than this, we should have protection prayers unlocked

                break;
            }
            case MAGIC:
            {
                if (Prayers.isUnlocked(Prayer.Modern.AUGURY))
                {
                    returnList.add(Prayer.Modern.AUGURY);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.MYSTIC_MIGHT))
                {
                    if (includeDefensive) returnList.add(Prayer.Modern.STEEL_SKIN);
                    returnList.add(Prayer.Modern.MYSTIC_MIGHT);
                }
                else if (Prayers.isUnlocked(Prayer.Modern.MYSTIC_LORE))
                {
                    returnList.add(Prayer.Modern.MYSTIC_LORE);
                }

                // Should not be lower than this, we should have protection prayers unlocked

                break;
            }
            default:
            {
                Log.severe("Unknown combat style, what weapon are you using?");
                break;
            }
        }

        return returnList;
    }
}
