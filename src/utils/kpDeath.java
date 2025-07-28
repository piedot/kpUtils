package utils;

import Main.OnTick;
import ids.ItemId;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.game.GravestoneTimer;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Region;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.*;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.SceneObjects;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class kpDeath
{
    public static boolean handlingDeath = false; // Needed to remember that we need to claim an item from death
    public static Position gravePosition = null;

    public static boolean HandleDeath(Set<String> itemsToSell)
    {
        String deathHandlingType = OnTick.config.getOtherConfig().getDeathHandlingType();

        switch (deathHandlingType)
        {
            case "NONE":
                return false;
            case "Death's Domain":
                return HandleDeathsDomain(itemsToSell);
            case "Grave":
                return HandleGrave(itemsToSell);
        }

        return false;
    }

    private static boolean HandleGrave(Set<String> itemsToSell)
    {
        if (!GravestoneTimer.isActive())
        {
            gravePosition = null;
            return false;
        }

        int minutes = GravestoneTimer.getMinutes();
        Log.debug("Minutes left death grave: " + minutes);
        if (minutes < 2)
        {
            Log.info("Death timer too low, aborting");
            kpStopScript.Stop();
            return true;
        }

        Log.info("Handling death (Grave)");

        Position tempGravePosition = WorldMap.getGravestonePosition();

        if (tempGravePosition != null)
        {
            gravePosition = tempGravePosition;
        }

        if (gravePosition == null)
        {
            Log.info("Opening the world map for the grave position");
            WorldMap.open();
            return true;
        }

        if (WorldMap.isOpen())
        {
            Log.info("Closing the world map");
            WorldMap.close();
            return true;
        }

        final int[] fairyRingStaffIds = new int[]{ItemId.DRAMEN_STAFF, ItemId.LUNAR_MOONCLAN_LIMINAL_STAFF};
        Item fairyRingStaffEquipment = Inventories.equipment().query().ids(fairyRingStaffIds).results().first();
        if (fairyRingStaffEquipment == null)
        {
            // Require a fairy staff, otherwise the journey will be dangerous and risks losing all items from our grave
            Item fairyRingStaff = kpUtils.GetItem(fairyRingStaffIds, false, 1, 1, false, null);

            if (fairyRingStaff == null)
            {
                Log.info("Trying to get fairy ring staff for travel ids: " + Arrays.toString(fairyRingStaffIds));
                return true;
            }
        }

        String dialogueText = Dialog.getText();
        if (dialogueText != null && dialogueText.contains("You need") && dialogueText.contains("to retrieve your items."))
        {
            Log.info("Not enough GP for grave, selling items");
            kpSellItems.StartSelling(itemsToSell, true);
            return true;
        }

        if (gravePosition.distance(Distance.CHEBYSHEV) > 7) // Grave interaction range is 7
        {
            Log.info("Walking to the grave");
            Movement.walkTo(gravePosition);
            return true;
        }

        final String GRAVE_LOOT_ACTION = "Loot";
        Npc grave = Npcs.query().nameContains("Grave").actions(GRAVE_LOOT_ACTION).results().first();

        if (grave == null)
        {
            Log.severe("We are at the gravestone location but no grave was found");
            return true;
        }

        if (Inventories.backpack().isFull())
        {
            Log.info("Equipping equipment to make room");
            OnTick.config.getEquipmentConfig().getEquipmentLoadout().equip();
            return true;
        }

        Log.info("Interacting with the grave");
        grave.interact(GRAVE_LOOT_ACTION);
        return true;
    }

    private static boolean HandleDeathsDomain(Set<String> itemsToSell)
    {
        int playerRegion = Region.fromPosition(Global.Data.localPosition.fromInstance()).getId();

        handlingDeath |= GravestoneTimer.isActive();

        if (!handlingDeath && playerRegion != 12633)
        {
            gravePosition = null;
            return false;
        }

        Log.info("Handling death (Death's Domain)");

        if (playerRegion != 12633)
        {
            SceneObject deathsDomain = SceneObjects.query().nameContains("Death's Domain").within(16).results().first();
            if (deathsDomain == null)
            {
                Log.warn("Death's Domain not found");
                kpMovement.WalkTo(Position.from(3239, 3196, 0));
                return true;
            }
            kpUtils.SafeInteractWith(deathsDomain, "Enter");
            return true;
        }

        Log.info("In Death's domain");

        String dialogText = Dialog.getText();

        Log.info("Dialog text: " + dialogText);
        // Dialog text: You don't have enough money for the fees. You can<br>sacrifice unwanted items to <col=7f0000>Death's Coffer</col> to help pay<br>these fees.
        if (dialogText != null && dialogText.contains("You don't have enough money for the fees. You can"))
        {
            Log.info("Not enough GP to pay the death fee");
            kpSellItems.StartSelling(itemsToSell, true);
            return true;
        }

        if (kpDialog.Continue())
        {
            Log.info("Continuing dialog");
            return true;
        }

        final List<String> deathsDomainOptions = List.of(
                "Can I collect the items from that gravestone now?",
                "Bring my items here now; I'll pay your fee.",
                "Pay Death's fee.",
                "Yes, have you got anything for me?"
        );
        if (kpDialog.Select(deathsDomainOptions))
        {
            Log.info("Selecting dialog options");
            return true;
        }

        InterfaceComponent itemRetrievalTextComponent = Interfaces.getDirect(InterfaceComposite.DEATH_ITEM_RETRIEVAL, 1, 1);

        if (itemRetrievalTextComponent != null &&
                itemRetrievalTextComponent.isVisible() &&
                itemRetrievalTextComponent.getText().equals("Death's Office Item Retrieval <col=ffb83f>(0/120)</col>"))
        {
            handlingDeath = false;
            SceneObject portal = SceneObjects.query().nameContains("Portal").results().first();
            kpUtils.SafeInteractWith(portal, "Use");
            return true;
        }

        InterfaceComponent takeItemsButton = Interfaces.getDirect(InterfaceComposite.DEATH_ITEM_RETRIEVAL, 10);
        if (takeItemsButton != null && takeItemsButton.isVisible())
        {
            Log.info("Take-All button is visible");

            if (Inventories.backpack().isFull())
            {
                Log.info("Equipping equipment to make room");
                OnTick.config.getEquipmentConfig().getEquipmentLoadout().equip();
                return true;
            }

            Log.info("Taking items");
            takeItemsButton.interact("Take-All");
            return true;
        }

        if (Global.Data.localPlayer.isMoving())
        {
            Log.info("Moving");
            return true;
        }

        Npc death = Npcs.query().nameContains("Death").results().first();
        if (death == null)
        {
            Log.severe("Death not found");
            return true;
        }

        Log.info("Interacting with Death");
        death.interact("Talk-to");
        return true;
    }
}
