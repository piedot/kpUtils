package restock;

class Main
{
    private Main()
    {
        Log("Starting..");

        kpInventoryLoadout inventoryLoadout = new kpInventoryLoadout("Boss inventory");

        inventoryLoadout.add(new kpItemEntry("Prayer potion")
                .quantity(1, 20)
                .consumableQuantity(1, 4)
                .optional(false)
                .stackable(false)
                .restock(true).amountToBuy(100)
        );

        inventoryLoadout.add(new kpItemEntry("Prayer potion")
                .quantity(1, 20)
                .consumableQuantity(1, 4)
                .optional(false)
                .stackable(false)
                .restock(true).amountToBuy(100)
        );

        
    }

    private void Log(String text)
    {
        System.out.println(text);
    }

    public static void main (String[] args)
    {
        new Main();
    }

}