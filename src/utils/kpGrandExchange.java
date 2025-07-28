package utils;

import ids.ItemId;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.Definitions;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.StockMarketTransaction;
import org.rspeer.game.adapter.component.StockMarketable;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.definition.ItemDefinition;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.Region;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.stockmarket.StockMarket;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.config.item.entry.setup.ItemEntrySetup;
import org.rspeer.game.config.item.loadout.BackpackLoadout;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.movement.pathfinding.Pathing;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.service.stockmarket.StockMarketEntry;
import org.rspeer.game.web.Web;

public class kpGrandExchange
{
    private static final Position grandExchangePosition = Position.from(3162, 3489, 0); //
    private static Area grandExchangeArea = Area.rectangular(3161, 3486, 3168, 3493, 0);

    public static boolean Open()
    {
        Player localPlayer = Players.self();

        if (localPlayer == null || localPlayer.isMoving())
            return false;

        if (!StockMarket.isOpen())
        {
            Log.info("GE is not open");
            if (grandExchangeArea.contains(localPlayer))
            {
                Log.info("Opening the GE");
                StockMarket.open();
            }
            else
            {
                Log.info("Walking to the GE");
                kpMovement.WalkTo(grandExchangeArea.getRandomTile());
            }
            return false;
        }

        return true;
    }

    public static void BuyItem(int itemId, int quantity)
    {
        Log.info("GE - Buying " + quantity + " of " + itemId);
        MakeOffer(StockMarketable.Type.BUY, itemId, quantity);
    }

    public static void SellItem(int itemId, int quantity)
    {
        Log.info("GE - Selling " + quantity + " of " + itemId);
        MakeOffer(StockMarketable.Type.SELL, itemId, quantity);
    }

    private static void MakeOffer(StockMarketable.Type type, int itemId, int quantity)
    {
        StockMarketTransaction transaction = StockMarket.query().ids(itemId).types(type).results().first(); // Will break if you had offers beforehand

        if (transaction != null)
        {
            if (transaction.getProgress().equals(StockMarketTransaction.Progress.FINISHED))
            {
                Log.info("Collecting offer id " + itemId);
                StockMarket.collectAll(false);

                return;
            }

            // todo failsafe timer, waited for too long, increase the price?, sometimes inubot seems to fail to collect the items as well :( but im collecting them myself so

            Log.info("Waiting for id '" + itemId + "' to buy/sell..");

            return;
        }

        Player localPlayer = Players.self();

        if (localPlayer == null)
        {
            Log.warn("GE - Local player was null");
            return;
        }

        if (localPlayer.isMoving())
        {
            Log.info("GE - moving");
            return;
        }

        if (!grandExchangeArea.contains(localPlayer))
        {
            Log.info("Moving to the GE");
            Movement.walkTo(grandExchangeArea.getRandomTile());

            return;
        }

        if (!Open())
        {
            return;
        }

        Log.info("Creating offer");

        StockMarket.createOffer(type)
                .item(itemId)
                .quantity(quantity)
                .price(value -> GetMultiplierPrice(itemId, quantity)) // todo: if we get "not enough money" in chat, stop the script
                .build()
                .createTransaction();

        Log.info("Done creating offer");

        return;
    }

    public static int GetMultiplierPrice(int itemId, int quantity)
    {
        int singularItemPrice = kpPrice.Get(itemId);
        double totalItemsPrice = singularItemPrice * quantity;

        if (totalItemsPrice < 0)
        {
            Log.warn("Price for " + itemId + " quantity " + quantity + " was negative");
            return 0;
        }

        // todo name hash random, if you return a different value to .price in .createOffer, it will not put the sale until the same price is provided twice

        if (totalItemsPrice > 100_000)
        {
            totalItemsPrice *= 2;
        }
        else if (totalItemsPrice > 10_000)
        {
            totalItemsPrice *= 3;
        }
        else if (totalItemsPrice > 1_000)
        {
            totalItemsPrice *= 8;
        }
        else
        {
            totalItemsPrice *= 20;
        }

        singularItemPrice = (int) (totalItemsPrice / quantity);

        return singularItemPrice;
    }
}
