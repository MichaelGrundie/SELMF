package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;
import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.Player;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Jodie Burnside - 4/30/2017.
 */

public class BattleEndScreen extends GameScreen
{
    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<Card> graveyard;
    private boolean won = false; //used to know to display win or lose info at end of battle

    private Rect battleResultRect; //Bitmap assets in AssetLoading.java
    private  Rect continueBt;
    private Rect prizeCardOne;
    private Rect prizeCardOneBackground;
    private Rect prizeCardTwo;
    private Rect prizeCardTwoBackground;
    private Rect prizeCardThree;
    private Rect prizeCardThreeBackground;

    private Game mGame;

    private Player ai;
    private Player human;

    private ArrayList<Card> prizeCards = new ArrayList<Card>(3); //Holds the 3 prize cards for the end of the battle (if win)
    private boolean prizeChosen;
    private int prizeCardPadding;

    //-------------------------------------------
    //Constructor
    //-------------------------------------------
    public BattleEndScreen(Game game, ArrayList<Card> graveyard, Player AI, Player human, boolean didHumanWin)
    {
        super("Battle End Screen", game);
        this.mGame = game;
        this.graveyard = graveyard;
        this.ai = AI;
        this.human = human;
        won = didHumanWin;
        prizeChosen = false; //Prize card not yet chosen by default

        setUpScreenViewPort();
        setUpRects();
        prizeCardPadding = screenViewport.width / 10;

        if (didHumanWin)
        {
            winBattle();
        }
        else
        {
            prizeChosen = true; //Do not display prize cards if player lost battle
        }
    }

    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();
        for(TouchEvent event : input.getTouchEvents())
        {
            if(event.type == TouchEvent.TOUCH_DOWN)
            {
                if (prizeCardOne.contains((int) event.x, (int) event.y) && !prizeChosen)
                {
                    mGame.getPlayerProfile().getPrizeCards().add(prizeCards.get(0));
                    Log.d("Prize card ", "ADDED 0");
                    prizeChosen = true;

                }
                if (prizeCardTwo.contains((int) event.x, (int) event.y) && !prizeChosen)
                {
                    mGame.getPlayerProfile().getPrizeCards().add(prizeCards.get(1));
                    Log.d("Prize card ", "ADDED 1");
                    prizeChosen = true;
                }
                if (prizeCardThree.contains((int) event.x, (int) event.y) && !prizeChosen)
                {
                    mGame.getPlayerProfile().getPrizeCards().add(prizeCards.get(2));
                    Log.d("Prize card ", "ADDED 2");
                    prizeChosen = true;
                }
                if (battleResultRect.contains((int) event.x, (int) event.y) && prizeChosen)
                {
                    mGame.getScreenManager().removeScreen(this.getName());
                    MapScreen mapScreen = new MapScreen(mGame);
                    mGame.getScreenManager().addScreen(mapScreen);
                    mGame.getScreenManager().setAsCurrentScreen("Map Screen");
                }

            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D)
    {
        if (prizeChosen) //If a player already chose a prize card
        {
            graphics2D.drawBitmap(assetManager.getBitmap("BOARD"), null, new Rect (screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom), null);
            if (won)
            {
                graphics2D.drawBitmap(AssetLoading.victory, null, battleResultRect, null);
            }
            else
            {
                graphics2D.drawBitmap(AssetLoading.defeat, null, battleResultRect, null);
            }
        }
        else //If prize card has not yet been chosen, provide choice of 3 prize cards
        {
            if (won)
            {
                graphics2D.drawBitmap(AssetLoading.prizeCardBackground, null, new Rect (screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom), null);

                graphics2D.drawBitmap(assetManager.getBitmap(prizeCards.get(0).getName()), null, prizeCardOne, null);
                graphics2D.drawBitmap(assetManager.getBitmap(prizeCards.get(1).getName()), null, prizeCardTwo, null);
                graphics2D.drawBitmap(assetManager.getBitmap(prizeCards.get(2).getName()), null, prizeCardThree, null);

            }




        }


    }

   //Method to take the appropriate actions if the user won the battle - increment user current level, reward with gold and allocate prize cards etc.
   //@author Jodie Burnside (4015003)
    private void winBattle()
    {
        won = true;

        //Prize Cards - on winning, chose three cards from enemy collection that player can chose from to add to their hand

        //ArrayList<UnimonCard> enemyUnimonCards = ai.getPlayerDeck().returnOnlyUnimon();
        for (int i = 0; i < 3; i++) //Obtain a random card from enemy hand as a prize card choice
        {
            //prizeCards.add(enemyUnimonCards.get(randomArrayIndex(enemyUnimonCards.size() - 1)));
            int index  = randomArrayIndex(graveyard.size());
            Log.d(TAG, "winBattle: " + graveyard.get(index).getName() + " added to prize cards.");
            prizeCards.add(graveyard.get(index));
            graveyard.remove(index);

        }


        //End Battle - increase current level if appropriate (not a replay) and return to map screen.
        if (human.getLevel() == mGame.getPlayerProfile().getCurrentLevel()) //if battle was not a replay of a previous level, increase game progress so the next level is unlocked
        {
            int currentLevel = mGame.getPlayerProfile().getCurrentLevel();
            currentLevel++;
            mGame.getPlayerProfile().setCurrentLevel(currentLevel); //increment level, to indicate the level has been completed.
            Log.d("Level incremented", "");
        }

        mGame.getPlayerProfile().setGoldCoins(mGame.getPlayerProfile().getGoldCoins() + 5); //Award default 5  gold coins for winning a battle
        Log.d("Win: ", "5 GOLD COINS AWARDED");
    }

    //Method to randomly generate an int for an array index, to randomly select prize cards.
    //@author edited for our purposes by Jodie Burnside (4015003), Basic generate random number code tutorial from Stack Overflow
    private int randomArrayIndex(int length) //Returns a random int index within an array, to be used to offer 3 random unimon prize cards
    {
       Random rand = new Random();
        int index = 0;

        index = rand.nextInt(length);
        return index;
    }


    //Method to assign rect positions
    //@author Jodie Burnside (4015003)
    public void setUpRects()
    {
        //Battle Ended (Defeat/Victroy) Rect positioning
        battleResultRect =  new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom);

        prizeCardOne = new Rect(screenViewport.width / 10, screenViewport.height / 3,
                screenViewport.width/3, screenViewport.height - mGame.getScreenHeight() / 3);

        prizeCardTwo = new Rect(screenViewport.width / 10 + screenViewport.width / 3, screenViewport.height / 3,
                screenViewport.width/3 +  screenViewport.width / 3, screenViewport.height - mGame.getScreenHeight() / 3);

        prizeCardThree =  new Rect(screenViewport.width / 10 + (screenViewport.width / 3) * 2, screenViewport.height / 3,
                screenViewport.width/3 +  (screenViewport.width / 3) * 2, screenViewport.height - mGame.getScreenHeight() / 3);


        continueBt = new Rect((mGame.getScreenWidth() / 4),
                (mGame.getScreenHeight() / 4) + 250,
                (mGame.getScreenWidth() - (mGame.getScreenWidth() / 4)),
                (mGame.getScreenHeight() / 4) + 200);
    }

}