package crazyhoneybadgerstudios.game.Game_Elements;


import android.graphics.Paint;
import android.util.Log;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardHolder;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.util.GameObjectMovement;
import crazyhoneybadgerstudios.game.GameSpecificAnimations.PopOutFromCentreOfGameObjectAnimation;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie.
 *
 * This method deals with the movement of cards between board card slots. It checks that the move
 * is valid with respects to the game rules and that their is actually a card holder in the drop
 * location.
 *
 * The finishMoveAfterTouchUp() method is the main entry for this class, it uses boolean flags to
 * decide whether card move decisions need to be made, or whether to just update the card's position
 * to progress it on it's move ro it's already decided destination.
 */
public class CardMovement
{
    private final String TAG = this.getClass().getSimpleName();

    private Player player;
    private int chargesThisTurn, evolvesThisTurn;
    private final int EVOLVE_LIMIT, CHARGE_LIMIT;
    private Card seeking;
    private CardHolder source, destination;
    private Board board;
    private boolean decisionsComplete;
    private boolean waitingForPopUpResults;
    private boolean moveHappening;
    private BattleScreen battleScreen;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// CONSTRUCTORS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This class is recreated every turn, which clears the charges and evolves and sets the player
     * to the new player.
     */
    public CardMovement(Player player, int EVOLVE_LIMIT, int CHARGE_LIMIT, BattleScreen battleScreen, Board board)
    {
        this.battleScreen = battleScreen;
        this.player = player;
        this.EVOLVE_LIMIT = EVOLVE_LIMIT;
        this.CHARGE_LIMIT = CHARGE_LIMIT;
        this.board = board;
        decisionsComplete = waitingForPopUpResults = moveHappening = false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// GETTERS /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Used to check if the player has taken all the school actions they can on this turn.
     * @return
     */
    public boolean maxedSchoolCardActions() { return evolvesThisTurn>= EVOLVE_LIMIT && chargesThisTurn >= CHARGE_LIMIT; }



    public CardHolder getSource() { return source == null ? null : source;}



    public Card getSeeking() { return seeking == null ? null : seeking; }



    /**
     * Returns true is a move is in progress.
     * @return
     */
    public boolean moveHappening() { return moveHappening; }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// SETUP AND RESET /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Checks the passed in location for a cardholder and sets the source cardholder.
     * @param touchPosition-
     *                     The TouchDown or chosen position.
     * @return
     * -True: If there a source holder is found.
     * -else False
     */
    public boolean setSourceHolder(Vector2 touchPosition)
    {
        if(seeking != null)
        {
            Log.d(TAG, "setSourceHolder: Fail, move already in progress.");
            return false;
        }

        CardHolder cardHolderAtTouch = (CardHolder)board.checkIfTouchedCards(touchPosition.x, touchPosition.y);

        if(cardHolderAtTouch != null && cardHolderAtTouch.getCard()!= null)
        {
            source = cardHolderAtTouch;
            Log.d(TAG, "setSourceHolder: New source holder.");
            return true;
        }
        else
        {
            source = null;
            Log.d(TAG, "setSourceHolder: No card at new touch down position.");
            return false;
        }

    }



    /**
     * Sets the seeking variable and flags that a move is now happening.
     */
    public void startNewMove()
    {
        moveHappening = true;
        seeking = source.getCard();
    }



    /**
     * This method skips  the decision making process for a card move and takes the appropriate
     * actions to move a player's active card to the graveyard.
     * @param destination-
     *                   The card container assigned as the grave yard (Assumed to be valid).
     */
    public void startGraveYardMove(CardHolder destination)
    {
        Log.d(TAG, "startGraveYardMove: ");
        this.destination = destination;
        moveHappening = true;
        decisionsComplete = true;
        seeking = source.getCard();
        ((UnimonCard)seeking).setActive(false);
        ((UnimonCard)seeking).chargeEnergy(false);
        sendCardToGraveyard(source.getCard());

    }



    /**
     *
     * Resets everything once a move is complete to allow new moves this turn.
     */
    public void reset()
    {
        seeking = null;
        source = destination = null;
        decisionsComplete = waitingForPopUpResults = moveHappening = false;
        Log.d(TAG, "reset: Card movement reset.");
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// MOVEMENT ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Moves a unimon card to the graveyard when its health reaches 0
     */
    public void sendCardToGraveyard(Card card)
    {
        Log.d(TAG, "sendCardToGraveyard: ");
        board.getGraveYardHolder().setCardWithoutChangingPosition(card);
        board.getGraveYardCards().add(card);
        player.getPlayerActiveContainer().getAllCards().clear();

        if (player instanceof AI)
        {
            ((CardHolder)board.aiSide.getContainers().get("ACTIVE").get(0)).setNull();
        }
        else
        {
            ((CardHolder) board.humanSide.getContainers().get("ACTIVE").get(0)).setNull();
        }
    }



    /**
     * Used by the dragged actions method in battleScreen to determine whether a touch down even
     * actually occurred on a movable card.
     * @return
     */
    public boolean cardDragPossible() { return source != null && !moveHappening; }



    /**
     * Updates the card's position on the screen, progress in it to the given end destinations.
     *
     * @param numberOfUpdates
     * @param endPosition
     * @return
     */
    public boolean moveCardOnScreen(int numberOfUpdates, Vector2 endPosition)
    {
        return GameObjectMovement.moveObject(seeking, numberOfUpdates, seeking.position, endPosition);
    }



    /**
     * @author v.1 Chloe McMullan - Battlescreen CompleteDrag() & draggedActions() methods.
     *                              See commit ae816a55b22aa6e2a5ce1883218fa6db0895de51.
     * @author v.2 Michael Grundie.
     * This method is used to update a card move after the player lifts their finger. It starts the
     * decision making process, delivers pop-up results, and updates the card position on screen.
     *
     * @param playerResponse
     * @param touchUpLocation
     * @return
     */
    public boolean finishMoveAfterTouchUp(boolean playerResponse, Vector2 touchUpLocation)
    {
        if (decisionsComplete)
        {
            if(moveCardOnScreen(3, destination.position))
            {
                seeking.setPosition(destination.position.x, destination.position.y);
                reset();
                Log.d(TAG, "finishMoveAfterTouchUp: Move finished!");
                return true;
            }
        }
        else if(waitingForPopUpResults)
        {
            playerResponseActions(playerResponse);
        }
        else
        {
            decideCardsDestination(touchUpLocation);
        }

        return false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// MOVE DECISION MAKING /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Michael Grundie
     * Uses the player's response to a pop-up in order to make a decision on the card's destination.
     * @param playerResponse
     */
    private void playerResponseActions(boolean playerResponse)
    {
        Log.d(TAG, "playerResponseActions: Checking the player's response");
        if(playerResponse)
        {
            retreatConfirmedActions();
            destination.setCardWithoutChangingPosition(seeking);
            source.setNull();
        }else
        {
            destination = source;
        }

        decisionsComplete = true;
    }



    /**
     * This method decides where the seeking card should end up after the user drops it.
     * @param touchUpLocation
     */
    private void decideCardsDestination(Vector2 touchUpLocation)
    {
        Log.d(TAG, "decideCardsDestination: deciding a new destination.");
        CardHolder potentialDestinationHolder =
                (CardHolder)board.checkIfTouchedCards(touchUpLocation.x,touchUpLocation.y);

        if (potentialDestinationHolder == null)
        {
            destination = source;
            decisionsComplete = true;
            Log.d(TAG, "decideCardsDestination: Not dropped on a card holder, returning to source.");
            return;
        }
        else if(potentialDestinationHolder.getCard() != null)
        {
            Log.d(TAG, "decideCardsDestination: Card already in holder, checking for school card actions.");
            if(schoolCardActions(potentialDestinationHolder))
            {
                destination = potentialDestinationHolder;
                source.setNull();

            }else
            {
                Log.d(TAG, "decideCardsDestination: No possible school-card actions, returning to source.");
                destination = source;
            }

            decisionsComplete = true;
            return;
        }
        else
        {
            Log.d(TAG, "decideCardsDestination: Card holder at drop location is empty, checking for a valid move.");
            boolean validMove;
            switch (source.getSlotType()) {
                case HAND:
                    validMove = moveFromHandActions(potentialDestinationHolder);
                    break;
                case BENCH:
                    validMove = moveFromBenchActions(potentialDestinationHolder);
                    break;
                default:
                    validMove = retreatActions(potentialDestinationHolder);
                    if (waitingForPopUpResults)
                    {
                        /**
                         * On any turn after the first turn, the user first has to reply to the
                         * pop-up question. This method returns at this point for that reason
                         */
                        destination = potentialDestinationHolder;
                        decisionsComplete = false;
                        return;
                    }
                    break;
            }

            if(validMove)
            {
                battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("CARDCLICK"));
                destination = potentialDestinationHolder;
                destination.setCardWithoutChangingPosition(seeking);
                source.setNull();
            }else
            {
                Log.d(TAG, "decideCardsDestination: No valid move, returning to source.");
                destination = source;
                battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("ERROR"));
            }

            decisionsComplete = true;
        }
    }



    /**
     * This method handles school card drags from the hand area
     * @param potentialDestination -
     *                             The destination to be checked.
     * @return true if move is valid, false if move is invalid
     */
    public boolean schoolCardActions(CardHolder potentialDestination)
    {
        if (potentialDestination.getSlotType() == PlayArea.BENCH || potentialDestination.getSlotType() == PlayArea.ACTIVE)
        {
            //Check if the dragged card is a school card.
            if (seeking.getType() == Card.TypeOfCard.SCHOOL)
            {
                //Check if the destination card can be evolved or charged
                UnimonCard destinationUnimon = (UnimonCard) potentialDestination.getCard();
                if (destinationUnimon.isEvolved()) {
                    if (!destinationUnimon.isCharged() && chargesThisTurn < CHARGE_LIMIT)
                    {
                        destinationUnimon.chargeEnergy(true);
                        chargesThisTurn++;
                        player.getPlayerHand().removeCard(seeking);
                        battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("CHARGESOUND"));
                        Log.d(TAG, "schoolCardActions: Unimon Charged");
                        return true;
                    }

                    if (!destinationUnimon.isCharged() && chargesThisTurn >= CHARGE_LIMIT)
                    {
                        battleScreen.setPopUp("OK",null, "Turn charge limit reached!", PopUp.SIZE.MEDIUM);
                    }

                }
                else if(evolvesThisTurn < EVOLVE_LIMIT)
                {
                    School toEvolveTo = seeking.getSchool();
                    destinationUnimon.evolveCard(toEvolveTo);
                    battleScreen.addAnimation(new PopOutFromCentreOfGameObjectAnimation(destinationUnimon, toEvolveTo.name(), battleScreen));
                    evolvesThisTurn++;
                    player.getPlayerHand().removeCard(seeking);
                    battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("EVOLVE"));
                    Log.d(TAG, "schoolCardActions: Unimon Evolved");
                    return true;
                }
                else if (evolvesThisTurn >= EVOLVE_LIMIT)
                {
                    battleScreen.setPopUp("OK",null, "Turn evolve limit reached!", PopUp.SIZE.MEDIUM);
                }

            }
        }
        return false;
    }



    /**
     * This method handles and human player drags from the hand area.
     * @param potentialDestination - The destination to be checked.
     * @return true if move is valid, false if move is invalid
     */
    public boolean moveFromHandActions(CardHolder potentialDestination)
    {
        //Handling school card drags to empty containers.
        if(seeking.getType() == Card.TypeOfCard.SCHOOL)
        {
            //Allows school cards to be moved around the player's hand.
            if(potentialDestination.getSlotType() == PlayArea.HAND)
            {
                Log.d(TAG, "moveFromHandActions: School card hand position change");
                return true;
            }
            //Stops the player dragging a school card to an empty bench or active location.
            Log.d(TAG, "moveFromHandActions: School cards can not move here");
            return false;
        }
        //Handling unimon drags from hand to hand
        else if(potentialDestination.getSlotType() == PlayArea.HAND)
        {
            Log.d(TAG, "moveFromHandActions: moved from hand to hand");
            return true;
        }

        //Handling Unimon drags from hand to bench
        if (potentialDestination.getSlotType() == PlayArea.BENCH)
        {
            player.getPlayerHand().removeCard(seeking);
            player.getPlayerBench().addCard(seeking);
            Log.d(TAG, "moveFromHandActions: moved from hand to bench");
            return true;
        }

        //Handling Unimon drags from hand to active
        if(potentialDestination.getSlotType() == PlayArea.ACTIVE && battleScreen.getTurnCount() == 0)
        {
            player.getPlayerHand().removeCard(seeking);
            player.getPlayerActiveContainer().addCard(seeking);
            ((UnimonCard)seeking).setActive(true);
            Log.d(TAG, "moveFromHandActions: moved from hand to active");
            return true;
        }

        return false;
    }



    /**
     * This method handles and human player drags from the bench area.
     * @param potentialDestination - The destination to be checked.
     * @return true if move is valid, false if move is invalid
     */
    public boolean moveFromBenchActions(CardHolder potentialDestination)
    {

        //Handling Unimon card drags from bench to hand.
        if(potentialDestination.getSlotType() == PlayArea.HAND)
        {
            Log.d(TAG, "moveFromBenchActions: Can not move from bench to hand.");
            return false;
        }

        //Handling Unimon card drags from bench to active.
        if(potentialDestination.getSlotType() == PlayArea.ACTIVE)
        {
            player.getPlayerBench().removeCard(seeking);
            player.getPlayerActiveContainer().addCard(seeking);
            ((UnimonCard)seeking).setActive(true);
            Log.d(TAG, "moveFromBenchActions: moved to active");
        }

        return true;
    }



    /**
     * This method handles and human player drags from the active area.
     * @param potentialDestination - The destination to be checked.
     * @return true if move is valid, false if move is invalid
     */
    public boolean retreatActions(CardHolder potentialDestination)
    {

        //Handling a Unimon card drag from active to hand.
        if (potentialDestination.getSlotType() == PlayArea.HAND)
        {
            Log.d(TAG, "retreatActions: can't retreat from active to hand");
            return false;
        }

        //Handling a Unimon drag from active to bench.
        if(battleScreen.getTurnCount()>1)
        {

            if(!waitingForPopUpResults)
            {
                waitingForPopUpResults = true;

                battleScreen.setPopUp("CANCEL", "OK", "Retreat for 1 witpoint?", PopUp.SIZE.MEDIUM);
                return false;
            }
        }

        retreatConfirmedActions();
        Log.d(TAG, "retreatActions: retreated to hand costing 0 witpoints on first turn");
        return true;

    }



    /**
     * Takes the actions for a retreating card.
     */
    private void retreatConfirmedActions()
    {
        player.getPlayerActiveContainer().removeCard(seeking);
        player.getPlayerBench().addCard(seeking);
        UnimonCard thisUnimonCard = (UnimonCard) seeking;
        thisUnimonCard.setActive(false);
        thisUnimonCard.resetHealth();
        battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("CARDCLICK"));
        if(battleScreen.getTurnCount()>1) player.setWitPoints(player.getWitPoints()
                - battleScreen.getWitpointRetreatCost());
        battleScreen.gameOverCheck();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// DRAW /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Ensures the moving card is drawn after (on top of) and board cards.
     *
     * @param elapsedTime
     * @param iGraphics2D
     * @param layerViewport
     * @param screenViewport
     * @param paint
     */
    public void drawSeeking(ElapsedTime elapsedTime, IGraphics2D iGraphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint)
    {
        if(seeking != null)
        {
            seeking.draw(elapsedTime, iGraphics2D, layerViewport, screenViewport, paint);
        }
    }

}
