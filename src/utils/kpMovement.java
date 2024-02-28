package kpUtils.src.utils;

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
            kpTaskQueue.AddTask(new kpTaskQueue.Task("Enabling run", 10, () -> {
                Movement.toggleRun(true);
            }), 0);
        }

        if (Movement.isDestinationSet())
        {
            return;
        }

        kpTaskQueue.AddTask(new kpTaskQueue.Task("Walking to [" + destination.getX() + ", " + destination.getY() + "]", 20, () -> {
            Movement.walkTo(destination);
        }), 0);
    }
}
