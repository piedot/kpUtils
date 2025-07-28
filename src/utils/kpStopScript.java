package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;

@TaskDescriptor(
        name = "kpStopScript",
        stoppable = true,
        blocking = true,
        priority = Integer.MAX_VALUE // Until RestockTask loses its priority
)

public class kpStopScript extends Task
{
    private static boolean stopScript = false;

    public static void Stop()
    {
        stopScript = true;
    }

    @Override
    public boolean execute()
    {
        if (stopScript)
        {
            Log.severe("Stopping the script");
            return true;
        }

        return false;
    }
}
