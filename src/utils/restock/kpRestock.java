package utils.restock;

import org.rspeer.game.component.Item;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.web.Web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class kpRestock
{
    public class RestockItem
    {
        int itemId;
        int quantity;
        boolean shouldBuy;

        public RestockItem(int itemId, int quantity, boolean shouldBuy)
        {
            this.itemId = itemId;
            this.quantity = quantity;
            this.shouldBuy = shouldBuy;
        }
    }

    class kpRestockData
    {
        String npcName;
        String npcInteraction;
        Area areaToTravelTo;
        List<String> dialogueOptions; // To be checked with .contains
        List<RestockItem> restockItems;
        List<Integer> itemIdsForTravel;

        // Travel, talk, interact
        public kpRestockData(String npcName, String npcInteraction, Area areaToTravelTo, List<String> dialogueOptions, List<RestockItem> restockItems, List<Integer> itemIdsForTravel)
        {
            this.npcInteraction = npcInteraction;
            this.npcName = npcName;
            this.areaToTravelTo = areaToTravelTo;
            this.dialogueOptions = dialogueOptions;
            this.restockItems = restockItems;
            this.itemIdsForTravel = itemIdsForTravel;
        }

        // Buy items from the GE and use on the item
        public kpRestockData(List<RestockItem> restockItems, List<Integer> itemIdsForTravel)
        {
            this.restockItems = restockItems;
            this.itemIdsForTravel = itemIdsForTravel;
        }
    }

    kpRestockData barrowsRepair = new kpRestockData(
            "Bob",
            "Repair",
            Data.bobsBrilliantAxesShop,
            Arrays.asList("Repair all items: ", "Repair that item: "),
            Arrays.asList(new RestockItem(Data.COINS, -1, false)),
            Arrays.asList(Data.LUMBRIDGE_TELEPORT, Data.VARROCK_TELEPORT)
    );

    kpRestockData tridentOfTheSeasRecharge = new kpRestockData(
            Arrays.asList(
                    new RestockItem(Data.DEATH_RUNE, 2500, true),
                    new RestockItem(Data.CHAOS_RUNE, 2500, true),
                    new RestockItem(Data.FIRE_RUNE, 12500, true),
                    new RestockItem(Data.COINS, 25000, false)),
            Arrays.asList(
                    Data.VARROCK_TELEPORT,
                    Data.RING_OF_WEALTH)
    );

    Map<String, kpRestockData> restockData = new HashMap<>(){{

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


    public static void Restock(kpItemEntry itemEntry)
    {
        // Todo
        /*
            Lumbridge Barrows repair
            Swamp & Seas trident recharge
            Blowpipe recharge + darts
        */



        // Check interfaces for options to continue/press
        // Check interfaces for amount of charges, input max

        // If npc to talk to is not null, talk to it

        // If inventory doesnt contain the items we need to have for travel, buy them

        // If We arent in the area, walk to it

        // If we dont have the items to charge, buy them

        // Use the charges on the item

        return;
    }
}
