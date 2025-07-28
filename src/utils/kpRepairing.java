package utils;

import ids.ItemId;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Region;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Dialog;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.movement.pathfinding.Collisions;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.service.stockmarket.StockMarketService;

import java.util.List;
import java.util.Set;

public class kpRepairing
{
    private static final int[] brokenEquipmentIds = new int[]{
            // Debugging
            //ItemId.BARROWS_TORAG_LEGS_100,
            //ItemId.BARROWS_TORAG_BODY_100,
            //ItemId.BARROWS_TORAG_HEAD_100,
            //ItemId.BARROWS_TORAG_LEGS_75,
            //ItemId.BARROWS_TORAG_BODY_75,
            //ItemId.BARROWS_TORAG_HEAD_75,

            ItemId.BLOOD_MOON_HELM_BROKEN,
            ItemId.BLOOD_MOON_CHESTPLATE_BROKEN,
            ItemId.BLOOD_MOON_TASSETS_BROKEN,

            ItemId.FROST_MOON_HELM_BROKEN,
            ItemId.FROST_MOON_CHESTPLATE_BROKEN,
            ItemId.FROST_MOON_TASSETS_BROKEN,

            ItemId.ECLIPSE_MOON_HELM_BROKEN,
            ItemId.ECLIPSE_MOON_CHESTPLATE_BROKEN,
            ItemId.ECLIPSE_MOON_TASSETS_BROKEN,

            ItemId.BARROWS_AHRIM_HEAD_BROKEN,
            ItemId.BARROWS_AHRIM_HEAD_25,
            ItemId.BARROWS_AHRIM_BODY_BROKEN,
            ItemId.BARROWS_AHRIM_BODY_25,
            ItemId.BARROWS_AHRIM_LEGS_BROKEN,
            ItemId.BARROWS_AHRIM_LEGS_25,
            ItemId.BARROWS_AHRIM_WEAPON_BROKEN,
            ItemId.BARROWS_AHRIM_WEAPON_25,

            ItemId.BARROWS_DHAROK_HEAD_BROKEN,
            ItemId.BARROWS_DHAROK_HEAD_25,
            ItemId.BARROWS_DHAROK_BODY_BROKEN,
            ItemId.BARROWS_DHAROK_BODY_25,
            ItemId.BARROWS_DHAROK_LEGS_BROKEN,
            ItemId.BARROWS_DHAROK_LEGS_25,
            ItemId.BARROWS_DHAROK_WEAPON_BROKEN,
            ItemId.BARROWS_DHAROK_WEAPON_25,

            ItemId.BARROWS_GUTHAN_HEAD_BROKEN,
            ItemId.BARROWS_GUTHAN_HEAD_25,
            ItemId.BARROWS_GUTHAN_BODY_BROKEN,
            ItemId.BARROWS_GUTHAN_BODY_25,
            ItemId.BARROWS_GUTHAN_LEGS_BROKEN,
            ItemId.BARROWS_GUTHAN_LEGS_25,
            ItemId.BARROWS_GUTHAN_WEAPON_BROKEN,
            ItemId.BARROWS_GUTHAN_WEAPON_25,

            ItemId.BARROWS_KARIL_HEAD_BROKEN,
            ItemId.BARROWS_KARIL_HEAD_25,
            ItemId.BARROWS_KARIL_BODY_BROKEN,
            ItemId.BARROWS_KARIL_BODY_25,
            ItemId.BARROWS_KARIL_LEGS_BROKEN,
            ItemId.BARROWS_KARIL_LEGS_25,
            ItemId.BARROWS_KARIL_WEAPON_BROKEN,
            ItemId.BARROWS_KARIL_WEAPON_25,

            ItemId.BARROWS_TORAG_HEAD_BROKEN,
            ItemId.BARROWS_TORAG_HEAD_25,
            ItemId.BARROWS_TORAG_BODY_BROKEN,
            ItemId.BARROWS_TORAG_BODY_25,
            ItemId.BARROWS_TORAG_LEGS_BROKEN,
            ItemId.BARROWS_TORAG_LEGS_25,
            ItemId.BARROWS_TORAG_WEAPON_BROKEN,
            ItemId.BARROWS_TORAG_WEAPON_25,

            ItemId.BARROWS_VERAC_HEAD_BROKEN,
            ItemId.BARROWS_VERAC_HEAD_25,
            ItemId.BARROWS_VERAC_BODY_BROKEN,
            ItemId.BARROWS_VERAC_BODY_25,
            ItemId.BARROWS_VERAC_LEGS_BROKEN,
            ItemId.BARROWS_VERAC_LEGS_25,
            ItemId.BARROWS_VERAC_WEAPON_BROKEN,
            ItemId.BARROWS_VERAC_WEAPON_25,
    };

    private static int sleepTicks = 0; // Repair takes a tick to appear in bob's axes dialogue

    public static boolean RepairBrokenEquipment(Set<String> itemsToSell, StockMarketService stockMarketService)
    {
        if (sleepTicks > 0)
        {
            Log.info("Repair - Sleeping for " + sleepTicks + " tick(s)");
            sleepTicks--;
            return true;
        }

        Item brokenEquipmentItem = Inventories.equipment().query().ids(brokenEquipmentIds).results().first();

        if (brokenEquipmentItem != null)
        {
            if (!kpBank.Open(Bank.Location.getNearestWithdrawable()))
            {
                Log.info("Repair - Opening bank");
                return true;
            }

            Log.info("Repair - Depositing equipment");
            Inventories.bank().depositEquipment();
            return true;
        }

        Item brokenBankItem = Inventories.bank().query().ids(brokenEquipmentIds).results().first();

        boolean maxedOutBackpack = Inventories.backpack().query().ids(brokenEquipmentIds).results().size() == 27;
        if (brokenBankItem != null && !maxedOutBackpack)
        {
            Log.info("Repair - Withdrawing " + brokenBankItem.getName() + " from bank");
            kpBank.WithdrawAll(brokenEquipmentIds, false);
            return true;
        }

        Item brokenBackpackItem = Inventories.backpack().query().ids(brokenEquipmentIds).results().first();

        if (brokenBackpackItem != null)
        {
            Item bankCoins = Inventories.bank().query().ids(ItemId.COINS).results().first();

            if (bankCoins != null)
            {
                Log.info("Repair - Withdrawing coins from bank");
                kpBank.WithdrawAll(new int[]{ItemId.COINS}, false);
                return true;
            }

            if (Inventories.backpack().query().ids(new int[]{ItemId.COINS}).results().isEmpty())
            {
                if (!kpBank.WithdrawAll(new int[]{ItemId.COINS}, false))
                {
                    Log.info("Repair - Failed to withdraw coins");
                    kpSellItems.StartSelling(stockMarketService, itemsToSell, true); // Scuffed?
                    return true;
                }

                return true;
            }

            String dialogText = Dialog.getText();

            if (dialogText != null && dialogText.contains("I'll need ") && dialogText.contains(" coins to repair that."))
            {
                Log.severe("Ran out of coins for item repair");
                kpSellItems.StartSelling(stockMarketService, itemsToSell, true);
                return true;
            }

            if (kpDialog.Select(List.of("Repair")))
            {
                Log.info("Repair - Selecting repair");
                return true;
            }
            if (kpDialog.SelectContains(List.of("Repair all items:")))
            {
                Log.info("Repair - Selecting repair (all items)");
                return true;
            }
            if (kpDialog.SelectContains(List.of("Repair that item:")))
            {
                Log.info("Repair - Selecting repair (single item)");
                return true;
            }

            if (kpDialog.Continue())
            {
                Log.info("Repair - Continuing dialogue");
                return true;
            }

            Position bobShopInsidePosition = Position.from(3231, 3203, 0);

            Npc bob = Npcs.query().names("Bob").actions("Repair").results().first();

            if (bob == null || !Collisions.canReach(bobShopInsidePosition))
            {
                Log.info("Repair - Walking to Bob");
                WalkToLumbridge(stockMarketService);
                return true;
            }

            Log.info("Repair - Interacting with Bob");
            bob.interact("Repair");
            sleepTicks = 1;
            return true;
        }

        return false;
    }

    private static final Position lumbridgePosition = Position.from(3235, 3203, 0); //

    /**
     * Walks to Lumbridge, will retrieve items to teleport from the bank if needed.
     */
    public static void WalkToLumbridge(StockMarketService stockMarketService)
    {
        if (Region.fromPosition(Players.self().getPosition().fromInstance()).getId() == 12633) // Death's domain
        {
            SceneObject portal = SceneObjects.query().nameContains("Portal").results().first();
            kpUtils.SafeInteractWith(portal, "Use");
            return;
        }

        kpConfig.ConfigItem teleportItem = kpConfig.LUMBRIDGE_TELEPORT_TAB;
        if (lumbridgePosition.distance(Distance.CHEBYSHEV) > 64)
        {
            if (Game.getAccountType().isRegular())
            {
                Item lumbridgeTeleport = kpUtils.GetItem(teleportItem.getIds(), false, 1, 5, false, stockMarketService);

                if (lumbridgeTeleport == null)
                {
                    Log.info("Getting teleport item " + teleportItem.getName());
                    return;
                }
                Log.info("We have the lumbridge teleport");
            }
        }

        Log.info("Walking to lumbridge");
        kpMovement.WalkTo(lumbridgePosition);
        return;
    }
}
