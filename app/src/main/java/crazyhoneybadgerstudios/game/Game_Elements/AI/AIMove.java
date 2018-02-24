package crazyhoneybadgerstudios.game.Game_Elements.AI;

import android.util.Log;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonMove;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;

/**
 * A class to store 'moves' such as swapping two cards, attacking, rerolling etc.
 * This just allow them to be stored in an array
 * @author Chloe McMullan
 */

public class AIMove {

    private final String TAG = this.getClass().getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// INSTANCE VARIABLES ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public enum TypeOfMove {MOVE_CARD, ATTACK, REROLL, ATTACH, SWAP_ACTIVE};
    private Card cardForMove; //primary card for move to be performed by
    private TypeOfMove tom;
    private PlayArea moveFromHere;
    private PlayArea moveToHere;
    private UnimonCard destinationCard; //secondary card, for if an move requires two cards
    private UnimonMove unimonAttack;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////  CONSTRUCTORS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * For rerolling
     * @param cardForMove card to be rerolled
     */
    public AIMove(Card cardForMove) {
        this.tom = TypeOfMove.REROLL;
        this.cardForMove = cardForMove;
    }

    /**
     * For attacking
     * @param unimonAttack attack being used
     */
    public AIMove(UnimonMove unimonAttack) {
        this.tom = TypeOfMove.ATTACK;
        this.unimonAttack = unimonAttack;
    }

    /**
     * For swapping a card, or buffing a card
     * @param tom ATTACH/SWAP
     * @param cardForMove The School card for buffing, or the bench card to swap with an active card
     * @param destinationCard The Active card being buffed, or the active card being swapped out
     */
    public AIMove(TypeOfMove tom, Card cardForMove, UnimonCard destinationCard) {
        this.tom = tom;
        this.cardForMove = cardForMove;
        this.destinationCard = destinationCard;
    }

    /**
     * For moving a card to a different container
     * @param cardForMove card to move
     * @param moveToHere container to move to
     */
    public AIMove(Card cardForMove, PlayArea moveFromHere, PlayArea moveToHere) {
        this.tom = TypeOfMove.MOVE_CARD;
        this.cardForMove = cardForMove;
        this.moveFromHere = moveFromHere;
        this.moveToHere = moveToHere;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// GENERAL GETTERS/SETTERS //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Card getCardForMove() {
        return cardForMove;
    }

    public TypeOfMove getTom() {
        return tom;
    }

    public PlayArea getMoveFromHere() {
        return moveFromHere;
    }

    public PlayArea getMoveToHere() {
        return moveToHere;
    }

    public UnimonCard getDestinationCard() {
        return destinationCard;
    }

    public UnimonMove getUnimonAttack() {
        return unimonAttack;
    }
}
