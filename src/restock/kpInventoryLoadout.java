package restock;

import java.util.HashMap;
import java.util.Map;

public class kpInventoryLoadout
{
    private String name;
    private Map<String, kpItemEntry> itemMap;

    public kpInventoryLoadout(String name)
    {
        this.name = name;
        this.itemMap = new HashMap<>();
    }

    public void add(kpItemEntry itemEntry)
    {
        itemMap.put(itemEntry.getKey(), itemEntry);
    }

    public String getName()
    {
        return name;
    }

    public kpItemEntry getItem(String key)
    {
        return itemMap.get(key);
    }
}
