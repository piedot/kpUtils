package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import ids.ItemId;
import org.rspeer.commons.Communication;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.commons.math.Random;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.Trade;
import org.rspeer.game.component.Worlds;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Players;
import org.rspeer.game.service.stockmarket.StockMarketService;

import java.util.Set;

public class kpMuling
{
    private static boolean goTradeWithMule = false; // After selling and withdrawing all our items, force us to go trade with the mule
    private static boolean soldItems = false; //
    private static int oldWorld = -1; // World to hop back to after muling (our old world)
    private static int sleepTicks = -1;

    public static void Reset()
    {
        goTradeWithMule = false;
        soldItems = false;
    }

    /**
     * @param mulePosition mule global world position
     * @param muleWorld    mule world (300+)
     * @param muleName     mule name (case-sensitive)
     * @param botPoolId    bot pool id
     * @return true if we are running muling, false once we are finished
     */
    public static boolean Run(
            Position mulePosition, int muleWorld, String muleName, String botPoolId,
            int moneyToKeep, String[] itemsToTradeWithMule, Set<String> itemsToSell, Communication.WebSocketClient webSocketClient, StockMarketService stockMarketService
    )
    {
        int localWorld = Worlds.getLocal();

        if (goTradeWithMule)
        {
            try
            {
                webSocketClient.broadcast("bot:" + Global.Data.localPlayer.getName() + ":" + botPoolId);
            } catch (JsonProcessingException e)
            {
                throw new RuntimeException(e);
            }

            if (sleepTicks > 0)
            {
                Log.info("Sleeping (muling) for " + sleepTicks + " ticks");
                sleepTicks--;
                return true;
            }

            if (muleName == null || muleName.isBlank())
            {
                Log.severe("Invalid mule name " + muleName);
                return true;
            }

            Log.info("Going to trade with " + muleName);

            if (oldWorld == -1)
            {
                Log.info("Saving old world " + localWorld);
                oldWorld = localWorld;
            }

            if (muleWorld < 300 || muleWorld > 600)
            {
                Log.severe("Invalid mule world " + muleWorld);
                return true;
            }

            if (localWorld != muleWorld)
            {
                Log.info("Hopping to mule world " + muleWorld);
                kpWorlds.Hop(muleWorld);
                return true;
            }

            Trade.View currentTradeView = Trade.getView();
            if (currentTradeView.equals(Trade.View.FIRST_SCREEN))
            {
                if (moneyToKeep > 0)
                {
                    Item tradeMoney = Trade.Outgoing.getInventory().query().ids(ItemId.COINS).results().first();
                    int moneyInTrade = tradeMoney != null ? tradeMoney.getStackSize() : 0;
                    Item inventoryMoney = Inventories.backpack().query().ids(ItemId.COINS).results().first();
                    int moneyInInventory = inventoryMoney != null ? inventoryMoney.getStackSize() : 0;

                    int moneyToAdd = moneyInInventory - moneyToKeep;
                    boolean shouldAddMoney = moneyToAdd > 0;

                    Log.info("Trade money: " + moneyInTrade + ", inventory money: " + moneyInInventory + ", money to keep: " + moneyToKeep + ", should add money: " + shouldAddMoney);

                    if (moneyInInventory < moneyToKeep && moneyInInventory + moneyInTrade > moneyToKeep)
                    {
                        Log.info("Added too much money to the trade (lag?), declining.");
                        Trade.decline();
                        return true;
                    }

                    if (shouldAddMoney)
                    {
                        Log.info("Adding " + moneyToAdd + " of coins to the trade");
                        Trade.offer(i -> inventoryMoney, moneyToAdd);
                        return true;
                    }

                    Log.info("Not adding any more money to the trade...");
                }
                else
                {
                    Item coins = Inventories.backpack().query().ids(ItemId.COINS).results().first();
                    if (coins != null)
                    {
                        Log.info("Adding all coins to trade");
                        Trade.offer(i -> coins, coins.getStackSize());
                        return true;
                    }
                    Log.info("No more coins to trade...");
                }

                Item itemToAddToTrade = Inventories.backpack().query().names(itemsToTradeWithMule).filter(i -> i.getId() != ItemId.COINS).results().first();

                if (itemToAddToTrade == null)
                {
                    Log.info("Finished adding all items to the trade, accepting");
                    Trade.accept();
                    sleepTicks = Random.nextInt(5, 10);
                    return true;
                }

                Log.info("Adding " + itemToAddToTrade.getStackSize() + " of " + itemToAddToTrade.getName() + " to the trade");
                Trade.offer(i -> itemToAddToTrade, itemToAddToTrade.getStackSize());
                return true;
            }

            if (currentTradeView.equals(Trade.View.SECOND_SCREEN))
            {
                Log.info("Accepting second trade screen");
                Trade.accept();
                sleepTicks = Random.nextInt(5, 10);
                return true;
            }

            Player mule = Players.query().names(muleName).filter(p -> !p.equals(Players.self())).results().first();
            if (mule == null || mule.distance(Distance.PATHING) > 10)
            {
                if (mulePosition != null && mulePosition.distance() > 0)
                {
                    Log.info("Walking to mule position " + mulePosition);
                    Movement.walkTo(mulePosition);
                    return true;
                }

                Log.severe("No mule found, waiting...");
                return true;
            }

            Log.info("Interacting with mule");
            mule.interact("Trade with");
            sleepTicks = Random.nextInt(5, 10);
            return true;
        }

        if (oldWorld != -1)
        {
            if (oldWorld == localWorld)
            {
                Log.info("Finished muling & returning to our old world " + oldWorld);
                oldWorld = -1;
                return false;
            }

            Log.info("Hopping back to old world " + oldWorld);
            kpWorlds.Hop(oldWorld);
            return true;
        }

        if (!soldItems)
        {
            Log.info("Submitting item selling request (muling)");
            kpSellItems.StartSelling(stockMarketService, itemsToSell, false);
            soldItems = true;
            return true;
        }

        Log.info("Getting items to trade with the mule");

        if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
        {
            Log.info("Opening bank");
            return true;
        }

        for (String itemToTradeWithMule : itemsToTradeWithMule)
        {
            if (kpBank.WithdrawAll(itemToTradeWithMule, true))
            {
                Log.info("Successfully withdrew " + itemToTradeWithMule);
                return true;
            }
        }

        Log.info("Withdrew all items, now going to trade with the mule");
        goTradeWithMule = true;
        return true;
    }
}
