package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.scene.Players;

public class kpBank
{
    public static boolean Open(Bank.Location location)
    {
        Player localPlayer = Players.self();

        if (localPlayer == null || localPlayer.isMoving())
            return false;

        if (!Bank.isOpen())
        {
            Log.info("Opening " + location.toString() + " bank");
            Bank.open(location);

            return false;
        }

        return true;
    }

    public static boolean Withdraw(String itemName, int quantity)
    {
        if (!Bank.isOpen())
        {
            Log.warn("Withdraw - bank isn't open");
            return true;
        }

        Item bankItem = Inventories.bank().query().names(itemName).filter(i -> !i.isPlaceholder()).results().first();

        if (bankItem != null && bankItem.getStackSize() >= quantity)
        {
            Log.info("Withdraw " + quantity + " " + itemName);
            Inventories.bank().withdraw(itemName, quantity);

            return true;
        }

        Log.warn("Withdraw - bank doesn't contain " + quantity + " of " + itemName);

        return false;
    }

    public static boolean WithdrawAll(String itemName)
    {
        if (!Bank.isOpen())
        {
            Log.warn("Withdraw - bank isn't open");
            return true;
        }

        Item bankItem = Inventories.bank().query().filter(i -> !i.isPlaceholder()).names(itemName).results().first();

        if (bankItem != null)
        {
            Log.info("Withdraw all " + itemName);
            Inventories.bank().withdrawAll(item -> item.names(itemName).results().first());

            return true;
        }

        Log.warn("WithdrawAll - bank doesn't contain " + itemName);

        return false;
    }
}
