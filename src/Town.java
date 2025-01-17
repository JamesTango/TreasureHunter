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
    private boolean winBrawl;

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
            treasure ="dust" ;
        } else if (chance > 0.5 - 0.5/3){
            treasure = "crown";
        } else if (chance > 0.5 - 1.0/3){
            treasure = "trophy" ;
        } else {
            treasure =  "gem" ;
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

    public void setLatestNews(String message) {printMessage = message;}

    public boolean getWinBrawl() {return winBrawl;}

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "\nWelcome to town, " + hunter.getHunterName() + ".";
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
            String item = terrain.getNeededItem();;
            if (terrain.getTerrainName() == "Jungle" && hunter.hasItemInKit("sword")) {
                item = "sword";
            }

            printMessage = "\nYou used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }

        printMessage = "\nYou can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
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
            printMessage = "\nYou couldn't find any trouble";
        } else {
            printMessage =   "\nYou want trouble, stranger! You got it! " ;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")){
              winBrawl = true;
              printMessage +=  "\nSorry, please forgive me.";
              printMessage += "\nThe brawler, seeing your sword surrendered. They gave you "+ goldDiff + " gold.";
            } else if (Math.random() > noTroubleChance) {
                winBrawl = true;
                printMessage += "\nOof! Umph! Ow!\nOkay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + goldDiff + " gold.";
                hunter.changeGold(goldDiff);
            } else {
                winBrawl = false;
                printMessage +=   "\nOof! Umph! Ow!\nThat'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay "  + goldDiff + " gold.";
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public void searchTown(){
        if (searched){
            printMessage = "\nYou have already searched this town!";
        } else{
            searched = true;
            if (treasure.equals("dust")){
                printMessage = "\nYou found " + treasure + " womp womp";
            } else if (hunter.addTreasure(treasure)){
                printMessage = "\nYou found " + treasure + "!";
            } else {
                printMessage = "\nYou have already found a " + treasure;
            }
        }
    }

    public String infoString() {
        return "\nThis nice little town is surrounded by " + terrain.getTerrainName() + ".";
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