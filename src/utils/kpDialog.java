package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.Keyboard;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.component.Dialog;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.query.component.ComponentQuery;

import java.util.Arrays;
import java.util.List;

public class kpDialog
{
    private final static int CHARGE_DIALOGUE_ID = 162;
    private final static int CHARGE_DIALOGUE_TEXT_ID = 41;

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
            Log.info("Processing dialogue");
            return Dialog.processContinue();
        }

        return false;
    }

    public static boolean TypeValue(int value)
    {
        InterfaceComponent dialogText = Interfaces.getDirect(CHARGE_DIALOGUE_ID, CHARGE_DIALOGUE_TEXT_ID);

        if (dialogText == null)
        {
            Log.info("Failed to find TypeValue dialog text");
            return false;
        }

        String text = dialogText.getText();

        if (text == null)
        {
            Log.info("TypeValue text was null");
            return false;
        }

        if (!text.contains("How many charges would you like to add?"))
        {
            Log.info("TypeValue didn't contain text");
            return false;
        }

        Keyboard.sendText(String.valueOf(value), true);

        return true;
    }
}
