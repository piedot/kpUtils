package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.type.SceneNode;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.scene.Players;

public class kpMovement
{
    public static void WalkTo(SceneNode destination)
    {
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 12)
        {
            Log.info("WalkTo - Enabling run");
            Movement.toggleRun(true);
        }

        Player localPlayer = Players.self();

        if (localPlayer == null)
            return;

        if (localPlayer.isMoving())
        {
            Log.info("WalkTo - we are moving");
            return;
        }

        Log.info("Walking to [" + destination.getX() + ", " + destination.getY() + "]");
        Movement.walkTo(destination);
    }

    public static void Step(SceneNode destination)
    {
        Log.info("Stepping to [" + destination.getX() + ", " + destination.getY() + "]");
        Movement.walkTowards(destination);
    }
}
