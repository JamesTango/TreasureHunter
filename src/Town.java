import java.awt.*;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean searched;
    private String treasure;
    private boolean TownDug = false;
    private boolean EasyMode = false;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */

    public Town(Shop shop, double toughness, boolean EasyMode) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        this.EasyMode = EasyMode;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";
        searched = false;
        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        double chance = Math.random();
        if (chance > 0.5){
            treasure = Colors.BLACK + "dust" + Colors.RESET;
        } else if (chance > 0.5 - 0.5/3){
            treasure = Colors.YELLOW + "crown" + Colors.RESET;
        } else if (chance > 0.5 - 1.0/3){
            treasure = Colors.RED + "trophy" + Colors.RESET;
        } else {
            treasure = Colors.BLUE + "gem" + Colors.RESET;
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTownDug() {
        TownDug = true;
    }

    public boolean getTownDug() {
        return TownDug;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger! You got it! " + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")){
              printMessage += Colors.RED + "\nSorry, please forgive me." + Colors.RESET;
                printMessage += "\nThe brawler, seeing your sword surrendered. They gave you " + Colors.YELLOW + goldDiff + Colors.RESET+ " gold.";
            } else if (Math.random() > noTroubleChance) {
                printMessage += Colors.RED +"\nOof! Umph! Ow!\nOkay, stranger! You proved yer mettle. Here, take my gold."+ Colors.RESET;
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET+ " gold.";
                hunter.changeGold(goldDiff);
            } else {
                printMessage += Colors.RED + "\nOof! Umph! Ow!\nThat'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                printMessage += "\nYou lost the brawl and pay " + Colors.RED + goldDiff + Colors.RESET + " gold.";
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public void searchTown(){
        if (searched){
            printMessage = "You have already searched this town!";
        } else{
            searched = true;
            if (treasure.equals(Colors.BLACK + "dust" + Colors.RESET)){
                printMessage = "You found " + treasure + " womp womp";
            } else if (hunter.addTreasure(treasure)){
                printMessage = "You found " + treasure + "!";
            } else {
                printMessage = "You have already found a " + treasure;
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1.0/6)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < 2*(1.0/6)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < 3*(1.0/6)) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < 4*(1.0/6)) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 5*(1.0/6)){
            return new Terrain("Jungle", "Machete");
        } else{
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (EasyMode) {
            return false;
        }
        double rand = Math.random();
        return (rand < 0.5);
    }
}