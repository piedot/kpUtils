package utils;

import org.rspeer.commons.PriceCheck;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.StockMarketTransaction;
import org.rspeer.game.adapter.component.StockMarketable;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.component.stockmarket.StockMarket;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Players;

public class kpGrandExchange
{
    private static Area grandExchangeArea = Area.rectangular(3161, 3486, 3168, 3493, 0);

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
                kpTaskQueue.AddTask(new kpTaskQueue.Task("Collecting offer id " + itemId, 10, () -> {
                    StockMarket.collectAll(false);
                }), 0);

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
            kpTaskQueue.AddTask(new kpTaskQueue.Task("Moving to the GE", 10, () -> {
                Movement.walkTo(grandExchangeArea.getRandomTile());
            }), 0);

            return;
        }

        if (!StockMarket.isOpen())
        {
            kpTaskQueue.AddTask(new kpTaskQueue.Task("Open the GE", 10, () -> {
                StockMarket.open();
            }), 0);

            return;
        }

        Log.info("Creating offer");

        StockMarket.createOffer(type)
                .item(itemId)
                .quantity(quantity)
                .price(value -> GetGuaranteedPrice(itemId, quantity)) // todo: if we get "not enough money" in chat, stop the script
                .build()
                .createTransaction();

        Log.info("Done creating offer");

        return;
    }

    // "Guaranteed" is an overstatement
    public static int GetGuaranteedPrice(int itemId, int quantity)
    {
        int singularItemPrice = PriceCheck.lookup(itemId).getAverage();
        double totalItemsPrice = singularItemPrice * quantity;

        if (totalItemsPrice < 0)
        {
            Log.warn("Price for " + itemId + " quantity " + quantity + " was negative");
            return 0;
        }

        // todo name hash random, if you return a different value to .price in .createOffer, it will not put the sale until the same price is provided twice

        if (totalItemsPrice > 100_000)
        {
            totalItemsPrice *= 3;
        }
        else if (totalItemsPrice > 10_000)
        {
            totalItemsPrice *= 5;
        }
        else if (totalItemsPrice > 1_000)
        {
            totalItemsPrice *= 10;
        }
        else
        {
            totalItemsPrice *= 20;
        }

        singularItemPrice = (int) (totalItemsPrice / quantity);

        return singularItemPrice;
    }

}
