package restock;

import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import utils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class kpRecharge
{
    private static class RechargeItem
    {
        int itemId;
        int quantity;
        boolean shouldBuy;

        public RechargeItem(int itemId, int quantity, boolean shouldBuy)
        {
            this.itemId = itemId;
            this.quantity = quantity;
            this.shouldBuy = shouldBuy;
        }
    }

    private static class kpRechargeData
    {
        String npcName;
        String npcInteraction;
        Area areaToTravelTo;
        List<String> dialogueOptions; // To be checked with .contains
        List<RechargeItem> rechargeItems;
        List<Integer> itemIdsForTravel;

        // Travel, talk, interact
        public kpRechargeData(String npcName, String npcInteraction, Area areaToTravelTo, List<String> dialogueOptions, List<RechargeItem> rechargeItems, List<Integer> itemIdsForTravel)
        {
            this.npcInteraction = npcInteraction;
            this.npcName = npcName;
            this.areaToTravelTo = areaToTravelTo;
            this.dialogueOptions = dialogueOptions;
            this.rechargeItems = rechargeItems;
            this.itemIdsForTravel = itemIdsForTravel;
        }

        // Buy items from the GE and use on the item
        public kpRechargeData(List<RechargeItem> rechargeItems, List<Integer> itemIdsForTravel)
        {
            this.rechargeItems = rechargeItems;
            this.itemIdsForTravel = itemIdsForTravel;
        }
    }

    private static kpRechargeData barrowsRepair = new kpRechargeData(
            "Bob",
            "Repair",
            Data.bobsBrilliantAxesShop,
            Arrays.asList("Repair all items: ", "Repair that item: "),
            Arrays.asList(new RechargeItem(Data.COINS, -1, false)),
            Arrays.asList(Data.LUMBRIDGE_TELEPORT, Data.VARROCK_TELEPORT)
    );

    private static kpRechargeData tridentOfTheSeasRecharge = new kpRechargeData(
            Arrays.asList(
                    new RechargeItem(Data.DEATH_RUNE, 2500, true),
                    new RechargeItem(Data.CHAOS_RUNE, 2500, true),
                    new RechargeItem(Data.FIRE_RUNE, 12500, true),
                    new RechargeItem(Data.COINS, 25000, false)),
            Arrays.asList(
                    Data.VARROCK_TELEPORT,
                    Data.RING_OF_WEALTH)
    );

    private static Map<String, kpRechargeData> rechargeDataMap = new HashMap<>(){{

        // Barrows

        put("Ahrim's hood", barrowsRepair);
        put("Ahrim's robetop", barrowsRepair);
        put("Ahrim's robeskirt", barrowsRepair);
        put("Ahrim's staff", barrowsRepair);

        put("Dharok's helm", barrowsRepair);
        put("Dharok's platebody", barrowsRepair);
        put("Dharok's platelegs", barrowsRepair);
        put("Dharok's greataxe", barrowsRepair);

        put("Guthan's helm", barrowsRepair);
        put("Guthan's platebody", barrowsRepair);
        put("Guthan's chainskirt", barrowsRepair);
        put("Guthan's warspear", barrowsRepair);

        put("Karil's coif", barrowsRepair);
        put("Karil's leathertop", barrowsRepair);
        put("Karil's leatherskirt", barrowsRepair);
        put("Karil's crossbow", barrowsRepair);

        put("Torag's helm", barrowsRepair);
        put("Torag's platebody", barrowsRepair);
        put("Torag's platelegs", barrowsRepair);
        put("Torag's hammers", barrowsRepair);

        put("Verac's helm", barrowsRepair);
        put("Verac's brassard", barrowsRepair);
        put("Verac's plateskirt", barrowsRepair);
        put("Verac's flail", barrowsRepair);

        // Powered staves

        put("Uncharged trident", tridentOfTheSeasRecharge);
        put("Trident of the seas", tridentOfTheSeasRecharge);

    }};

    // Todo
        /*
            Lumbridge Barrows repair
            Swamp & Seas trident recharge
            Blowpipe recharge + darts
        */

    public static void Restock(kpItemEntry itemEntry)
    {
        if (itemEntry == null)
            return;

        Player localPlayer = Players.self();

        if (localPlayer == null)
            return;

        if (localPlayer.isMoving())
            return;

        kpRechargeData rechargeData = rechargeDataMap.get(itemEntry.getKey());

        if (kpDialog.Continue())
        {
            return;
        }

        if (kpDialog.Select(rechargeData.dialogueOptions))
        {
            return;
        }

        if (kpDialog.TypeCharges())
        {
            return;
        }

        Npc npcToTalkTo = Npcs.query().names(rechargeData.npcName).actions(rechargeData.npcInteraction).reachable().results().first();

        if (npcToTalkTo != null)
        {
            npcToTalkTo.interact(rechargeData.npcInteraction);
            return;
        }



        // If npc to talk to is not null, talk to it

        // If inventory doesnt contain the items we need to have for travel, buy them

        // If We arent in the area, walk to it

        // If we dont have the items to charge, buy them

        // Use the charges on the item

        return;
    }
}
