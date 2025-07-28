package utils;

import Main.GrandExchangeWalker;
import Main.OnTick;
import ids.ItemId;
import kotlin.Triple;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Npcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class kpDecanting
{
    private static final String[][] potionNamesArrays = new String[][]{
            {"Attack potion(1)", "Attack potion(2)", "Attack potion(3)"},
            {"Antipoison(1)", "Antipoison(2)", "Antipoison(3)"},
            {"Strength potion(1)", "Strength potion(2)", "Strength potion(3)"},
            {"Compost potion(1)", "Compost potion(2)", "Compost potion(3)"},
            {"Restore potion(1)", "Restore potion(2)", "Restore potion(3)"},
            {"Energy potion(1)", "Energy potion(2)", "Energy potion(3)"},
            {"Defence potion(1)", "Defence potion(2)", "Defence potion(3)"},
            {"Agility potion(1)", "Agility potion(2)", "Agility potion(3)"},
            {"Combat potion(1)", "Combat potion(2)", "Combat potion(3)"},
            {"Prayer potion(1)", "Prayer potion(2)", "Prayer potion(3)"},
            {"Super attack(1)", "Super attack(2)", "Super attack(3)"},
            {"Superantipoison(1)", "Superantipoison(2)", "Superantipoison(3)"},
            {"Super energy(1)", "Super energy(2)", "Super energy(3)"},
            {"Hunter potion(1)", "Hunter potion(2)", "Hunter potion(3)"},
            {"Goading potion(1)", "Goading potion(2)", "Goading potion(3)"},
            {"Super strength(1)", "Super strength(2)", "Super strength(3)"},
            {"Prayer regeneration potion(1)", "Prayer regeneration potion(2)", "Prayer regeneration potion(3)"},
            {"Super restore(1)", "Super restore(2)", "Super restore(3)"},
            {"Sanfew serum(1)", "Sanfew serum(2)", "Sanfew serum(3)"},
            {"Super defence(1)", "Super defence(2)", "Super defence(3)"},
            {"Antidote+(1)", "Antidote+(2)", "Antidote+(3)"},
            {"Antifire potion(1)", "Antifire potion(2)", "Antifire potion(3)"},
            {"Divine super attack potion(1)", "Divine super attack potion(2)", "Divine super attack potion(3)"},
            {"Divine super defence potion(1)", "Divine super defence potion(2)", "Divine super defence potion(3)"},
            {"Divine super strength potion(1)", "Divine super strength potion(2)", "Divine super strength potion(3)"},
            {"Ranging potion(1)", "Ranging potion(2)", "Ranging potion(3)"},
            {"Divine ranging potion(1)", "Divine ranging potion(2)", "Divine ranging potion(3)"},
            {"Magic potion(1)", "Magic potion(2)", "Magic potion(3)"},
            {"Stamina potion(1)", "Stamina potion(2)", "Stamina potion(3)"},
            {"Zamorak brew(1)", "Zamorak brew(2)", "Zamorak brew(3)"},
            {"Divine magic potion(1)", "Divine magic potion(2)", "Divine magic potion(3)"},
            {"Antidote++(1)", "Antidote++(2)", "Antidote++(3)"},
            {"Bastion potion(1)", "Bastion potion(2)", "Bastion potion(3)"},
            {"Battlemage potion(1)", "Battlemage potion(2)", "Battlemage potion(3)"},
            {"Saradomin brew(1)", "Saradomin brew(2)", "Saradomin brew(3)"},
            {"Surge potion(1)", "Surge potion(2)", "Surge potion(3)"},
            {"Extended antifire(1)", "Extended antifire(2)", "Extended antifire(3)"},
            {"Ancient brew(1)", "Ancient brew(2)", "Ancient brew(3)"},
            {"Divine bastion potion(1)", "Divine bastion potion(2)", "Divine bastion potion(3)"},
            {"Divine battlemage potion(1)", "Divine battlemage potion(2)", "Divine battlemage potion(3)"},
            {"Anti-venom(1)", "Anti-venom(2)", "Anti-venom(3)"},
            {"Menaphite remedy(1)", "Menaphite remedy(2)", "Menaphite remedy(3)"},
            {"Super combat potion(1)", "Super combat potion(2)", "Super combat potion(3)"},
            {"Forgotten brew(1)", "Forgotten brew(2)", "Forgotten brew(3)"},
            {"Super antifire potion(1)", "Super antifire potion(2)", "Super antifire potion(3)"},
            {"Anti-venom+(1)", "Anti-venom+(2)", "Anti-venom+(3)"},
            {"Extended anti-venom+(1)", "Extended anti-venom+(2)", "Extended anti-venom+(3)"},
            {"Divine super combat potion(1)", "Divine super combat potion(2)", "Divine super combat potion(3)"},
            {"Extended super antifire(1)", "Extended super antifire(2)", "Extended super antifire(3)"}
    };


    private static final List<Triple<Integer, Integer, Integer>> potionIds = new ArrayList<>(List.of(
            new Triple<>(ItemId._1DOSE1ATTACK, ItemId._2DOSE1ATTACK, ItemId._3DOSE1ATTACK),
            new Triple<>(ItemId._1DOSEANTIPOISON, ItemId._2DOSEANTIPOISON, ItemId._3DOSEANTIPOISON),
            new Triple<>(ItemId.RELICYMS_BALM1, ItemId.RELICYMS_BALM2, ItemId.RELICYMS_BALM3),
            new Triple<>(ItemId._1DOSE1STRENGTH, ItemId._2DOSE1STRENGTH, ItemId._3DOSE1STRENGTH),
            new Triple<>(ItemId.SUPERCOMPOST_POTION_1, ItemId.SUPERCOMPOST_POTION_2, ItemId.SUPERCOMPOST_POTION_3),
            new Triple<>(ItemId._1DOSESTATRESTORE, ItemId._2DOSESTATRESTORE, ItemId._3DOSESTATRESTORE),
            new Triple<>(ItemId._1DOSE1ENERGY, ItemId._2DOSE1ENERGY, ItemId._3DOSE1ENERGY),
            new Triple<>(ItemId._1DOSE1DEFENSE, ItemId._2DOSE1DEFENSE, ItemId._3DOSE1DEFENSE),
            new Triple<>(ItemId._1DOSE1AGILITY, ItemId._2DOSE1AGILITY, ItemId._3DOSE1AGILITY),
            new Triple<>(ItemId._1DOSECOMBAT, ItemId._2DOSECOMBAT, ItemId._3DOSECOMBAT),
            new Triple<>(ItemId._1DOSEPRAYERRESTORE, ItemId._2DOSEPRAYERRESTORE, ItemId._3DOSEPRAYERRESTORE),
            new Triple<>(ItemId._1DOSE2ATTACK, ItemId._2DOSE2ATTACK, ItemId._3DOSE2ATTACK),
            new Triple<>(ItemId._1DOSE2ANTIPOISON, ItemId._2DOSE2ANTIPOISON, ItemId._3DOSE2ANTIPOISON),
            new Triple<>(ItemId._1DOSE2ENERGY, ItemId._2DOSE2ENERGY, ItemId._3DOSE2ENERGY),
            new Triple<>(ItemId._1DOSEHUNTING, ItemId._2DOSEHUNTING, ItemId._3DOSEHUNTING),
            new Triple<>(ItemId._1DOSEGOADING, ItemId._2DOSEGOADING, ItemId._3DOSEGOADING),
            new Triple<>(ItemId._1DOSE2STRENGTH, ItemId._2DOSE2STRENGTH, ItemId._3DOSE2STRENGTH),
            new Triple<>(ItemId._1DOSE1PRAYER_REGENERATION, ItemId._2DOSE1PRAYER_REGENERATION, ItemId._3DOSE1PRAYER_REGENERATION),
            new Triple<>(ItemId._1DOSE2RESTORE, ItemId._2DOSE2RESTORE, ItemId._3DOSE2RESTORE),
            new Triple<>(ItemId.SANFEW_SALVE_1_DOSE, ItemId.SANFEW_SALVE_2_DOSE, ItemId.SANFEW_SALVE_3_DOSE),
            new Triple<>(ItemId._1DOSE2DEFENSE, ItemId._2DOSE2DEFENSE, ItemId._3DOSE2DEFENSE),
            new Triple<>(ItemId.ANTIDOTE_1, ItemId.ANTIDOTE_2, ItemId.ANTIDOTE_3),
            new Triple<>(ItemId._1DOSE1ANTIDRAGON, ItemId._1DOSE1ANTIDRAGON, ItemId._1DOSE1ANTIDRAGON),
            new Triple<>(ItemId._1DOSEDIVINEATTACK, ItemId._2DOSEDIVINEATTACK, ItemId._3DOSEDIVINEATTACK),
            new Triple<>(ItemId._1DOSEDIVINEDEFENCE, ItemId._2DOSEDIVINEDEFENCE, ItemId._3DOSEDIVINEDEFENCE),
            new Triple<>(ItemId._1DOSEDIVINESTRENGTH, ItemId._2DOSEDIVINESTRENGTH, ItemId._3DOSEDIVINESTRENGTH),
            new Triple<>(ItemId._1DOSERANGERSPOTION, ItemId._2DOSERANGERSPOTION, ItemId._3DOSERANGERSPOTION)
    ));

    private static final Area grandExchangeArea = Area.rectangular(3147, 3474, 3181, 3508, 0);

    public static boolean Run()
    {
        int decantPotionsAtDoses = OnTick.config.getOtherConfig().getDecantPotionsAtDoses();

        if (decantPotionsAtDoses <= 0)
            return false;

        for (String[] potionNames : potionNamesArrays) // VERY expensive
        {
            int bankOneDoses = kpBank.GetCount(potionNames[0], false);
            int bankTwoDoses = kpBank.GetCount(potionNames[1], false) * 2;
            int bankThreeDoses = kpBank.GetCount(potionNames[2], false) * 3;
            int totalBankDoses = bankOneDoses + bankTwoDoses + bankThreeDoses;

            int amountOfBankPotions = bankOneDoses + bankTwoDoses + bankThreeDoses;

            int backpackOneDoses = kpInventory.GetCount(potionNames[0], true);
            int backpackTwoDoses = kpInventory.GetCount(potionNames[1], true) * 2;
            int backpackThreeDoses = kpInventory.GetCount(potionNames[2], true) * 3;
            int totalBackpackDoses = backpackOneDoses + backpackTwoDoses + backpackThreeDoses;

            int totalDoses = totalBankDoses + totalBackpackDoses;

            if (totalDoses < decantPotionsAtDoses)
            {
                continue;
            }

            if (Inventories.backpack().getEmptySlots() < 2)
            {
                if (!kpBank.Open(Bank.Location.GRAND_EXCHANGE))
                {
                    Log.warn("Opening GE bank");
                    return true;
                }

                Log.info("Depositing inventory for decanting");
                Inventories.bank().depositInventory();
                return true;
            }

            if (Bank.isOpen() && amountOfBankPotions > 0)
            {
                Log.info("Withdrawing " + Arrays.toString(potionNames) + " from bank");
                kpBank.WithdrawAll(potionNames, true);
                return true;
            }

            if (totalBackpackDoses >= decantPotionsAtDoses)
            {
                Log.info("Decanting " + Arrays.toString(potionNames) + " with Bob");

                if (!grandExchangeArea.contains(Global.Data.localPosition))
                {
                    Log.info("Walking to GE");
                    GrandExchangeWalker.WalkToGrandExchange(null);
                    return true;
                }

                InterfaceComponent fourDosesButton = Interfaces.getDirect(582, 6);

                if (fourDosesButton != null && fourDosesButton.isVisible())
                {
                    Log.info("Interacting with 4 dose button");
                    fourDosesButton.interact("Continue");
                    return true;
                }

                Npc bobBarter = Npcs.query().names("Bob Barter (herbs)").results().first();

                if (bobBarter == null)
                {
                    Log.severe("Bob Barter (herbs) not found");
                    return true;
                }

                bobBarter.interact("Decant");

                return true;
            }
        }

        return false;
    }
}
