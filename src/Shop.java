import java.awt.*;
import java.util.Scanner;
import java.util.spi.AbstractResourceBundleProvider;

/**
 * The Shop class controls the cost of the items in the Treasure Hunt game. <p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method. <p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Shop {
    // constants
    private static final int WATER_COST = 2;
    private static final int ROPE_COST = 4;
    private static final int MACHETE_COST = 6;
    private static final int HORSE_COST = 12;
    private static final int BOAT_COST = 20;
    private static final int BOOTS_COST = 50;
    private static final int SHOVEL_COST = 8;

    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);
    OutputWindow window;
    // instance variables
    private double markdown;
    private Hunter customer;

    /**
     * The Shop constructor takes in a markdown value and leaves customer null until one enters the shop.
     *
     * @param markdown Percentage of markdown for selling items in decimal format.
     */
    public Shop(double markdown) {
        this.markdown = markdown;
        customer = null; // customer is set in the enter method
    }

    /**
     * Method for entering the shop.
     *
     * @param hunter the Hunter entering the shop
     * @param buyOrSell String that determines if hunter is "B"uying or "S"elling
     * @return a String to be used for printing in the latest news
     */
    public String enter(Hunter hunter, String buyOrSell) {
        customer = hunter;
        window = customer.GetWindow();
        if (buyOrSell.equals("b")) {
            window.addTextToWindow("\nWelcome to the shop! We have the finest wares in town.", Color.black);
            window.addTextToWindow("\nCurrently we have the following items:", Color.black);
            window.addTextToWindow(inventory(), Color.black);
            window.addTextToWindow("\nWhat're you lookin' to buy? ", Color.black);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, true);
            if (cost == 0 && !customer.getHunterSamuraiMode())  {
                window.addTextToWindow("\nWe ain't got none of those.", Color.black);
            } else if (cost == 0 && !item.equals("sword")) {
                window.addTextToWindow("\nWe ain't got none of those.", Color.black);
            } else {
                window.addTextToWindow("\nIt'll cost you " + cost + " gold. Buy it (y/n)? " , Color.black);
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    if (customer.hasItemInKit("sword") && item.equals("boots")){
                        window.addTextToWindow("\nThe sword's aura scared the shopkeeper and he gives you the item for free.", Color.black);
                        customer.addItem("boots");
                    }else if (customer.hasItemInKit("sword")) {
                        window.addTextToWindow("\nThe sword's aura scared the shopkeeper and he gives you the item for free.", Color.black);
                    }
                    if (!(customer.hasItemInKit("sword") && item.equals("boots"))){
                        buyItem(item);
                    }
                }
            }
        } else {
            window.addTextToWindow("\nWhat're you lookin' to sell? ", Color.black);
            window.addTextToWindow("\nYou currently have the following items: " + customer.getInventory(), Color.black);
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, false);
            if (cost == 0) {
                window.addTextToWindow("\nWe don't want none of those.", Color.black);
            } else {
                window.addTextToWindow("\nIt'll get you " + cost + " gold. Sell it (y/n)? ", Color.black);
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    sellItem(item);
                }
            }
        }
        return "You left the shop";
    }

    /**
     * A method that returns a string showing the items available in the shop
     * (all shops sell the same items).
     *
     * @return the string representing the shop's items available for purchase and their prices.
     */
    public String inventory() {
        String str = "\nWater: " + WATER_COST + " gold\n";
        str += "Rope: " + ROPE_COST + " gold\n";
        str += "Machete: " + MACHETE_COST + " gold\n";
        str += "Horse: " + HORSE_COST + " gold\n";
        str += "Boat: " + BOAT_COST + " gold\n";
        str += "Boots: " + BOOTS_COST + " gold\n";
        str += "Shovel: " + SHOVEL_COST +  " gold\n";
        if (customer.getHunterSamuraiMode()) {
            str += "Sword: " + 0 + " gold\n";
        }
        return str;
    }

    /**
     * A method that lets the customer (a Hunter) buy an item.
     *
     * @param item The item being bought.
     */
    public void buyItem(String item) {
        int costOfItem = checkMarketPrice(item, true);
        if (customer.buyItem(item, costOfItem)) {
            window.addTextToWindow("\nYe' got yerself a " + item + ". Come again soon.", Color.black);
        } else {
            window.addTextToWindow("\nHmm, either you don't have enough gold or you've already got one of those!", Color.black);
        }
    }

    /**
     * A pathway method that lets the Hunter sell an item.
     *
     * @param item The item being sold.
     */
    public void sellItem(String item) {
        int buyBackPrice = checkMarketPrice(item, false);
        if (customer.sellItem(item, buyBackPrice)) {
            window.addTextToWindow("\nPleasure doin' business with you.", Color.black);
        } else {
            window.addTextToWindow("\nStop stringin' me along!", Color.black);
        }
    }

    /**
     * Determines and returns the cost of buying or selling an item.
     *
     * @param item The item in question.
     * @param isBuying Whether the item is being bought or sold.
     * @return The cost of buying or selling the item based on the isBuying parameter.
     */
    public int checkMarketPrice(String item, boolean isBuying) {
        if (isBuying) {
            return getCostOfItem(item);
        } else {
            return getBuyBackCost(item);
        }
    }

    /**
     * Checks the item entered against the costs listed in the static variables.
     *
     * @param item The item being checked for cost.
     * @return The cost of the item or 0 if the item is not found.
     */
    public int getCostOfItem(String item) {
        if (customer.getHunterSamuraiMode()) {
            if (item.equals("sword")) {
                return 0;
            }
        }
        if (item.equals("water")) {
            return WATER_COST;
        } else if (item.equals("rope")) {
            return ROPE_COST;
        } else if (item.equals("machete")) {
            return MACHETE_COST;
        } else if (item.equals("horse")) {
            return HORSE_COST;
        } else if (item.equals("boat")) {
            return BOAT_COST;
        } else if (item.equals("boots")) {
            return BOOTS_COST;
        } else if (item.equals("shovel")) {
            return SHOVEL_COST;
        } else {
            return 0;
        }
    }

    /**
     * Checks the cost of an item and applies the markdown.
     *
     * @param item The item being sold.
     * @return The sell price of the item.
     */
    public int getBuyBackCost(String item) {
        int cost = (int) (getCostOfItem(item) * markdown);
        return cost;
    }
}