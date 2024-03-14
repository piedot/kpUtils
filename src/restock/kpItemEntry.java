package restock;

public class kpItemEntry
{
    // All member variables should be filled
    private String key;

    private int minQuantity, maxQuantity; // Min-Max inclusive
    private int minConsumableQuantity, maxConsumableQuantity; // Min-Max inclusive

    private boolean optional;

    private boolean stackable;

    private boolean restock;
    private int amountToBuy; // For restocking

    //

    public kpItemEntry(String key)
    {
        this.key = key;
    }

    //

    public kpItemEntry quantity(int quantity)
    {
        this.minQuantity = quantity;
        this.maxQuantity = quantity;
        return this;
    }

    public kpItemEntry quantity(int minQuantity, int maxQuantity)
    {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        return this;
    }

    public kpItemEntry consumableQuantity(int quantity)
    {
        this.minConsumableQuantity = quantity;
        this.maxConsumableQuantity = quantity;
        return this;
    }

    public kpItemEntry consumableQuantity(int minConsumableQuantity, int maxConsumableQuantity)
    {
        this.minConsumableQuantity = minConsumableQuantity;
        this.maxConsumableQuantity = maxConsumableQuantity;
        return this;
    }

    public kpItemEntry optional(boolean optional)
    {
        this.optional = optional;
        return this;
    }

    public kpItemEntry stackable(boolean stackable)
    {
        this.stackable = stackable;
        return this;
    }

    public kpItemEntry restock(boolean restock)
    {
        this.restock = restock;
        return this;
    }

    public kpItemEntry amountToBuy(int amountToBuy)
    {
        this.amountToBuy = amountToBuy;
        return this;
    }

    //

    public String getKey()
    {
        return key;
    }

    public int getMinQuantity()
    {
        return minQuantity;
    }

    public int getMaxQuantity()
    {
        return maxQuantity;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public boolean isStackable()
    {
        return stackable;
    }

    public boolean shouldRestock()
    {
        return restock;
    }

    public int getAmountToBuy()
    {
        return amountToBuy;
    }


}
