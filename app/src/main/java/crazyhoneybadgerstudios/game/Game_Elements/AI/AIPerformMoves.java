package crazyhoneybadgerstudios.game.Game_Elements.AI;

import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.game.Game_Elements.Board;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardHolder;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.util.GameObjectMovement;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * Allows moves to be performed. Currently made to allow the AI to visually perform its moves,
 * however it can also be used for a player's moves
 * @author Chloe McMullan
 */



public class AIPerformMoves {

    private final String TAG = this.getClass().getSimpleName();

    private Vector2 START_MOVEMENT_FROM_HERE;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// INSTANCE VARIABLES /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<AIMove> moves; // moves yet to be completed/performed

    private boolean started; //have any moves been performed this turn
    /**
     * Checks if all the moves have been performed. Protected as this is for use by any class
     * using the AIPerformMoves, so that they can check if they still need to call the perform
     * Method.
     */
    protected boolean finished;

    private Board board;
    private BattleScreen battleScreen;

    private Vector2 movingFrom = new Vector2(-1, -1);
    private Vector2 movingTo = new Vector2(-1, -1);
    private CardHolder cardHolder = null;

    private final int NUMBER_OF_UPDATES = 50; //The number of updates each move should be performed in


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// CONSTRUCTOR ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * General constructor. Started will obviously begin as false.
     * @param board the board to be performed upon
     */
    public AIPerformMoves(Board board, BattleScreen battleScreen) {
        this.board = board;
        this.battleScreen = battleScreen;
        START_MOVEMENT_FROM_HERE = new Vector2(
                battleScreen.getScreenViewPort().left - battleScreen.getCardWidth(),
                battleScreen.getScreenViewPort().bottom + battleScreen.getCardHeight());
        started = false;
        finished = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// PUBLIC METHODS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void reset() {
        finished = false;
    }

    /**
     * Adds the arraylist of moves to be performed
     * @param moves moves to be performed
     */
    public void addMoves(ArrayList<AIMove> moves) {
        this.moves = moves;
        orderMoves(); //ensures they are performed in the correct order
        started = true;
    }

    /**
     * The public method to be called each update. Delegates to the correct method and
     * updates instance variable booleans as required
     */
    public void performMoves() {
        for (AIMove move: moves) {
            Log.d(TAG, "performMoves: Move" + moves.indexOf(move) + ": \t" + move.getTom());
        }
        if (moves == null || moves.size() == 0) {
            setEnd();
            return;
        }

        finished = false;
        if(moves.size()>0)
        {
            Log.d(TAG, "performMoves: " + moves.get(0).getTom());
            perform(moves.get(0));
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////// REARRANGE ARRAYLIST INTO CORRECT ORDER //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reorders moves so that they will be performed in the correct order.
     * Priority (highest to lowest) is: Move cards, Swap Cards, buff cards, attack, reroll
     */
    private void orderMoves() {
        for (int i = 0; i < moves.size(); i++) {
            for (int j = moves.size() - 1; j >= 0; j--) {
                if (moveHasGreaterPriority(moves.get(i).getTom(), moves.get(j).getTom())) {
                    AIMove toReplace = moves.get(j); // temporarily stores it
                    moves.remove(j);
                    moves.add(i, toReplace); //adds it into the correct place
                }
            }
        }
    }

    /**
     * Returns if typeofmove2 is of higher priority than typeofmove1
     * @param tom1 First move to compare
     * @param tom2 Second move to compare
     * @return true = tom2 has higher priority, false = tom1 has higher priority
     */
    private boolean moveHasGreaterPriority(AIMove.TypeOfMove tom1, AIMove.TypeOfMove tom2) {
        switch(tom1) {
            case MOVE_CARD: return false;
            case SWAP_ACTIVE:
                return !(tom2 == AIMove.TypeOfMove.MOVE_CARD ||
                        tom2 == AIMove.TypeOfMove.SWAP_ACTIVE);
            case ATTACH:
                return (tom2 == AIMove.TypeOfMove.ATTACK
                        || tom2 == AIMove.TypeOfMove.REROLL);
            case ATTACK:
                return tom2 == AIMove.TypeOfMove.REROLL;
            case REROLL: return true;
        }
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// METHODS TO PERFORM MOVES ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delegates to the correct method depending on what the type of move is
     * @param move the move to be performed
     */
    private void perform(AIMove move) {
        switch(move.getTom()) {
            case MOVE_CARD: moveCard(move.getCardForMove(), move.getMoveFromHere(),
                    move.getMoveToHere());
                break;
            case SWAP_ACTIVE: swapCard(move.getCardForMove(), move.getDestinationCard());
                break;
            case REROLL: rerollCard(move.getCardForMove());
                break;
            case ATTACH: attachCard(move.getCardForMove(), move.getDestinationCard());
                break;
            case ATTACK:  attackCard(move);
                break;

        }
    }


    /**
     * Method to perform the attack
     * @param move slightly quicker way to access moves.get(0) which contains info about attack
     */
    private void attackCard(AIMove move) {
        battleScreen.startCentreFocus();
        move.getUnimonAttack().performMove();
        if (move.getUnimonAttack().getMoveType() != School.BASIC)
            move.getUnimonAttack().getUnimonMakingMove().chargeEnergy(false);
        completedMove();
    }


    /**
     * Method to move a school card to the active Unimon card as a buff
     * @param schoolCard School card to be used for buffing
     * @param unimonCard Unimon card being buffed
     */
    private void attachCard(Card schoolCard, Card unimonCard) {

        //initialises movements if not already initialised
        if (movingTo.x == -1 && movingFrom.x == -1 && movingFrom.y == -1) {

            cardHolder = findCardHolderWithCard(unimonCard);

            // work around for when cardholder = null bug
            if (cardHolder == null) {
                completedMove();
                return;
            }

            // if the card holder = null, move to unimoncard position. Else, cardholder  position
            initMovingFromAndMovingTo(START_MOVEMENT_FROM_HERE, cardHolder.position);

            schoolCard.setPosition(movingFrom.x, movingFrom.y);
            battleScreen.selectedCard = schoolCard; // ensures school card will be drawn
        }

        //moves the card on the screen, returns true if movement is complete
        if (GameObjectMovement.moveObject(schoolCard, NUMBER_OF_UPDATES, movingFrom, movingTo)) {

            actionsWhenAttached(schoolCard, (UnimonCard)unimonCard);
            completedMove();
        }
    }

    /**
     * Performs the relevant actions when a school card is attached to a unimon card
     * @param school school card attaching to the Unimon card
     * @param unimonCard unimon card being attached to
     */
    private void actionsWhenAttached(Card school, UnimonCard unimonCard) {

        // if the unimon has not been evolved, evolve to this type
        if (!(unimonCard.isEvolved())) {
            unimonCard.evolveCard(school.getSchool());
            addSound("EVOLVE");
        }

        // if the school card was placed on an evolved unimon, it means it's being charged
        else {
            unimonCard.chargeEnergy(true);
            addSound("CHARGESOUND");
        }
    }

    /**
     * Method to swap the active card with a card in the bench.
     * @param benchCard First card swap with
     * @param activeCard Second card to swap with
     */
    private void swapCard(Card benchCard, Card activeCard) {
        completedMove();
        // swapCardBody(benchCard, activeCard);

    }


    /**
     * For moving a card from one play area to another play area. No checks required as these would
     * have been done in the AI.
     * @param card the card to move
     * @param moveFromHere area you're moving it from
     * @param moveToHere area you're moving it to
     */
    private void moveCard(Card card, PlayArea moveFromHere, PlayArea moveToHere) {
        if (movingTo.x == -1 && movingFrom.x == -1 && movingFrom.y == -1) {

            // removes card from cardholder it's currently in
            if (moveFromHere != PlayArea.HAND) {
                CardHolder removeFromHere = findCardHolderWithCard(card, moveFromHere);
                if (removeFromHere != null) removeFromHere.setNull();
            }

            cardHolder = findEmptyCardHolder(moveToHere);

            // work around for bug where card sometimes isn't removed - seems the bug revolves around
            // second card holder in the ai bench
            if (cardHolder == null) {
                completedMove();
                return;
            }

            //if the card has no position at the moment, set it to the default position
            if (card.position == null) {
                card.setPosition(START_MOVEMENT_FROM_HERE.x, START_MOVEMENT_FROM_HERE.y);
            }
            battleScreen.selectedCard = card;
            Log.d(TAG, "moveCard: Cardholder:" + cardHolder.position);
            initMovingFromAndMovingTo(card.position, cardHolder.position);

        }

        // moves the card to desired position, and completes the method when it arrives
        if (GameObjectMovement.moveObject(card, NUMBER_OF_UPDATES, movingFrom, movingTo)) {
            cardHolder.setCard(card);
            addSound("CARDCLICK");
            completedMove();
        }
    }

    /**
     * Performs animations for a card being rerolled
     */
    private void rerollCard(Card card) {
        completedMove();
        //rerollCardBody(card);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// UTIL FOR THESE METHODS //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Finds a card holder in a particular area that contains the specified card
     * @param c card to search for
     * @param playarea play area to search in
     * @return If card holder is found, returns this. Else, returns null.
     */
    private CardHolder findCardHolderWithCard(Card c, PlayArea playarea) {
        for (GameObject cardholder : board.aiSide.getSpecificContainer(playarea.toString()))
            if (((CardHolder)cardholder).getCard() == c) return (CardHolder)cardholder;
        return null;
    }

    /**
     * Finds a card holder that contains the specified card, searching in all areas
     * @param c card to search for
     * @return If card holder is found, returns this. Else, returns null.
     */
    private CardHolder findCardHolderWithCard(Card c) {
        for (ArrayList<GameObject> cardHolderContainers: board.aiSide.getArrayListContainers()) {
            for (GameObject cardHolder : cardHolderContainers) {
                if (((CardHolder)cardHolder).getCard() == c) return (CardHolder)cardHolder;
            }
        }
        return null;
    }

    /**
     * Finds an empty card holder in the specified area.
     * @param playArea area to search in for the card holder
     * @return A card holder containing no card, else returns null.
     */
    private CardHolder findEmptyCardHolder(PlayArea playArea) {
        for (GameObject cardholder: board.aiSide.getContainers().get(playArea.toString())) {
            if (cardholder == null) continue;
            if (((CardHolder) cardholder).getCard() == null) return (CardHolder) cardholder;
        }
        return null;
    }


    /**
     * Initialised the movingFrom and movingTo vectors
     * @param from vector to init movingFrom
     * @param to vector to init movingTo
     */
    private void initMovingFromAndMovingTo(Vector2 from, Vector2 to) {
        movingFrom = new Vector2(from);
        movingTo = new Vector2(to);
    }

    /**
     * resets all values for a move being completed
     */
    private void completedMove() {
        moves.remove(0);
        cardHolder = null;
        movingTo.set(-1, -1);
        movingFrom.set(-1, -1);
        battleScreen.selectedCard = null;
        if (moves.isEmpty()) {
            setEnd();
        }
    }

    /**
     * Method to be called once all the moves have been completed
     */
    private void setEnd() {
        started = false;
        finished = true;
        moves = null;
    }

    /**
     * Adds a sound for the battlescreen to play
     * @param soundName the name of the sound to add
     */
    private void addSound(String soundName) {
        try {
            battleScreen.addSound(battleScreen.
                    getGame().getAssetManager().getSound(soundName));
        }
        catch (Exception e) {
            Log.d(TAG, "addSound: Problem loading in sound: " + e.getMessage());
            Log.d(TAG, "addSound: Caught and program will continue");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GENERAL ACCESSORS/MUTATORS ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasStarted() {
        return started;
    }

    public boolean hasFinished() {
        return finished;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// REMOVED METHODS ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Removed as it could never properly be tested
     *
     * Performs an animation of the cards swapping
     * @param benchCard bench card to swap with the active
     * @param activeCard active card to swap with the bench
     */
    private void swapCardBody(Card benchCard, Card activeCard) {

        if (movingTo.x == -1 && movingFrom.x == -1 && movingFrom.y == -1) {
            //finds both card holders
            CardHolder bench = findCardHolderWithCard(benchCard, PlayArea.BENCH);
            CardHolder active = findCardHolderWithCard(activeCard, PlayArea.ACTIVE);

            // sets each card to their new card holder
            if (active != null && bench != null) {
                bench.setCardWithoutChangingPosition(activeCard);
                active.setCardWithoutChangingPosition(benchCard);
            }
            else { // work around for bug
                completedMove();
                return;
            }
            initMovingFromAndMovingTo(benchCard.position, activeCard.position);
        }

        //moves the card to the desired position, and completes when both cards have arrived
        if (GameObjectMovement.moveObject(benchCard, NUMBER_OF_UPDATES, movingFrom, movingTo) &&
                GameObjectMovement.moveObject(activeCard, NUMBER_OF_UPDATES, movingTo, movingFrom)) {
            // restores the Unimon back to full health
            ((UnimonCard)activeCard).setHealth(((UnimonCard)activeCard).getUnimon().getHealth());
            //removes the correct amount of witpoints for performing a retreat
            battleScreen.getAI().setWitPoints
                    (battleScreen.getAI().getWitPoints()-battleScreen.getWitpointRetreatCost());
            addSound("CARDCLICK");
            completedMove();
        }
    }


    /**
     * Removed as the animation didn't tie in with the other kinds of animations, and the player
     * doesn't have access to this same feature.
     *
     * Performs an animation to show the card being rerolled
     * @param card card to reroll
     */
    private void rerollCardBody(Card card) {
        if (movingTo.x == -1 && movingFrom.x == -1 && movingFrom.y == -1) {
            // sets the drawing to the centre of the screen
            movingFrom.set(battleScreen.getLayerViewport().x,
                    battleScreen.getLayerViewport().y);
            card.setPosition(movingFrom.x, movingFrom.y);
            battleScreen.selectedCard = card;
            addSound("ERROR");
        }

        // reduces the size of the card as a visual effect. This is done in NUMBER_OF_UPDATES/2
        // as normal amount of updates is too fast for user to see what was rerolled
        card.update(null); //ensures bitmaps have been merged
        card.getBound().halfWidth -= (battleScreen.getCardWidth()/NUMBER_OF_UPDATES/2);
        card.getBound().halfHeight -= (battleScreen.getCardHeight()/NUMBER_OF_UPDATES/2);

        // if the card is adequately small enough, complete the move
        if (card.getBound().getWidth() <= 20 || card.getBound().getHeight() <= 20) {
            completedMove();
        }
    }



}
