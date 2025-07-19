package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.scene.Players;

public class kpMovement
{
    public static void ActivateRunEnergy(int at)
    {
        boolean shouldEnableRunEnergy = Movement.getRunEnergy() >= at;

        //Log.debug("ActivateRunEnergy(" + shouldEnableRunEnergy + ")");

        if (!Movement.isRunEnabled() && shouldEnableRunEnergy)
        {
            Movement.toggleRun(true);
        }

        return;
    }

    public static void WalkTo(SceneNode destination)
    {
        if (IsMoving())
        {
            Log.info("WalkTo - We are moving");
            return;
        }

        Log.info("Walking to [" + destination.getX() + ", " + destination.getY() + "]");

        Movement.walkTo(destination);

        return;
    }

    public static void Step(SceneNode destination)
    {
        ActivateRunEnergy(12);

        Log.info("Stepping to [" + destination.getX() + ", " + destination.getY() + "]");

        Movement.walkTowards(destination);

        return;
    }

    public static void StepWalk(SceneNode destination)
    {
        if (destination.equals(Movement.getDestination()))
        {
            Log.info("StepWalk - We are already stepping to the destination");
            return;
        }

        Log.info("Stepping to [" + destination.getX() + ", " + destination.getY() + "]");

        Movement.walkTowards(destination);

        return;
    }

    public static boolean IsMoving()
    {
        Player localPlayer = Players.self();

        if (localPlayer == null)
            return false;

        return localPlayer.isMoving() && Movement.isDestinationSet();
    }
}
