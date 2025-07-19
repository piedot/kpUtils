package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.Keyboard;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.component.Dialog;
import org.rspeer.game.component.InterfaceComposite;
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

        for (InterfaceComponent component : components)
        {
            if (component == null)
                continue;

            String text = component.getText();

            if (options.contains(text))
            {
                Log.info("Processing chat option " + text);
                component.interact("Continue");
                return true;
            }
        }

        return false;
    }

    public static boolean SelectContains(List<String> options)
    {
        List<InterfaceComponent> components = Dialog.getChatOptions().actions().getDefaultProvider().get();

        for (InterfaceComponent component : components)
        {
            if (component == null)
                continue;

            String text = component.getText();

            for (String option : options)
            {
                if (text.contains(option))
                {
                    Log.info("Processing chat option " + text);
                    component.interact("Continue");
                    return true;
                }
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
            Log.warn("TypeCharges text was not visible");
            return false;
        }

        String text = dialogText.getText();

        if (text == null || text.isEmpty() || text.isBlank())
        {
            Log.warn("TypeCharges text was null or empty");
            return false;
        }

        // How many charges would you like to add? (0 - 2,500)

        if (!text.contains("How many charges would you like to add?"))
        {
            Log.warn("TypeValue didn't contain text");
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
            Log.warn("Failed to find charges, <= 0");
            return false;
        }

        Keyboard.sendText(String.valueOf(maxChargesToAdd), true);

        return true;
    }

    @Deprecated // Will change its name once I create a function for all charge prompts
    public static boolean TypeChargesNew()
    {
        InterfaceComponent dialogText = Interfaces.getDirect(CHARGE_DIALOGUE_ID, 42);

        if (dialogText == null || !dialogText.isVisible())
        {
            Log.warn("TypeCharges text was not visible");
            return false;
        }

        String text = dialogText.getText();

        if (text == null || text.isEmpty() || text.isBlank())
        {
            Log.warn("TypeCharges text was null or empty");
            return false;
        }

        // How many charges do you wish to add? (0 - 2,500)

        if (!text.contains("How many charges do you wish to add?"))
        {
            Log.warn("TypeValue didn't contain text");
            return false;
        }

        text = text.replace(",", ""); // Remove "," from for example "2,500"

        Pattern pattern = Pattern.compile("How many charges do you wish to add\\? \\(0 - (\\d+)\\)");

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
            Log.warn("Failed to find charges, <= 0");
            return false;
        }

        Keyboard.sendText(String.valueOf(maxChargesToAdd), true);

        return true;
    }

    public static boolean TypeRingOfSufferingCharges(int charges)
    {
        InterfaceComponent dialogText = Interfaces.getDirect(CHARGE_DIALOGUE_ID, 42);

        if (dialogText == null || !dialogText.isVisible())
        {
            Log.warn("TypeRingOfSufferingCharges text was not visible");
            return false;
        }

        String text = dialogText.getText();

        if (text == null || text.isEmpty() || text.isBlank())
        {
            Log.warn("TypeRingOfSufferingCharges text was null or empty");
            return false;
        }

        if (!text.equals("How many rings do you wish to use?"))
        {
            Log.warn("TypeRingOfSufferingCharges text was not 'How many rings do you wish to use?'");
            return false;
        }

        Keyboard.sendText(String.valueOf(charges), true);

        return true;
    }

    public static boolean TypeValue(int value)
    {
        InterfaceComponent dialogInput = Interfaces.getDirect(InterfaceComposite.CHATBOX, 43);
        if (dialogInput == null || !dialogInput.isVisible() || !dialogInput.getText().contains("*"))
        {
            Log.warn("TypeValue input was not visible");
            return false;
        }
        Keyboard.sendText(String.valueOf(value), true);
        return true;
    }
}
