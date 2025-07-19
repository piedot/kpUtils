package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.scene.Players;

import java.util.Arrays;

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

    @Deprecated // Not deprecated, just make sure you know you are withdrawing noted/unnoted
    public static boolean Withdraw(String itemName, int quantity)
    {
        return Withdraw(itemName, quantity, false);
    }

    public static boolean Withdraw(String itemName, int quantity, boolean noted)
    {
        return Withdraw(new String[]{itemName}, quantity, noted);
    }

    public static boolean Withdraw(String[] itemNames, int quantity, boolean noted)
    {
        Bank.Location bankLocation = Bank.Location.getNearestWithdrawable();
        if (!Open(bankLocation))
        {
            Log.info("Withdraw - opening bank at " + bankLocation);
            return true;
        }

        if (Inventories.backpack().isFull())
        {
            Log.info("Withdraw - backpack is full, depositing inventory");
            Inventories.bank().depositInventory();
            return true;
        }

        Bank.WithdrawMode withdrawMode = noted ? Bank.WithdrawMode.NOTED : Bank.WithdrawMode.ITEM;

        if (!Inventories.bank().getWithdrawMode().equals(withdrawMode))
        {
            Log.info("Withraw - Setting withdraw mode to " + withdrawMode);
            Inventories.bank().setWithdrawMode(withdrawMode);
        }

        Item bankItem = Inventories.bank().query().names(itemNames).filter(i -> !i.isPlaceholder()).results().first();

        if (bankItem != null && bankItem.getStackSize() >= quantity)
        {
            Log.info("Withdraw " + quantity + " of " + itemNames);
            Inventories.bank().withdraw(bankItem.getName(), quantity);

            return true;
        }

        Log.warn("Withdraw - bank doesn't contain " + quantity + " of " + Arrays.toString(itemNames));

        return false;
    }

    public static boolean Withdraw(int[] itemIds, int quantity, boolean noted)
    {
        Bank.Location bankLocation = Bank.Location.getNearestWithdrawable();
        if (!Open(bankLocation))
        {
            Log.info("Withdraw - opening bank at " + bankLocation);
            return true;
        }

        if (Inventories.backpack().isFull())
        {
            Log.info("Withdraw - backpack is full, depositing inventory");
            Inventories.bank().depositInventory();
            return true;
        }

        Bank.WithdrawMode withdrawMode = noted ? Bank.WithdrawMode.NOTED : Bank.WithdrawMode.ITEM;

        if (!Inventories.bank().getWithdrawMode().equals(withdrawMode))
        {
            Log.info("Withraw - Setting withdraw mode to " + withdrawMode);
            Inventories.bank().setWithdrawMode(withdrawMode);
        }

        Item bankItem = Inventories.bank().query().ids(itemIds).filter(i -> !i.isPlaceholder()).results().first();

        if (bankItem != null)
        {
            Log.info("Withdraw " + quantity + " of " + Arrays.toString(itemIds));
            Inventories.bank().withdraw(i -> i.ids(itemIds).results().first(), quantity);
            return true;
        }

        Log.warn("Withdraw - bank doesn't contain " + quantity + " of " + Arrays.toString(itemIds));

        return false;
    }

    @Deprecated // Not deprecated, just make sure you know you are withdrawing noted/unnoted
    public static boolean WithdrawAll(String itemName)
    {
        return WithdrawAll(itemName, false);
    }

    public static boolean WithdrawAll(String itemName, boolean noted)
    {
        return WithdrawAll(new String[]{itemName}, noted);
    }

    public static boolean WithdrawAll(String[] itemNames, boolean noted)
    {
        Bank.Location bankLocation = Bank.Location.getNearestWithdrawable();
        if (!Open(bankLocation))
        {
            Log.info("WithdrawAll - opening bank at " + bankLocation);
            return true;
        }

        if (Inventories.backpack().isFull())
        {
            Log.info("Withdraw All- backpack is full, depositing inventory");
            Inventories.bank().depositInventory();
            return true;
        }

        Bank.WithdrawMode withdrawMode = noted ? Bank.WithdrawMode.NOTED : Bank.WithdrawMode.ITEM;

        if (!Inventories.bank().getWithdrawMode().equals(withdrawMode))
        {
            Log.info("Withraw - Setting withdraw mode to " + withdrawMode);
            Inventories.bank().setWithdrawMode(withdrawMode);
        }

        Item bankItem = Inventories.bank().query().filter(i -> !i.isPlaceholder()).names(itemNames).results().first();

        if (bankItem != null)
        {
            Log.info("Withdraw all " + itemNames);
            Inventories.bank().withdrawAll(item -> item.names(itemNames).results().first());

            return true;
        }

        Log.warn("WithdrawAll - bank doesn't contain " + Arrays.toString(itemNames));

        return false;
    }

    @Deprecated // Not deprecated, just make sure you know you are withdrawing noted/unnoted
    public static boolean WithdrawAll(int itemId)
    {
        return WithdrawAll(itemId, false);
    }

    public static boolean WithdrawAll(int itemId, boolean noted)
    {
        return WithdrawAll(new int[]{itemId}, noted);
    }

    public static boolean WithdrawAll(int[] itemIds, boolean noted)
    {
        Bank.Location bankLocation = Bank.Location.getNearestWithdrawable();
        if (!Open(bankLocation))
        {
            Log.info("WithdrawAll - opening bank at " + bankLocation);
            return true;
        }

        if (Inventories.backpack().isFull())
        {
            Log.info("Withdraw All- backpack is full, depositing inventory");
            Inventories.bank().depositInventory();
            return true;
        }

        Bank.WithdrawMode withdrawMode = noted ? Bank.WithdrawMode.NOTED : Bank.WithdrawMode.ITEM;

        if (!Inventories.bank().getWithdrawMode().equals(withdrawMode))
        {
            Log.info("Withraw - Setting withdraw mode to " + withdrawMode);
            Inventories.bank().setWithdrawMode(withdrawMode);
        }

        Item bankItem = Inventories.bank().query().ids(itemIds).results().first();

        if (bankItem != null)
        {
            Log.info("Withdraw all " + itemIds);
            Inventories.bank().withdrawAll(item -> item.ids(itemIds).results().first());

            return true;
        }

        Log.warn("WithdrawAll - bank doesn't contain " + itemIds);

        return false;
    }

    public static int GetCount(String itemName, boolean nameContains)
    {
        return GetCount(new String[]{itemName}, nameContains);
    }

    /**
     * Includes both noted and unnoted?
     * @param itemNames
     * @return
     */
    public static int GetCount(String[] itemNames, boolean nameContains)
    {
        if (nameContains)
        {
            return Inventories.bank().query()
                    .nameContains(itemNames)
                    .filter(i -> !i.isPlaceholder())
                    .results()
                    .stream()
                    .map(Item::getStackSize)
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        return Inventories.bank().query()
                .names(itemNames)
                .filter(i -> !i.isPlaceholder())
                .results()
                .stream()
                .map(Item::getStackSize)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public static int GetCount(int itemId)
    {
        return GetCount(new int[]{itemId});
    }

    public static int GetCount(int[] itemIds)
    {
        return Inventories.bank().query()
                .ids(itemIds)
                .filter(i -> !i.isPlaceholder())
                .results()
                .stream()
                .map(Item::getStackSize)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
