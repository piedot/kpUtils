package utils;

import ids.ItemId;
import org.rspeer.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;

public class kpConfig
{
    public static class ConfigItem
    {
        private String name = null;
        private int[] ids = new int[]{};
        private String action = null;
        private boolean isNone = true;
        private boolean stackable = false;

        public ConfigItem name(String name)
        {
            this.name = name;
            return this;
        }

        public ConfigItem ids(int[] ids)
        {
            this.ids = ids;
            return this;
        }

        public ConfigItem id(int id)
        {
            this.ids = new int[]{id};
            return this;
        }

        public ConfigItem action(String action)
        {
            this.action = action;
            return this;
        }

        public ConfigItem isNone(boolean isNone)
        {
            this.isNone = isNone;
            return this;
        }

        public ConfigItem stackable(boolean stackable)
        {
            this.stackable = stackable;
            return this;
        }

        public String getName()
        {
            return name;
        }

        public int[] getIds()
        {
            return ids;
        }

        public int getId()
        {
            if (ids == null || ids.length == 0)
            {
                Log.warn("No id set for " + name);
                return -1;
            }
            return ids[0];
        }

        public String getAction()
        {
            return action;
        }

        public boolean isNone()
        {
            return isNone;
        }

        public boolean isStackable()
        {
            return stackable;
        }

        @Override
        public String toString()
        {
            return name;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof ConfigItem)
            {
                return ((ConfigItem) obj).getId() == getId();
            }
            return false;
        }
    }

    public static final Map<String, ConfigItem> configItemMap = new HashMap<>();

    public static final ConfigItem NONE = new ConfigItem().name("None").id(-1).isNone(true);
    public static final ConfigItem DRAMEN_STAFF = new ConfigItem().name("Dramen staff").id(ItemId.DRAMEN_STAFF).stackable(false);
    public static final ConfigItem LUNAR_STAFF = new ConfigItem().name("Lunar staff").id(ItemId.LUNAR_MOONCLAN_LIMINAL_STAFF).stackable(false);
    public static final ConfigItem ARDOUGNE_CLOAK_1 = new ConfigItem().name("Ardougne cloak 1").id(ItemId.ARDY_CAPE_EASY).stackable(false);
    public static final ConfigItem ARDOUGNE_CLOAK_2 = new ConfigItem().name("Ardougne cloak 2").id(ItemId.ARDY_CAPE_MEDIUM).stackable(false);
    public static final ConfigItem ARDOUGNE_CLOAK_3 = new ConfigItem().name("Ardougne cloak 3").id(ItemId.ARDY_CAPE_HARD).stackable(false);
    public static final ConfigItem ARDOUGNE_CLOAK_4 = new ConfigItem().name("Ardougne cloak 4").id(ItemId.ARDY_CAPE_ELITE).stackable(false);
    public static final ConfigItem QUEST_CAPE = new ConfigItem().name("Quest point cape").id(ItemId.SKILLCAPE_QP).stackable(false);
    public static final ConfigItem SALVE_GRAVEYARD_TELEPORT = new ConfigItem().name("Salve graveyard teleport").id(ItemId.TELETAB_SALVE).stackable(true);
    public static final ConfigItem PRAYER_POTION = new ConfigItem().name("Prayer potion").id(ItemId._4DOSEPRAYERRESTORE).stackable(false);
    public static final ConfigItem SUPER_RESTORE = new ConfigItem().name("Super restore").id(ItemId._4DOSE2RESTORE).stackable(false);
    public static final ConfigItem SANFEW_SERUM = new ConfigItem().name("Sanfew serum").id(ItemId.SANFEW_SALVE_4_DOSE).stackable(false);
    public static final ConfigItem SUPER_COMBAT_POTION = new ConfigItem().name("Super combat potion").id(ItemId._4DOSE2COMBAT).stackable(false);
    public static final ConfigItem DIVINE_SUPER_COMBAT_POTION = new ConfigItem().name("Divine super combat potion").id(ItemId._4DOSEDIVINECOMBAT).stackable(false);
    public static final ConfigItem RUNE_POUCH = new ConfigItem().name("Rune pouch").id(ItemId.BH_RUNE_POUCH).stackable(false);
    public static final ConfigItem DIVINE_RUNE_POUCH = new ConfigItem().name("Divine rune pouch").id(ItemId.DIVINE_RUNE_POUCH).stackable(false);
    public static final ConfigItem RING_OF_DUELING = new ConfigItem().name("Ring of dueling").id(ItemId.RING_OF_DUELING_8).stackable(false).action("Ferox Enclave");
    public static final ConfigItem TELEPORT_TO_HOUSE = new ConfigItem().name("Teleport to house").id(ItemId.POH_TABLET_TELEPORTTOHOUSE).stackable(true).action("Inside");
    //private static final ConfigItem CRAFTING_CAPE = new ConfigItem().name("Construction cape").id(ItemId.SKILLCAPE_CONSTRUCTION).stackable(false).action("TODO");
    //private static final ConfigItem MAX_CAPE = new ConfigItem().name("Max cape").id(ItemId.SKILLCAPE_MAX).stackable(false).action("TODO");
    public static final ConfigItem VARROCK_TELEPORT = new ConfigItem().name("Varrock teleport").id(ItemId.POH_TABLET_VARROCKTELEPORT).stackable(true);
    public static final ConfigItem RING_OF_WEALTH = new ConfigItem().name("Ring of wealth").ids(new int[]{ItemId.RING_OF_WEALTH_5, ItemId.RING_OF_WEALTH_4, ItemId.RING_OF_WEALTH_3, ItemId.RING_OF_WEALTH_2, ItemId.RING_OF_WEALTH_1}).stackable(false).action("Grand Exchange");

    public static final ConfigItem LUMBRIDGE_TELEPORT_TAB = new ConfigItem().name("Lumbridge teleport").id(ItemId.POH_TABLET_LUMBRIDGETELEPORT).stackable(true).action("Break");

    static
    {
        configItemMap.put(NONE.getName(), NONE);
        configItemMap.put(DRAMEN_STAFF.getName(), DRAMEN_STAFF);
        configItemMap.put(LUNAR_STAFF.getName(), LUNAR_STAFF);
        configItemMap.put(ARDOUGNE_CLOAK_1.getName(), ARDOUGNE_CLOAK_1);
        configItemMap.put(ARDOUGNE_CLOAK_2.getName(), ARDOUGNE_CLOAK_2);
        configItemMap.put(ARDOUGNE_CLOAK_3.getName(), ARDOUGNE_CLOAK_3);
        configItemMap.put(ARDOUGNE_CLOAK_4.getName(), ARDOUGNE_CLOAK_4);
        configItemMap.put(QUEST_CAPE.getName(), QUEST_CAPE);
        configItemMap.put(SALVE_GRAVEYARD_TELEPORT.getName(), SALVE_GRAVEYARD_TELEPORT);
        configItemMap.put(PRAYER_POTION.getName(), PRAYER_POTION);
        configItemMap.put(SUPER_RESTORE.getName(), SUPER_RESTORE);
        configItemMap.put(SANFEW_SERUM.getName(), SANFEW_SERUM);
        configItemMap.put(SUPER_COMBAT_POTION.getName(), SUPER_COMBAT_POTION);
        configItemMap.put(DIVINE_SUPER_COMBAT_POTION.getName(), DIVINE_SUPER_COMBAT_POTION);
        configItemMap.put(RUNE_POUCH.getName(), RUNE_POUCH);
        configItemMap.put(DIVINE_RUNE_POUCH.getName(), DIVINE_RUNE_POUCH);
        configItemMap.put(RING_OF_DUELING.getName(), RING_OF_DUELING);
        configItemMap.put(TELEPORT_TO_HOUSE.getName(), TELEPORT_TO_HOUSE);
        //configItemMap.put(CRAFTING_CAPE.getName(), CRAFTING_CAPE);
        //configItemMap.put(MAX_CAPE.getName(), MAX_CAPE);
        configItemMap.put(VARROCK_TELEPORT.getName(), VARROCK_TELEPORT);
        configItemMap.put(RING_OF_WEALTH.getName(), RING_OF_WEALTH);
    }

    public static ConfigItem GetConfigItem(String name)
    {
        ConfigItem item = configItemMap.get(name);
        if (item == null)
        {
            Log.severe("No config item found for " + name);
            return null;
        }
        return item;
    }

}
