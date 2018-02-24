package crazyhoneybadgerstudios.game.Game_Elements;

import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

/**
 * @author Dariusz Jerzewski
 */

public class Player
{

    private final String TAG = this.getClass().getSimpleName();

    private String name = "Player"; // def name
    private int witPoints = 20; // def witPoints
    private int level = 0;
    private CardContainer deck; //player's deck
    private CardContainer hand; // player's hand
    private CardContainer bench;
    private CardContainer activeCard; //player's active card/s (allows to change number later, if needed)
    private CardContainer graveyard; //Player's defeated cards
    private boolean emptyHumanDeckNotification = false, emptyAINotification = false;

    protected BattleScreen battleScreen;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// CONSTRUCTORS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Courtney Shek
     *
     * Constructor used for testing purposes
     *
     * @param name - Name of the player
     * @param witPoints - The number of witPoints the player has
     */
    public Player(String name, int witPoints)
    {
        this.name = name;
        this.witPoints = witPoints;
        hand = new CardContainer(5); // player's hand
        bench = new CardContainer(3);
        activeCard = new CardContainer(1);
        graveyard = new CardContainer(6); //Number of cards to be defeated before game is lost

    }


    /**
     * constructor used to initialise new players
     * @param name
     * @param witPoints
     * @param battleScreen
     */
    public Player(String name, int witPoints, BattleScreen battleScreen)
    {

        this.battleScreen = battleScreen;
        this.name = name;
        this.witPoints = witPoints;
        hand = new CardContainer(battleScreen.getHandSize()); // player's hand
        bench = new CardContainer(battleScreen.getBenchSize());
        activeCard = new CardContainer(1);
        graveyard = new CardContainer(6); //Number of cards to be defeated before game is lost
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// GETTERS ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getName() { return name; }



    public int getWitPoints() { return witPoints; }



    public CardContainer getPlayerDeck() { return deck; }



    public CardContainer getPlayerBench() { return bench; }



    public CardContainer getPlayerHand() { return hand; }



    public CardContainer getPlayerActiveContainer() { return activeCard; }



    public CardContainer getPlayerGraveyard() { return graveyard; }



    public int getLevel() { return level; }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// SETTERS ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setPlayerDeck(CardContainer cardContainer) { deck = cardContainer; }



    public void setLevel(int lvl) { level = lvl; }



    public void incrementLevel() { level++; }



    public void setWitPoints(int witPoints) { this.witPoints = witPoints; }


    /*
    * Method to add a new card to the deck, incrementing the size of the deck card container if necessary.
    * @Author: Jodie Burnside (40150039)
    */
    public void addPrizeCardsToDeck(ArrayList<Card> prizeCards)
    {
            for(Card card : prizeCards)
            {
                for(int i = 0; i< deck.getAllCards().size();i++)
                {
                    if(deck.getAllCards().get(i) instanceof UnimonCard)
                    {
                        deck.getAllCards().remove(i);
                        break;
                    }
                }
            }

            for(Card card : prizeCards)
            {
                deck.addToMiddle(card);
            }
    }



    /**
     * @author Michael Grundie.
     *
     * Removes cards from this player's deck and places them into this player's hand.
     * @param schoolCardLimit -
     *                        Limits the number of school cards to be allowed in the hand.
     */
    public void fillHand(int schoolCardLimit)
    {
        Log.d(TAG, "fillHand: started fill hand");
        int handSpaces = hand.getCardsSize() - hand.getAllCards().size();
        for (Card card : hand.getAllCards()) {
            if (card.getType() == Card.TypeOfCard.SCHOOL) {
                schoolCardLimit--;
            }
        }

        for (int i = 0; i < handSpaces; i++)
        {

            if (deck.getAllCards().size() < 1)
            {

                if(this instanceof AI && !emptyAINotification)
                {
                    emptyAINotification = true;
                    battleScreen.setPopUp("OK", null, "Your opponent's deck is now empty", PopUp.SIZE.MEDIUM);
                }
                else if(!emptyHumanDeckNotification && !(this instanceof AI))
                {
                    emptyHumanDeckNotification = true;
                    battleScreen.setPopUp("OK", null, "Your deck is now empty", PopUp.SIZE.MEDIUM);
                }

                Log.d(TAG, "fillHand: " + name + "'s deck is empty.");
                return;
            }
            else
            {
                //When the deck is nearly empty, school card limit becomes irrelevant
                int unimonLeftInDeck  = 0;

                for(Card card : deck.getAllCards())
                {
                    if(card instanceof UnimonCard) unimonLeftInDeck++;
                }

                if (handSpaces >= deck.getAllCards().size())
                {
                    Log.d(TAG, "fillHand: Card added to " + name + "'s hand (More spaces in hand than cards in deck)");
                    hand.addCard(deck.getAndRemoveTopCard());
                }
                else if(handSpaces > unimonLeftInDeck)
                {
                    Log.d(TAG, "fillHand: Card added to " + name + "'s hand (More spaces in hand than unimon in deck)");
                    hand.addCard(deck.getAndRemoveTopCard());
                }
                else
                {
                    Log.d(TAG, "fillHand: looking for a unimon card");
                    Card toTest = deck.getAndRemoveTopCard();

                    //If the hand's school card limit has been reached, the card is reinserted
                    //into the deck and a new card is tested.
                    while (toTest.getType() == Card.TypeOfCard.SCHOOL && schoolCardLimit < 1)
                    {
                        deck.addCard(toTest);
                        toTest = deck.getAndRemoveTopCard();
                    }

                    Log.d(TAG, "fillHand: Card added to " + name + "'s hand.");
                    hand.addCard(toTest);
                    schoolCardLimit--;
                }
            }
            Log.d(TAG, "fillHand: " + hand.getAllCards().size() + " cards in l"+ name + "'s hand.");

        }
    }



    /**
     * @author Michael Grundie
     *
     * The player is allowed to play right to their very last unimon, this method checks that
     * they have at least one unimon left.
     * @return
     */
    public boolean checkIfPlayerHasAnyUnimonCardsLeft()
    {
        Log.d(TAG, "checkIfPlayerHasAnyUnimonCardsLeft: started check for unimon card. ");

        for(Card card : deck.getAllCards())
        {
            if(card instanceof UnimonCard) return true;
        }

        for(Card card : hand.getAllCards())
        {
            if (card instanceof UnimonCard) return true;
        }

        for(Card card: bench.getAllCards())
        {
            if (card instanceof UnimonCard) return true;
        }

        for(Card card: activeCard.getAllCards())
        {
            if (card instanceof UnimonCard) return true;
        }

        Log.d(TAG, "checkIfPlayerHasAnyUnimonCardsLeft: no unimon cards left");
        return false;
    }



    /**
     * Moves a card from one container to another
     * @param c1 container to remove the card from
     * @param c2 container to add the card to
     * @param card card to be moved around
     * @author Chloe McMullan
     */
    protected void moveCardToDifferentContainer(CardContainer c1, CardContainer c2, Card card)
    {
        c1.removeCard(card);
        c2.addCard(card);
    }
}
