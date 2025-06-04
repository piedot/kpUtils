package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.World;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Trade;
import org.rspeer.game.component.Worlds;
import org.rspeer.game.component.stockmarket.StockMarket;
import org.rspeer.game.component.tdi.Skills;

public class kpWorlds
{
    public static void Hop(boolean members)
    {
        World currentWorld = Worlds.getLocalWorld();

        if (currentWorld == null)
        {
            Log.info("Current world is null");
            return;
        }

        World world = Worlds.query()
            .flags(f ->
                //f.contains(World.Flag.MEMBERS) &&
                //f.contains(World.Flag.SKILL_TOTAL) &&

                !f.contains(World.Flag.BETA) &&
                !f.contains(World.Flag.BETA_SAVELESS) &&
                !f.contains(World.Flag.FRESH_START) &&
                !f.contains(World.Flag.DEADMAN) &&
                !f.contains(World.Flag.PVP) &&
                !f.contains(World.Flag.SPEEDRUN) &&
                !f.contains(World.Flag.TOURNAMENT) &&
                !f.contains(World.Flag.PVP_ARENA) &&
                !f.contains(World.Flag.BOUNTY) &&
                !f.contains(World.Flag.SECRET) &&
                !f.contains(World.Flag.SEASONAL) &&
                !f.contains(World.Flag.HIGH_RISK) // Inubot doenst handle the warning when hopping to a high risk world, otherwise this would not be here
            )
            .filter(w ->
                    w.getId() != currentWorld.getId()
            )
            .filter(w ->
                    members == w.getFlags().contains(World.Flag.MEMBERS)
            )
            .filter(w -> {
                World.Location worldLocation = w.getLocation();

                switch (currentWorld.getLocation())
                {
                    case AU:
                    {
                        if (!worldLocation.equals(World.Location.AU))
                        {
                            return false;
                        }
                        break;
                    }
                    case US:
                    {
                        if (!worldLocation.equals(World.Location.US))
                        {
                            return false;
                        }
                        break;
                    }
                    case DE:
                    {
                        if (!worldLocation.equals(World.Location.DE))
                        {
                            return false;
                        }
                        break;
                    }
                    case UK:
                    {
                        if (!worldLocation.equals(World.Location.UK))
                        {
                            return false;
                        }
                        break;
                    }
                }

                return true;
            })
            .filter(w -> {
                if (w.getFlags().contains(World.Flag.SKILL_TOTAL))
                {
                    String[] parts = w.getProvider().getActivity().split(" "); // "1250 skill total"

                    int requiredTotalLevel = Integer.MAX_VALUE;

                    try
                    {
                        requiredTotalLevel = Integer.parseInt(parts[0]);
                    }
                    catch (Exception exception)
                    {
                        Log.info("Failed to parse world query for world " + w.getId());
                    }

                    if (Skills.getTotalLevel() < requiredTotalLevel)
                    {
                        return false;
                    }
                }

                return true;
            })
            .results().random();

        if (world == null)
        {
            Log.warn("Failed to find a valid world to hop to");
            return;
        }

        Log.info("Hopping worlds to " + world.getId() + " " + world.getProvider().getActivity());
        Hop(world);

        return;
    }

    // Could also need to continue the chat or something
    public static boolean Hop(World world)
    {
        if (StockMarket.isOpen() || Bank.isOpen() || Trade.getView() != Trade.View.CLOSED)
        {
            Log.info("kpWorlds - closing interfaces");
            Interfaces.closeSubs();
        }

        return Worlds.hopTo(world);
    }

    public static boolean Hop(int world)
    {
        if (StockMarket.isOpen() || Bank.isOpen() || Trade.getView() != Trade.View.CLOSED)
        {
            Log.info("kpWorlds - closing interfaces");
            Interfaces.closeSubs();
        }

        return Worlds.hopTo(world);
    }


}
