package utils;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.StockMarketable;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Region;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import org.rspeer.game.service.stockmarket.StockMarketEntry;
import org.rspeer.game.service.stockmarket.StockMarketService;

import java.util.Set;

@TaskDescriptor(
        name = "kpSellItems",
        blocking = true,
        priority = 1005 // Until RestockTask loses its priority, we have to have a higher priority value to execute before it
)

public class kpSellItems extends Task
{
    private static boolean sellItems = false;
    private static boolean firstSellingCheck = false;
    private static Set<String> itemsToSell = null;
    private static StockMarketService stockMarketService = null;

    public static void StartSelling(StockMarketService stockMarketServiceIn, Set<String> itemsToSellIn, boolean stopIfNoItemsToSell)
    {
        stockMarketService = stockMarketServiceIn;
        itemsToSell = itemsToSellIn;
        sellItems = true;
        firstSellingCheck = stopIfNoItemsToSell;
        Log.info("Starting to sell items");
    }

    @Override
    public boolean execute()
    {
        if (!sellItems)
            return false;

        Log.info("=====");

        Log.info("Selling items...");

        kpMovement.ActivateRunEnergy(12);

        if (Region.fromPosition(Players.self().getPosition().fromInstance()).getId() == 12633) // Death's domain
        {
            SceneObject portal = SceneObjects.query().nameContains("Portal").results().first();
            kpUtils.SafeInteractWith(portal, "Use");
            return true;
        }

        if (!kpBank.Open(Bank.Location.GRAND_EXCHANGE))
        {
            Log.info("Opening bank");
            return true;
        }

        Item wrongInventoryItem = Inventories.backpack().query().filter(i -> !itemsToSell.contains(i.getName())).results().first();

        if (wrongInventoryItem != null)
        {
            Log.info("Depositing inventory (wrong item " + wrongInventoryItem.getName() + ")");
            Inventories.bank().depositInventory();
            return true;
        }

        Item bankItem = Inventories.bank().query().names(itemsToSell.toArray(String[]::new)).filter(i -> !i.isPlaceholder()).results().first();

        if (bankItem != null && !Inventories.backpack().isFull())
        {
            firstSellingCheck = false;
            Log.info("Withdrawing " + bankItem.getName() + " from bank");
            kpBank.WithdrawAll(bankItem.getName(), true);
            return true;
        }

        if (Inventories.backpack().isEmpty())
        {
            if (firstSellingCheck)
            {
                Log.severe("We had no items to sell, stopping script.");
                kpStopScript.Stop();
                return true;
            }

            Log.info("No items left to sell");
            sellItems = false;
            return true;
        }

        for (Item item : Inventories.backpack().query().results())
        {
            Log.info("Submitting sell offer for " + item.getName());
            firstSellingCheck = false;
            stockMarketService.submit(StockMarketable.Type.SELL, new StockMarketEntry(item.getId(), kpInventory.GetCount(new int[]{item.getId()}, null), -3));
        }

        Log.info("Done submitting sales");
        sellItems = false;
        return false;
    }
}
