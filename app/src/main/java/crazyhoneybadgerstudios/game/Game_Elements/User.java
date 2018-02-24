package crazyhoneybadgerstudios.game.Game_Elements;

import android.content.Context;
import android.media.AudioManager;

import java.util.ArrayList;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems.Item;

/**
 * @author Jodie Burnside - 4/20/2017.
 */

public class User
{
    //User contains all the users profile data - Current level, character head/body, money stats, inventory etc.
    private  Boolean soundEnabled; //default to true on game start
    private int currentLevel; //used to keep track of users progression through levels of the game
    private ArrayList<Card> prizeCards;
    private ArrayList<Item> inventory; //Would have been used for implementation of accessories store
    private int playerSelectedHeadNumber;
    private int playerSelectedRobesNumber;
    private Game game;
    private int goldCoins; //Players number of gold coins

    public User(Game game)
    {
        //Set up default user values
        this.game = game;
        soundEnabled = true;
        currentLevel = 0;
        prizeCards = new ArrayList<Card>();
        playerSelectedHeadNumber = 0;
        playerSelectedRobesNumber = 0;
        goldCoins = 0;

        inventory = new ArrayList<Item>();
    }

    //Getters and setters below
    public int getCurrentLevel() {return currentLevel;}
    public int setCurrentLevel(int level) {return currentLevel = level;}
    public int getPlayerSelectedRobes() { return playerSelectedRobesNumber; }

    public void setPlayerSelectedRobes(int playerSelectedRobes) { this.playerSelectedRobesNumber = playerSelectedRobes; }

    public int getPlayerSelectedHead() { return playerSelectedHeadNumber; }

    public void setPlayerSelectedHead(int playerSelectedHead) { this.playerSelectedHeadNumber = playerSelectedHead; }

    public int getGoldCoins() { return goldCoins; }

    public void setGoldCoins(int goldCoins) { this.goldCoins = goldCoins; }

    public Boolean getSoundEnabled() { return soundEnabled; }


    public ArrayList<Card> getPrizeCards() { return prizeCards; }

    public void setSoundEnabled(Boolean soundEnabled)  { this.soundEnabled = soundEnabled; }

    public ArrayList<Item> getInventory() { return inventory; }

    public void setInventory(ArrayList<Item> inventory) { this.inventory = inventory; }

}
