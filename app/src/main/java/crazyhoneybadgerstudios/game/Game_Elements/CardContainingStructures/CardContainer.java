package crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures;

import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.ImageHolder;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Dariusz Jerzewski
 * container to store cards in
 */

public class CardContainer{

    //variables necessary for operation
    private final String TAG = this.getClass().getSimpleName();
    private int cardsSize;

    private ArrayList<Card> cards; // hold cards

    /**
     * constructor limiting size of cards
     * @param size
     */
    public CardContainer(final int size){
        cards = new ArrayList<>(size);
        cardsSize = size;
    }

    /**
     * add a card to the container
     * @param card
     */
    public void addCard(final Card card){
        //if size is smaller than max size add card, else log
        if(cards.size() < cardsSize){
            cards.add(card);
        }else Log.d(this.toString(), "Card container full");
    }

    /**
     * function to add a schoolCard
     * @param card
     */
    public void addCard(final SchoolCard card){
        //if size is smaller than max size add card
        if(cards.size() < cardsSize){
            cards.add(card);
        }
    }

    /**
     * function to add UnimonCard
     * @param card
     */
    public void addCard(final UnimonCard card){
        //if size is smaller than max size add card
        if(cards.size() < cardsSize){
            cards.add(card);
        }
    }

    /**
     * gets top card and removes it from container
     * @return
     */
    public Card getAndRemoveTopCard()
    {
        Card toReturn = cards.get(0);
        cards.remove(0);
        cards.trimToSize();
        return toReturn;
    }

    /**
     * shuffle cards to make sure they are randomly distributed
     */
    public void shuffleCards()
    {
        Collections.shuffle(cards);
    }

    /**
     * @author Michael Grundie
     *
     * Deals a given card into the middle of the array list.
     * @param card
     */
    public void addToMiddle(Card card)
    {
        cards.add((cards.size()/2)+1, card);
    }

    /**
     * remove card by passing a card
     * @param card
     */
    public void removeCard(final Card card){
        if(cards.contains(card)){
            cards.remove(card);
        }
    }

    /**
     * remove card by index
     * @param index
     */
    public void removeCard(int index) {
        if (cards.size() > index && index > 0) {
            cards.remove(index);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GETTING CARDS /////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns only the unimon within this container as an arraylist
     * @return Arraylist of Unimon in this container
     * @author Chloe McMullan
     */
    public ArrayList<UnimonCard> returnOnlyUnimon() {
        ArrayList<UnimonCard> temp = new ArrayList<>();
        for (Card c: cards) {
            if (c.getType() == Card.TypeOfCard.UNIMON) temp.add((UnimonCard)c);
        }
        return temp;
    }

    /**
     * Returns only the school cards in this container
     * @return An arraylist of the school cards in this container
     * @author Chloe McMullan
     */
    public ArrayList<SchoolCard> returnOnlySchool() {
        ArrayList<SchoolCard> temp = new ArrayList<>();
        for (Card c: cards) {
            if (c.getType() == Card.TypeOfCard.SCHOOL) temp.add((SchoolCard) c);
        }
        return temp;
    }


    /**
     * gets card with certain name
     * @param name
     * @return
     */
    public Card getCard(final String name){
        Card card = null;
        for(Card c : cards)
            if(c.getName().equals(name)){card = c; break;}
        return card;
    }

    /**
     * get all cards as array list of cards
     * @return
     */
    public ArrayList<Card> getAllCards(){
        return cards;
    }

    public int getCardIndex(final String name){
        int index = -1; // if not existent return -1
        for(Card c : cards)
            if(c.getName().equals(name)){index = cards.indexOf(c); break;}
        return index;
    }

    /**
     * get index of a card
     * @param searchingFor
     * @return
     */
    public int getCardIndex(final Card searchingFor) {
        return cards.indexOf(searchingFor);
    }


    ////////////////////// SWAPPING CARDS ///////////////////////////////

    public void swapCard(Card cardTarget, Card card){
        if(cards.contains(cardTarget)) {
            cards.toArray()[getCardIndex(cardTarget.getName())] = card;
        }
    }

    ////////////////////  METHODS TO FIND SIZE OF CONTAINER //////////////////////////
    public int getCardsSize()
    {
        return cardsSize;
    }

    /**
     * Returns only the occupied number of spaces
     * @return an int of the occupied spaces within this container
     * @author Chloe McMullan
     */
    public int getHowManyCards() {
        int howManyCards = 0;
        for (Card c : cards) {
            if (c != null) howManyCards++;
        }
        return howManyCards;
    }


    ////////////////// UPDATE AND DRAW METHODS /////////////////////////

    /**
     * @author Michael Grundie
     *
     * @param elapsedTime
     */
    public void updateCards(ElapsedTime elapsedTime)
    {
        for(Card card: cards)
        {
            if(card.getType() == Card.TypeOfCard.UNIMON)
            {
                ((UnimonCard) card).update(elapsedTime);
            }else
            {
                ((SchoolCard) card).update(elapsedTime);
            }
        }
    }

    /**
     * draw cards to the screen
     * @param elapsedTime
     * @param iGraphics2D
     * @param layerViewport
     * @param screenViewport
     * @param paint
     */
    public void drawCards(ElapsedTime elapsedTime, IGraphics2D iGraphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint){
        for(Card card : cards){
            card.draw(elapsedTime, iGraphics2D, layerViewport, screenViewport, paint);
        }
    }


}