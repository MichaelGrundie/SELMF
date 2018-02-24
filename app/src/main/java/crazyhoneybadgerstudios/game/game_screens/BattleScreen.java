package crazyhoneybadgerstudios.game.game_screens;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import crazyhoneybadgerstudios.engine.animation.IAnimation;
import crazyhoneybadgerstudios.engine.audio.Sound;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AIPerformMoves;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonMove;
import crazyhoneybadgerstudios.game.Game_Elements.CardMovement;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;
import crazyhoneybadgerstudios.game.Game_Elements.PopUp;
import crazyhoneybadgerstudios.game.Game_Elements.RockPaperScissors;
import crazyhoneybadgerstudios.game.Game_Elements.TypeOfLoss;
import crazyhoneybadgerstudios.game.Game_Elements.WitPointsBar;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.game.GameSpecificAnimations.PopOutFromCentreOfGameObjectAnimation;
import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.game.Game_Elements.Board;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.DeckBuilder;
import crazyhoneybadgerstudios.game.Game_Elements.Player;
import crazyhoneybadgerstudios.util.InputHelper;
import crazyhoneybadgerstudios.game.GameSpecificAnimations.moveUpOrDownGameObjectAnimation;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.*;

/**
 * @author Dariusz Jerzewski - basic functionality & initial setup
 * @author Michael Grundie - Most of the game and human side logic along witch card moves and drags.
 */

public class BattleScreen extends GameScreen{

    private final String TAG = "BattleScreen";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// BOARD LAYOUT VARIABLES ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final float CARD_WIDTH = 84f;
    private final float CARD_HEIGHT = 150f;
    private final float BOARD_WIDTH = 480f;
    private final float BOARD_HEIGHT = 810f;
    private final float layerViewportWidth = 480;
    private final float layerViewportHeight = 320;
    private final float xAxisCardSpacing = 10f;
    private final float yAxisCardSpacing = 10f;

    public float getCardWidth() {
        return CARD_WIDTH;
    }
    public float getCardHeight() {
        return CARD_HEIGHT;
    }
    public float getXAxisCardSpacing() {
        return xAxisCardSpacing;
    }
    public float getYAxisCardSpacing() {
        return yAxisCardSpacing;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// Layer Viewport ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private LayerViewport layerViewport;

    public LayerViewport getLayerViewport()
    {
        return layerViewport;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// GAMERULE VARIABLES //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * TO TEST A SHORTER GAME, decrease unimonPerDeck and schoolCardsPerDeck.
     */
    private final int unimonPerDeck = 25;
    private final int schoolCardsPerDeck = 30;
    private final int handSize = 5;
    private final int benchSize = 3;
    private final int handSchoolCardLimit = 3;
    private final int chargeLimit = 1;
    private final int evolveLimit = 1;
    private final int witpoints = 20;
    private final int witpointKoCost = 1;
    private final int witpointRetreatCost = 1;

    public int getWitpointRetreatCost() { return witpointRetreatCost; }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// BATTLESCREEN VARIABLES ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //EVENT DRIVING BOOLEANS
    private boolean turnResolved = false;
    private boolean finishedFocusShift = false;
    private boolean doFocusToMiddle = false;
    private boolean rockPaperScissorsComplete = false;
    private boolean popupResults = false;
    private boolean timeDelayFinished = false;


    //EVENT DRIVING INTS
    private final int NUMBER_OF_UPDATES = 10;
    //Human == Even Numbers | AI == Odd Numbers
    private int turnCount = 0;
    private int soundDelay = 0;
    private int timeDelay = 0;
    private TypeOfLoss typeOfLoss = TypeOfLoss.NO_LOSS;

    public int getTurnCount() {
        return turnCount;
    }
    public int getUnimonPerDeck() {
        return unimonPerDeck;
    }
    public int getSchoolCardsPerDeck() {
        return schoolCardsPerDeck;
    }
    public int getHandSize() {
        return handSize;
    }
    public int getBenchSize() {
        return benchSize;
    }


    //GAME OBJECTS
    public Card selectedCard = null;
    private Board board;
    private PopUp popup = null;
    private RockPaperScissors rockPaperScissorGame;
    private DeckBuilder deckBuilder;
    private CardMovement cardMovement;
    private ScreenViewportReleaseButton pauseButton;
    private WitPointsBar humanWitPointsBar;
    private WitPointsBar aiWitPointsBar;
    public DeckBuilder getDeckBuilder() { return deckBuilder; }


    //PLAYERS
    private Player human, winner;
    private AI ai;

    public Player getCurrentPlayer() { return turnCount%2 == 0 ? human : ai; }
    public Player getHuman() { return human; }
    public Player getAI() { return ai; }


    //Animation and Sound
    private ArrayList<IAnimation> animations = new ArrayList<>(5);
    private ArrayList<Sound> sounds = new ArrayList<>(5);

    public void addAnimation(IAnimation animation){ animations.add(animation); }
    public void addSound(Sound sound) { sounds.add(sound); }


    //TOUCH INPUT AND DRAG VARIABLES
    private Vector2 lastTouchPos = new Vector2(0, 0);


    //AI
    public AIPerformMoves aiPerformMoves;


    //USED TO SKIP TURNS DURING TESTING, COMMENT OUT setUpEndTurn() IN THE CONSTRUCTOR TO DISABLE.//
    private ScreenViewportReleaseButton endTurn;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// CONSTRUCTORS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public BattleScreen(Game game, int level)
    {
        super("Battle Screen", game); // init super class

        //Setup the players.
        human = new Player("Human", witpoints, this);
        human.setLevel(level); //Required to check level progression requirements are met (no increment level on replay)
        ai = createAI(level);

        //Setup the layer viewport.
        layerViewport = new LayerViewport(layerViewportWidth / 2, BOARD_HEIGHT / 2,
                layerViewportWidth / 2, layerViewportHeight / 2);


        setUpScreenViewPort();
        setUpPause();
        setUpDeckBuilder();
        loadUnimonImages();
        //Setup the Board
        board = new Board(BOARD_WIDTH / 2, BOARD_HEIGHT / 2, BOARD_WIDTH, BOARD_HEIGHT,
                assetManager.getBitmap("BOARD"), this);
        getPlayerCardsReady();
        cardMovement = new CardMovement(human, evolveLimit, chargeLimit, this, board);
        setUpWitPointsBars();
        aiPerformMoves = new AIPerformMoves(board, this);

        /**
         * COMMENT THIS LINE TO PLAY BY THE RULES
         */
        addPlayersPrizeCardsToDeck();

        //////////////////////////////////////////////////////////////
        setUpEndTurn();
        //^^^^^^^^^^^^^UNCOMMENT THIS LINE TO ACTIVATE THE SKIP BUTTON.
        //             COMMENT TO PLAY BY THE RULES.

    }



    /**
     * @author Michael Grundie
     * Test Constructor
     */
    public BattleScreen(Game game)
    {
        super("battlescreen", game);
    }



    /**
     * Adds additional unimon cards the user/player had collected has prize cards (from previous battles) to the player deck.
     * @Author: Jodie Burnside
     */
    private void addPlayersPrizeCardsToDeck()
    {
        if(mGame.getPlayerProfile().getPrizeCards().size()>0)
        {
            Log.d(TAG, "addPlayersPrizeCardsToDeck: adding prize cards.");
            human.addPrizeCardsToDeck(mGame.getPlayerProfile().getPrizeCards());
        }
        else Log.d(TAG, "addPlayersPrizeCardsToDeck: no prize cards to add.");
    }



    /**
     * Sets up the AI player.
     * @param level
     * @return
     */
    private AI createAI(int level)
    {
        if (level == 1)
        {
            return new AI("AI", witpoints, 100, human, this);
        }
        else
        {
            return new AI("AI", witpoints, 100, human, this);
        }
    }



    /**
     * @author Michael Grundie.
     *
     * Deals card to players and draws cards to thir hands.
     */
    private void getPlayerCardsReady()
    {
        board.dealCards(human, ai);
        human.fillHand(handSchoolCardLimit);
        ai.fillHand(handSchoolCardLimit);
        board.syncPlayerContainerWithBoardContainer(human, PlayArea.HAND);
        human.getPlayerHand().updateCards(null);
    }



    /**
     * @author v.1 Courtney Shek - Pause rect.
     * @author v.2 Michael Grundie - Pause button.
     */
    private void setUpPause()
    {
        float widthAndHeight = screenViewport.height/10;
        pauseButton = new ScreenViewportReleaseButton(screenViewport.right - widthAndHeight/2, screenViewport.top+widthAndHeight/2,
                widthAndHeight,widthAndHeight, "PAUSE",null , "POP", null, this);
    }



    /**
     * @author v.1 Chloe McMullan - Text Holder Button
     * @author v.2 Michael Grundie - Converted to ScreenViewportReleaseButton
     */
    private void setUpEndTurn()
    {
        float widthAndHeight = screenViewport.height/10;
        endTurn = new ScreenViewportReleaseButton(screenViewport.right - widthAndHeight/2, screenViewport.top+widthAndHeight*2,
                widthAndHeight,widthAndHeight, "SKIPTURN",null , "POP", "SKIP", this);
    }



    /**
     * @author Michael Grundie.
     */
    private void setUpDeckBuilder()
    {
        assetManager.loadAndAddTxtFile("UnimonStats.txt", "UnimonStats.txt");
        this.deckBuilder = new DeckBuilder(mGame);
    }



    /**
     * @author Michael Grundie.
     */
    private void loadUnimonImages()
    {
        for(Unimon unimon: deckBuilder.getAllUnimon())
        {
            String unimonPicFile = "img/unimonImages/" + unimon.getName()+".png";
            mGame.getAssetManager().loadAndAddBitmap(unimon.getName(), unimonPicFile);
        }
    }



    /**
     * @author v.1 Courtney Shek.
     * @author v.2 Michael Grundie.
     *
     * Sets up WitPoints bar for player and AI
     */
    private void setUpWitPointsBars()
    {
        float indent  = screenViewport.height/100*1;
        float height = screenViewport.height/25;
        float width = screenViewport.width/2.5f;

        aiWitPointsBar = new WitPointsBar(screenViewport.left + indent + width/2,
                screenViewport.top + indent + height/2, width,height, this, ai, 20);

        humanWitPointsBar = new WitPointsBar(screenViewport.right - indent - width/2,
                screenViewport.bottom - indent - height/2, width,height, this, human, 20);

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// UPDATE ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Michael Grundie.
     * @param elapsedTime
     */
    @SuppressLint("NewApi")
    @Override
    public void update(ElapsedTime elapsedTime)
    {
        /**
         * Pause check gets first priority.
         */
        pauseCheck(elapsedTime);

        /**
         * Handle and pop-ups, or the rock/paper/scissors game before allowing anything else to
         * happen.
         */
        if(popup != null)
        {
            handlePopUp(elapsedTime);
            return;
        }

        if (rockPaperScissorGame != null)
        {
            rockPaperScissorsActions(elapsedTime);
            return; //stops dragging behind the screen
        }


        /**
         * If the doFocusToMiddle boolean flag is true: do nothing else until the viewport
         * moves to the active cards area.
         */
        if(doFocusToMiddle)
        {
            doFocusToMiddle = focusToMiddle();
            return;
        }

        /**
         * Updating the animations and sounds.
         */
        updateAnimations(elapsedTime);
        playSounds();



        /**
        * Used when we want to give the user some time to take in what has just happened.
        */
        if(timeDelay > 0)
        {
            timeDelay --;
            if (timeDelay == 1) timeDelayFinished = true;
            return;
        }


        /**
         * Moves the viewport to the new player's side of the board before allowing anything else to
         * happen.
         */
        if (!finishedFocusShift && !cardMovement.moveHappening())
        {
            if (displayFocusShift(turnCount % 2 == 0 ? human : ai))
            {
                finishedFocusShift = true;
                if(getCurrentPlayer() == human)
                {
                    board.syncPlayerContainerWithBoardContainer(human, PlayArea.HAND);
                }
            }
            else return;
        }


        /**
         * Check if the player has used their basic, or special move.
         */
        if(turnCount > 1 && getCurrentPlayer() == human) finalMoveCheck();


        /**
         * This progresses a defeated AI active card towards the graveyard.
         */
        if(getCurrentPlayer() == ai && turnCount>1 && cardMovement.moveHappening())
        {
            cardMovement.finishMoveAfterTouchUp(false,null);
            return;
        }


        /**
         * The AI performs it's moves.
         */
        if (finishedFocusShift && getCurrentPlayer()==ai) performAIMoves();

        /**
         * Checking for touch input bt the human, on the human's turn this will
         * initiate card movements.
         */
        checkTouchInput();


        /**
         * Updating the board so cards can be drawn.
         */
        board.update(elapsedTime);


        /**
         * Set the game to start the next  player's turn afteranimations have completed.
         */
        if (turnResolved && animations.size()==0)
        {
            newTurnActions();

            if(typeOfLoss != TypeOfLoss.NO_LOSS)return;
        }

        updateWitpointBars(elapsedTime);

        endTurnButtonCheck(elapsedTime);

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// UPDATE METHODS ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @author Chloe McMullan.
     *
     * Analyses the user's touch inputs and takes the appropriate actions.
     */
    private void checkTouchInput()
    {
        Input input = mGame.getInput();


        for (TouchEvent e : input.getTouchEvents()) {
            Vector2 v = new Vector2(0, 0); //vector containing layer co-ordinates
            InputHelper.convertScreenPosIntoLayer(screenViewport, e.x, e.y, layerViewport,
                    v);
            if (getCurrentPlayer() == human) humanTurnUpdate(e, v);
        }



    /**
     * @author Michael Grundie.
     *
     * Action up work-around.
     */

        if(!input.fingerStillOnScreen() && cardMovement.moveHappening() &&
                cardMovement.finishMoveAfterTouchUp(popupResults,lastTouchPos))
        {
            Log.d(TAG, "checkTouchInput: should play sound");


            if(turnCount == 0)
            {
                human.fillHand(handSchoolCardLimit);
                board.syncPlayerContainerWithBoardContainer(human, PlayArea.HAND);
                if(checkIfSetupTurnResolved())
                {
                    turnResolved =true;
                }
            }
        }

    }


    /**
     * @author Chloe McMullan
     *
     * Performs actions on the human turn on each turn
     * @param e
     * @param v
     */
    private void humanTurnUpdate(TouchEvent e, Vector2 v)
    {
        switch (e.type) {
            case TouchEvent.TOUCH_DRAGGED:
                draggedActions(v, e);
                break;
            case TouchEvent.TOUCH_UP:
                break;
            case TouchEvent.LONG_PRESS:
                longPressActions(v, e);
                break;
            case TouchEvent.TOUCH_DOWN:
                touchDownActions(v, e);
                break;
            default: break;
        }
    }



    /**
     * @author Michael Grundie.
     *
     * At this point the game has switched back to the player that has just been attacked.
     * This method checks to see if the attacked unimon has passed out (RIP) and sends it to the
     * graveyard and removes a wit-point from the player.\
     */
    private void checkForActiveDeath()
    {
        if(getCurrentPlayer().getPlayerActiveContainer().getAllCards().size() > 0)
        {
            Log.d(TAG, "checkForActiveDeath: Active card > 0");

            if(((UnimonCard)getCurrentPlayer()
                    .getPlayerActiveContainer().getAllCards().get(0)).getHealth() <= 0)
            {
                Log.d(TAG, "checkForActiveDeath: health <= 0");

                cardMovement.setSourceHolder(board.getActiveHolder(getCurrentPlayer()).position);
                cardMovement.startGraveYardMove(board.getGraveYardHolder());
                getCurrentPlayer().setWitPoints(getCurrentPlayer().getWitPoints() - witpointKoCost);
            }
        }
    }



    /**
     * @author Michael Grundie
     *
     * Checking if the game has ended.
     * @return
     */
    public boolean gameOverCheck()
    {
        if (!getCurrentPlayer().checkIfPlayerHasAnyUnimonCardsLeft())
        {
            Log.d(TAG, "gameOverCheck: no unimon left");
            typeOfLoss = TypeOfLoss.OUT_OF_CARDS;
            winner = getCurrentPlayer() instanceof AI ? human : ai;
        }
        if (getCurrentPlayer().getWitPoints() <= 0)
        {
            typeOfLoss = TypeOfLoss.OUT_OF_WITPOINTS;
            winner = getCurrentPlayer() instanceof AI ? human : ai;
        }

        if(typeOfLoss != TypeOfLoss.NO_LOSS)
        {
            Log.d(TAG, "gameOverCheck: Loser found");
            String wonOrLost;
            if(winner == human) wonOrLost = "You have won! AI has " + typeOfLoss.getDescriptionOfLoss();
            else wonOrLost = "You have lost! You have " + typeOfLoss.getDescriptionOfLoss();

            setPopUp("LEAVE", null, wonOrLost, PopUp.SIZE.LARGE);
            return true;
        }

        return false;
    }



    /**
     * @author Michael Grudie.
     *
     * Close this screen and pass the results.
     */
    private void endGame()
    {
            mGame.getScreenManager().removeScreen(this.getName());
            BattleEndScreen mapScreen = new BattleEndScreen(mGame, board.getGraveYardCards(), ai, human, winner == human);
            mGame.getScreenManager().addScreen(mapScreen);
            mGame.getScreenManager().setAsCurrentScreen("Battle End Screen");
    }



    /**
     * @author Michael Grundie.
     * Checks that pop-up queries for acknowledgement or query results.
     *
     * @param elapsedTime -
     *                    Used in the pop-ups update method.
     */
    private void handlePopUp(ElapsedTime elapsedTime)
    {
        popup.update(elapsedTime);
        if(popup.positivePush)
        {
            popupResults = true;
            popup = null;
            if(typeOfLoss != TypeOfLoss.NO_LOSS) endGame();
            return;
        }

        if(popup.negativePush)
        {
            popupResults = false;
            popup = null;
            return;
        }
    }



    /**
     * Authors:
     * Idea and previous implementation: Courtney Shek.
     * This implementation: Michael Grundie.
     *
     * Calls the menu screen when the pause button has been pushed.
     * @param elapsedTime
     */
    private void pauseCheck(ElapsedTime elapsedTime)
    {
        pauseButton.update(elapsedTime);

        if(pauseButton.pushTriggered())
        {
            pauseButton.reset();
            ScreenManager screenManager = mGame.getScreenManager();
            PauseScreen pauseScreen = new PauseScreen(mGame);
            screenManager.addScreen(pauseScreen);
            screenManager.setAsCurrentScreen("Pause Screen");
            return;

        }
    }



    /**
     *
     */
    private void endTurnButtonCheck(ElapsedTime elapsedTime)
    {
        endTurn.update(elapsedTime);

        if(endTurn!= null && endTurn.pushTriggered())
        {
            turnResolved = true;
            return;
        }
    }



    /**
     * @author Michael Grundie.
     *
     * Prepares the battlescreen for a new turn.
     *
     * - Updates the current player's active card.
     * - Flags that the focus should be shifted to the new player.
     * - Fill's the current player's active card.
     */
    private void newTurnActions()
    {
        if (turnCount == 0 )
        {
            if(animations.size()==0)
            {
                if(timeDelayFinished == false)
                {
                    timeDelay = 10;
                    return;
                }
                timeDelayFinished = false;
                setPopUp("OK", null, "No more possible setup actions.", PopUp.SIZE.MEDIUM);
            }
            else return;
        }

        turnResolved = false;
        finishedFocusShift = false;
        turnCount++;

        aiPerformMoves.reset();
        cardMovement = new CardMovement(getCurrentPlayer(),evolveLimit,chargeLimit,this,board);


        if(getCurrentPlayer() == human)
        {
            human.fillHand(handSchoolCardLimit);
            if(human.getPlayerActiveContainer().getAllCards().size()>0)
                ((UnimonCard)human.getPlayerActiveContainer().getAllCards().get(0)).turnUpdates();
        }
        else
        {
            ai.fillHand(handSchoolCardLimit);
            if(ai.getPlayerActiveContainer().getAllCards().size()>0)
                ((UnimonCard)ai.getPlayerActiveContainer().getAllCards().get(0)).turnUpdates();
        }

        if(turnCount>1)checkForActiveDeath();
        if(gameOverCheck()) return;
        if(turnCount == 2 && !rockPaperScissorsComplete) rockPaperScissorGame = new RockPaperScissors(this, ai.getDifficulty());
    }



    /**
     * @author Michael Grundie.
     *
     * Checks if the human player has selected their final turn-ending move and  performs that move.
     */
    private void finalMoveCheck()
    {
        if(selectedCard != null && selectedCard.getType()!= Card.TypeOfCard.SCHOOL)
        {
            UnimonCard selectedUnimon = (UnimonCard) selectedCard;
            if (selectedUnimon.isActive())
            {
                //UnimonCard selectedUnimon = (UnimonCard) selectedCard;
                School moveType = selectedUnimon.finalMoveSelected();
                if(moveType != null)
                {
                    UnimonMove finalMove = new UnimonMove(human, ai, moveType, this);
                    finalMove.performMove();
                    selectedUnimon.deselect();
                    selectedCard = null;
                    doFocusToMiddle = true;
                    turnResolved = true;
                }
            }
        }
    }



    private void updateWitpointBars(ElapsedTime elapsedTime)
    {
        humanWitPointsBar.update(elapsedTime);
        aiWitPointsBar.update(elapsedTime);
    }



    /**
     * @author Michael Grundie
     */
    private void playSounds()
    {
        if(mGame.getPlayerProfile().getSoundEnabled())
        {
            if (soundDelay > 0) soundDelay--;
            if (sounds.size() > 0 && soundDelay == 0) {
                sounds.get(0).play();
                sounds.remove(0);
                soundDelay = 15;
            }
        }else sounds.clear();
    }



    /**
     * @author Michael Grundie.
     *
     * Updates any animations in the animation array list. Removes any completed animations.
     * @param elapsedTime
     */
    private void updateAnimations(ElapsedTime elapsedTime)
    {
        for (IAnimation animation : animations)
        {
            animation.update(elapsedTime);
        }

        for (Iterator<IAnimation> it = animations.iterator(); it.hasNext();)
        {
            IAnimation an = it.next();
            if (an.isFinished())
            {
                it.remove();
                Log.d(TAG, "update: Number of animations left = " + animations.size());
            }
        }
    }



    /**
     * @author Michael Grundie.
     *
     * The set-up human turn can not be ended with an attack, this method detects when the player
     * takes all possible actions.
     */
    private boolean checkIfSetupTurnResolved()
    {
        int currentBenchSize  = human.getPlayerBench().getAllCards().size();
        int activeFilled = human.getPlayerActiveContainer().getAllCards().size();
        if (currentBenchSize == benchSize && activeFilled == 1 )
        {

            if(cardMovement.maxedSchoolCardActions())
            {
                return true;
            }

            for(Card card : human.getPlayerHand().getAllCards())
            {
                if(card.getType() == Card.TypeOfCard.SCHOOL) return false;
            }

            return true;
        }
        return false;
    }



    /**
     * @author Chloe McMullan.
     *
     * Updates the rock paper scissors accordingly, and deletes if required
     * @param elapsedTime time that has passed
     * @return true if actions have been performed, false otherwise
     */
    private boolean rockPaperScissorsActions(ElapsedTime elapsedTime)
    {
        rockPaperScissorGame.update(elapsedTime);
        if (rockPaperScissorGame.isComplete()) {
            turnCount = (rockPaperScissorGame.playerWon() ? 4 : 5);
            rockPaperScissorGame = null;
            rockPaperScissorsComplete = true;
            return true;
        }
        return false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// DISPLAY FOCUS SHIFT ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Michael Grundie.
     */
    public void startCentreFocus()
    {
        doFocusToMiddle = true;
    }



    /**
     * @author Michael Grundie.
     *
     * Moves the layer viewport's y position a little o to the current player's side of the board.
     *
     * @param player - The current player.
     * @return - True once the viewport has reached the correct position.
     */
    public boolean displayFocusShift(Player player)
    {
        if (player.getName() == "AI" && layerViewport.getTop() != BOARD_HEIGHT) {
            layerViewport.y += 20;
            if (layerViewport.y > BOARD_HEIGHT - layerViewport.halfHeight) {
                layerViewport.y = BOARD_HEIGHT - layerViewport.halfHeight;
                return true;
            }
            return false;
        }

        if (player.getName() == "Human" && layerViewport.getBottom() != 0) {
            layerViewport.y -= 20;
            if (layerViewport.getBottom() < 0) {
                layerViewport.y = layerViewport.halfHeight;
                return true;
            }
            return false;
        }

        return true;
    }



    /**
     * @author Michael Grundie.
     *
     * Centres the focus around the 2 active unimon cards
     */
    public boolean focusToMiddle()
    {
        float middle = BOARD_HEIGHT - (2* CARD_HEIGHT) - (2* yAxisCardSpacing) - (yAxisCardSpacing/2);

        if(layerViewport.y == middle) return false;

        if(layerViewport.y > middle)
        {

            layerViewport.y = Math.max(middle, layerViewport.y - 20);

        }
        else
        {
            layerViewport.y = Math.min(middle, layerViewport.y + 20);
        }

        return true;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// AI ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Chloe McMullan.
     *
     * Performs the AI moves by calling relevant aiperformmoves and ai. Changes the turn when completed.
     */
    private void performAIMoves() {
        if (!aiPerformMoves.hasStarted() && !aiPerformMoves.hasFinished())
        {
            Log.d(TAG, "performAIMoves: I'm in here!");
            if (turnCount < 2) aiPerformMoves.addMoves(ai.firstTurn());
            else aiPerformMoves.addMoves(ai.aiTurn());
        }

        if (aiPerformMoves.hasFinished()) {
            turnResolved = true;
        }
        else aiPerformMoves.performMoves();


    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// TOUCH INPUT RESOLUTION ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * @author Michael Grundie.
     * Actions if a long press has occured
     * @param v vector containing events position as layer position
     * @param e touch event that occured
     */
    public void longPressActions(Vector2 v, TouchEvent e)
    {
        if(cardMovement.getSource() != null)
        {
            if(cardMovement.getSource().getCard() != null)
            {
                if(selectedCard != null) selectedCard.deselect();
                selectedCard = cardMovement.getSource().getCard();
                selectedCard.select();
            }
        }
    }



    /**
     * @author Michael Grundie.
     *
     * Actions if a touch down has occured
     * @param v vector containing events position as layer position
     * @param e touch event that occured
     */
    public void touchDownActions(Vector2 v, TouchEvent e)
    {
        //Deselects and shrinks the selected card it the touch is outside it's bounds.
        if(selectedCard!= null)
        {
            if(!selectedCard.getBound().contains(v.x,v.y))
            {
                selectedCard.deselect();
                selectedCard = null;
            }
        }


        //Limits the human's touches to the their own side of the board.
        if(cardMovement.moveHappening() == false && v.y < 3*(CARD_HEIGHT + getYAxisCardSpacing()))
        {
            cardMovement.setSourceHolder(v);
        }

    }



    /**
     * @author Michael Grundie.
     *
     * Performs the dragged actions
     *
     * @param v vector container layer viewport position
     * @param e event containing sceen viewport position
     */
    public void draggedActions(Vector2 v, TouchEvent e)
    {
        //Shrinks the selected card if one exists.
        if(selectedCard != null)
        {
            selectedCard.deselect();
            selectedCard = null;
        }

        //Start a new card drag
        if(cardMovement.cardDragPossible())
        {
            cardMovement.startNewMove();
        }

        //Move the layer viewport when no card drag is happening.
        if (cardMovement.moveHappening() == false)
        {
            moveLayerViewport(e);
        }
        else
        //If it is not a new card drag, update the current card drag.
        {
            if(v.y > 3*(CARD_HEIGHT+yAxisCardSpacing)) v.y = 3*(CARD_HEIGHT+yAxisCardSpacing);
            lastTouchPos = v;
            cardMovement.moveCardOnScreen(NUMBER_OF_UPDATES,v);

            /**
             * Moves the viewport up and down with the card.
             */
            if(v.y < layerViewport.getBottom()+20)
            {
                layerViewport.y -=20;
                if (layerViewport.y < layerViewport.halfHeight) layerViewport.y = layerViewport.halfHeight;
            }
            else if(v.y > layerViewport.getTop()-20)
            {
                layerViewport.y+=20;

            }
        }
    }



    /**
     * @author Michael Grundie.
     * @param e
     */
    private void moveLayerViewport(TouchEvent e)
    {
        float moveY = e.y - e.oldY;
        float newY;

        //Stops the layer viewport passing the bottom of the board.
        if (layerViewport.y + moveY < layerViewport.halfHeight)
            newY = layerViewport.halfHeight;

            //Stops the layer viewport passing the top of the board.
        else if (layerViewport.y + moveY > board.getBound().getHeight() - layerViewport.halfHeight)
            newY = board.getBound().getHeight() - layerViewport.halfHeight;

            //Distance to move the viewport
        else newY = layerViewport.y + moveY;

        layerViewport.set(layerViewport.halfWidth, newY
                , layerViewport.halfWidth, layerViewport.halfHeight);
    }



    /**
     * @author Michael Grundie.
     */
    public void setPopUp(String button1, String button2, String message, PopUp.SIZE size)
    {
        if (popup == null) {
            if (button2 == null)
                popup = new PopUp(layerViewport, this, "POPUP", "BUTTON", size, button1, message);
            else
                popup = new PopUp(layerViewport, this, "POPUP", "BUTTON","BUTTON", size, button1, button2, message,2);

            addSound(assetManager.getSound("ERROR"));
            sounds.get(0).play();
            sounds.clear();

        }else
        {
            Log.e(TAG, "setPopUp: Pop up already set!");
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// DRAW //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2d)
    {
        board.draw(elapsedTime, graphics2d, layerViewport, screenViewport, new Paint());

        //Ensures the moving card is always drawn above the other cards.
        cardMovement.drawSeeking(elapsedTime, graphics2d, layerViewport, screenViewport, new Paint());

        //Draw the witpoint bars
        aiWitPointsBar.draw(elapsedTime, graphics2d, null, screenViewport);
        humanWitPointsBar.draw(elapsedTime, graphics2d, null, screenViewport);

        //Used to skip turns quicker during testing.
        if(endTurn!= null)endTurn.draw(elapsedTime, graphics2d, screenViewport);

        //Fade the screen to the player knows touch is disabled.
        if (getCurrentPlayer() == ai)   greyScreen(graphics2d);

        if(popup != null) popup.draw(elapsedTime,graphics2d,layerViewport,screenViewport);

        if (rockPaperScissorGame != null) rockPaperScissorGame.draw(graphics2d);

        //Draw any animations in the list.
        drawAnitmations(elapsedTime, graphics2d);

        //Draw the enlarged selected card.
        if(selectedCard!= null)selectedCard.draw(elapsedTime, graphics2d, layerViewport, screenViewport, new Paint());

        pauseButton.draw(elapsedTime, graphics2d, screenViewport);


    }



    /**
     * @author Michael Grundie
     *
     * @param elapsedTime
     * @param iGraphics2D
     */
    private void drawAnitmations(ElapsedTime elapsedTime, IGraphics2D iGraphics2D)
    {
        for (IAnimation animation : animations) {

            if(animation instanceof moveUpOrDownGameObjectAnimation)
            {
                ((moveUpOrDownGameObjectAnimation)animation).draw(elapsedTime,iGraphics2D,layerViewport,screenViewport);
            }
            else if(animation instanceof PopOutFromCentreOfGameObjectAnimation)
            {
                ((PopOutFromCentreOfGameObjectAnimation) animation).draw(elapsedTime,iGraphics2D,layerViewport,screenViewport);
            }
        }
    }



    /**
     * @author Chloe McMullan
     * Sets the play area to greyed out to indicate player cannot attack
     * @param graphics2d canvas to draw to
     */
    private void greyScreen(IGraphics2D graphics2d)
    {
        Rect greyOut = new Rect(screenViewport.right, screenViewport.top, screenViewport.right,
                screenViewport.bottom);
        Paint greyOutPaint = new Paint();
        greyOutPaint.setColor(Color.LTGRAY);
        greyOutPaint.setAlpha(20);
        graphics2d.drawRect(greyOut, greyOutPaint);
    }
}