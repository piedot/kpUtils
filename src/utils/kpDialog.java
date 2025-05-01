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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class kpDialog
{
    private final static int CHARGE_DIALOGUE_ID = 162;
    private final static int CHARGE_DIALOGUE_TEXT_ID = 41;

    public static boolean Select(List<String> options)
    {
        List<InterfaceComponent> components = Dialog.getChatOptions().actions().getDefaultProvider().get();

        for (InterfaceComponent component: components)
        {
            if (component == null)
                continue;

            String text = component.getText();

            if (options.contains(text))
            {
                Log.info("Processing chat option " + text);

                component.interact("Continue"); // Todo confirm

                return true;
            }
        }

        return false;
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

    public static boolean TypeCharges()
    {
        InterfaceComponent dialogText = Interfaces.getDirect(CHARGE_DIALOGUE_ID, CHARGE_DIALOGUE_TEXT_ID);

        if (dialogText == null || !dialogText.isVisible())
        {
            Log.info("TypeCharges text was not visible");
            return false;
        }

        String text = dialogText.getText();

        if (text == null || text.isEmpty() || text.isBlank())
        {
            Log.info("TypeCharges text was null or empty");
            return false;
        }

        // How many charges would you like to add? (0 - 2,500)

        if (!text.contains("How many charges would you like to add?"))
        {
            Log.info("TypeValue didn't contain text");
            return false;
        }

        text = text.replace(",", ""); // Remove "," from for example "2,500"

        Pattern pattern = Pattern.compile("How many charges would you like to add\\? \\(0 - (\\d+)\\)");

        Matcher matcher = pattern.matcher(text);

        int maxChargesToAdd = -1;

        if (matcher.find())
        {
            maxChargesToAdd = Integer.parseInt(matcher.group(1));
        }
        else
        {
            Log.warn("Failed to find sanity");
            return false;
        }

        if (maxChargesToAdd <= 0)
        {
            Log.info("Failed to find charges, <= 0");
            return false;
        }

        Keyboard.sendText(String.valueOf(maxChargesToAdd), true);

        return true;
    }
}
