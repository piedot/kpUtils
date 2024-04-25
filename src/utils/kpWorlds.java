package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.World;
import org.rspeer.game.component.Worlds;
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
                if (currentWorld.getLocation().equals(World.Location.AU)) // Australia
                {
                    if (!w.getLocation().equals(World.Location.AU))
                    {
                        return false;
                    }
                }
                else if (currentWorld.getLocation().equals(World.Location.US)) // The US
                {
                    if (!w.getLocation().equals(World.Location.US))
                    {
                        return false;
                    }
                }
                else if (currentWorld.getLocation().equals(World.Location.DE) || currentWorld.getLocation().equals(World.Location.UK)) // Europe
                {
                    if (!w.getLocation().equals(World.Location.DE) && !w.getLocation().equals(World.Location.UK))
                    {
                        return false;
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
        Worlds.hopTo(world);

        return;
    }
}
