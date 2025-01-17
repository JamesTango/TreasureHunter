import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean easyMode;
    private boolean samuraiMode;
    private boolean brawlLatest;

    OutputWindow window = new OutputWindow(); // only want one OutputWindow object
    Scanner scan = new Scanner(System.in);

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
        samuraiMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();

        enterTown();
        showMenu();

    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        window.addTextToWindow("\nWelcome to TREASURE HUNTER!", Color.black);
        window.addTextToWindow("\nGoing hunting for the big treasure, eh?", Color.black);
        window.addTextToWindow("\nWhat's your name, Hunter?", Color.black);
        String name = SCANNER.nextLine();

        // set hunter instance variable

        window.addTextToWindow("\nHard(h), Normal(n), Easy(e): ", Color.black);
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
            hunter = new Hunter(name, 20,samuraiMode,  window);
        } else if (hard.equals("test")) {
            hunter = new Hunter(name, 106,samuraiMode,  window);
            hunter.buyItem("water", 1);
            hunter.buyItem("rope", 1);
            hunter.buyItem("machete", 1);
            hunter.buyItem("horse", 1);
            hunter.buyItem("boat", 1);
            hunter.buyItem("boots", 1);
        } else if (hard.equals("e")) {
            easyMode = true;
            hunter = new Hunter(name, 40,samuraiMode, window);
        } else if (hard.equals("s")){
            samuraiMode = true;
            hunter = new Hunter(name,20,samuraiMode, window);
        } else {
            hunter = new Hunter(name, 20,samuraiMode, window);
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        if (easyMode) {
            markdown = 1;
            toughness = 0.2;

        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class

        currentTown = new Town(shop, toughness, easyMode);



        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x")) {

            window.addTextToWindow("\n", Color.black);
            if (brawlLatest){
                window.addTextToWindow(currentTown.getLatestNews(), Color.black);
                if (currentTown.getWinBrawl()){
                    currentTown.setLatestNews("\nYou won a brawl.");
                } else {
                    currentTown.setLatestNews("\nYou lost a brawl.");
                }
            }else {
                window.addTextToWindow(currentTown.getLatestNews(), Color.black);
            }
            if (hunter.getBankruptcy()) {
                window.addTextToWindow("\nGame Over", Color.red);
                break;
            }
            if (hunter.emptyPositionInTreasures() == -1){
                window.addTextToWindow("\nCongratulations, you have found the last of the three treasures, you win!", Color.green);
                break;
            }
            window.addTextToWindow("\n***\n", Color.black);
            window.addTextToWindow(hunter.infoString(), Color.black);
            window.addTextToWindow(currentTown.infoString(), Color.black);
            window.addTextToWindow("\n(B)uy something at the shop.", Color.black);
            window.addTextToWindow("\n(S)ell something at the shop.", Color.black);
            window.addTextToWindow("\n(E)xplore surrounding terrain.", Color.black);
            window.addTextToWindow("\n(H)unt for treasure in the town.", Color.black);
            window.addTextToWindow("\n(M)ove on to a different town.", Color.black);
            window.addTextToWindow("\n(L)ook for trouble!", Color.black);
            window.addTextToWindow("\n(D)ig for gold", Color.black);
            window.addTextToWindow("\nGive up the hunt and e(X)it.", Color.black);
            window.addTextToWindow("\n", Color.black);
            window.addTextToWindow("\nWhat's your next move? ", Color.black);

            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        window.clear();
        if (choice.equals("b") || choice.equals("s")) {
            brawlLatest = false;
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            window.addTextToWindow(currentTown.getTerrain().infoString(), Color.black);
        } else if (choice.equals("m")) {
            brawlLatest = false;
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                window.addTextToWindow(currentTown.getLatestNews(), Color.black);
                enterTown();
            }
        } else if (choice.equals("l")) {
            brawlLatest = true;
            currentTown.lookForTrouble();
        } else if (choice.equals("h")){
            brawlLatest = false;
            currentTown.searchTown();
        } else if (choice.equals("x")) {
            window.addTextToWindow("\nFare thee well, " + hunter.getHunterName() + "!", Color.black);
        } else if (choice.equals("d")) {
            if (hunter.hasItemInKit("shovel") && !currentTown.getTownDug() ) {
                if (Math.random() > 0.5) {
                    int goldDigged = (int)(Math.random() * 21);
                    window.addTextToWindow("\nYou dug up " + goldDigged + " gold", Color.black);
                    hunter.changeGold(goldDigged);
                } else {
                    window.addTextToWindow("\nYou dug but only found dirt",Color.black);
                }
                currentTown.setTownDug();
            } else if (currentTown.getTownDug()) {
                window.addTextToWindow("\nYou already dug for gold in this town.",Color.red);
            } else {
                window.addTextToWindow("\nYou can't dig for gold without a shovel TRY GOING TO THE SHOP TO BUY THE SHOVEL" ,Color.red);
            }
        } else {
            window.addTextToWindow("\nYikes! That's an invalid option! Try again.",Color.red);
        }
    }
}