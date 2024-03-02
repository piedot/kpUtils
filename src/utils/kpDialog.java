package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.component.Dialog;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.query.component.ComponentQuery;

import java.util.Arrays;
import java.util.List;

public class kpDialog
{
    public static void ProcessAndSelect(String ... options)
    {
        if (Continue())
            return;

        InterfaceComponent dialogOption = Dialog.getChatOptions().actions(options).results().first();

        if (dialogOption == null)
        {
            Log.info("Failed to continue dialogue with " + Arrays.toString(options));
            return;
        }

        Log.info("Processing chat option " + dialogOption.getText() + " index " + dialogOption.getIndex());

        Dialog.process(dialogOption.getIndex());

        return;
    }

    public static boolean Continue()
    {
        if (Dialog.canContinue())
        {
            kpTaskQueue.AddTask(new kpTaskQueue.Task("Continue dialogue", 10, () -> {
                Dialog.processContinue();
            }), 0);

            return true;
        }

        return false;
    }
}
