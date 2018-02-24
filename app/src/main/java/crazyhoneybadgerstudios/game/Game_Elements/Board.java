package crazyhoneybadgerstudios.game.Game_Elements;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import java.util.ArrayList;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardHolder;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardHolderContainer;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;;
import crazyhoneybadgerstudios.util.LayoutHelper;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;



/**
 * @author Dariusz Jerzewski
 */
public class Board extends GameObject
{
    private final String TAG = this.getClass().getSimpleName();

    //CardHolderContainers(Board Sides)
    public CardHolderContainer humanSide, aiSide;

    //Graveyard
    private CardHolder graveYardHolder;
    private ArrayList<Card> graveYardCards;

    public CardHolder getGraveYardHolder() { return graveYardHolder; }
    public ArrayList<Card> getGraveYardCards() { return graveYardCards; }

    //BattleScreen
    private BattleScreen battleScreen;

    //Players
    private Player aiPlayer, humanPlayer;



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// CONSTRUCTORS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Board(float x, float y, float width, float height,
                 Bitmap bitmap, BattleScreen battleScreen)
    {
        super(x, y, width, height, bitmap, battleScreen);
        this.battleScreen = battleScreen;

        aiPlayer = battleScreen.getAI();
        humanPlayer = battleScreen.getHuman();

        setupBoardSides();
        createGridLayout();
        setupGraveYard();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// UTILITY METHODS ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Darius Jerzewski
     *
     * Sets up both sides of the board by creating cardholders and cardholder containers.
     */
    private void setupBoardSides()
    {
        humanSide = new CardHolderContainer(3);
        aiSide = new CardHolderContainer(2);

        String[] humanKeys = {PlayArea.HAND.name(), PlayArea.BENCH.name(), PlayArea.ACTIVE.name()};
        humanSide.setupEmptyContainers(new int[]{battleScreen.getHandSize(), battleScreen.getBenchSize(), 1}, humanKeys, battleScreen);

        String[] aiKeys = {PlayArea.ACTIVE.name(), PlayArea.BENCH.name()};
        aiSide.setupEmptyContainers(new int[]{1, battleScreen.getBenchSize()}, aiKeys, battleScreen);
    }



    /**
     * @author Darius Jerzewski
     *
     * Positions the card holders on the board.
     */
    private void createGridLayout()
    {
        //Retrieving these card spacing variables once
        float xSpacing = battleScreen.getXAxisCardSpacing();
        float ySpacing = battleScreen.getYAxisCardSpacing();

        //Lays out the locations on the board using this boards' dimensions.
        LayoutHelper.CreateGridLayoutFromRows(0, 0, mBound.getWidth(), xSpacing, ySpacing, humanSide.getArrayListContainers());

        //The AI's grid needs to be drawn above the players' cards (3 cards and 3 gaps)
        LayoutHelper.CreateGridLayoutFromRows(0, (3*battleScreen.getCardHeight()) + (3*ySpacing),
                mBound.getWidth(), xSpacing, ySpacing, aiSide.getArrayListContainers());
    }



    /**
     * @author Michael Grundie.
     */
    private void setupGraveYard()
    {
        //Creating a slot on the board to hold the last UnimonCard to enter the graveyard.
        float graveYardX = mBound.getLeft()+ (battleScreen.getCardWidth()/2) + battleScreen.getXAxisCardSpacing();
        float gravYardY = mBound.getTop() - (2 * battleScreen.getCardHeight()) - (2*battleScreen.getYAxisCardSpacing()) - (battleScreen.getYAxisCardSpacing()/2);
        graveYardHolder = new CardHolder(graveYardX,gravYardY,battleScreen,PlayArea.GRAVEYARD);

        //An ArrayList to hold all the fallen UnimonCards.
        graveYardCards = new ArrayList<>(battleScreen.getUnimonPerDeck() * 2);
    }



    /**
     * @author Michael Grundie.
     *
     * Obtains a new deck from deck builder and inserts them into the player's
     * deck card container.
     * @param player1 - The player to which the cards should be dealt.
     */
    public void dealCards(Player player1, Player player2)
    {
        player1.setPlayerDeck(battleScreen.getDeckBuilder().getDeck(battleScreen.getUnimonPerDeck(),
                battleScreen.getSchoolCardsPerDeck(),battleScreen));

        player2.setPlayerDeck(battleScreen.getDeckBuilder().getDeck(battleScreen.getUnimonPerDeck(),
                battleScreen.getSchoolCardsPerDeck(),battleScreen));

        Log.d(TAG, "dealCards: " + player1.getName() + "'s cards dealt");
        Log.d(TAG, "dealCards: " + player2.getName() + "'s cards dealt");
    }



    /**
     * @author Michael Grundie.
     *
     * Synchronizes the player's card containers with the board's card containers.
     * @param player -
     *               The player to which this card container holder relates.
     * @param playArea -
     *                 The play area to be synchronised.
     */
    public void syncPlayerContainerWithBoardContainer(Player player, PlayArea playArea)
    {
        //Checking which player and play area to sync.
        ArrayList<GameObject> boardVersion;
        if(player instanceof AI)
        {
            boardVersion = aiSide.getContainers().get(playArea.name());
        }
        else boardVersion = humanSide.getContainers().get(playArea.name());

        //Loop through each card-holder/slot in the play area.
        for(int i = 0; i<boardVersion.size(); i++)
        {
            CardHolder BoardSlot = (CardHolder)(boardVersion.get(i));
            Card playerCard;


            //Check the player's version of the container.
            switch (playArea)
            {
                case HAND:   playerCard = player.getPlayerHand().getAllCards().size() >= i+1 ?  player.getPlayerHand().getAllCards().get(i) : null;
                    break;
                case BENCH:  playerCard =  player.getPlayerBench().getAllCards().get(i);
                    break;
                default :      playerCard =  player.getPlayerActiveContainer().getAllCards().get(i);
                    break;
            }

            //Clear empty spaces.
            if (playerCard == null)
            {
                BoardSlot.setCard(null);
            }//Sync cards.
            else
            {
                BoardSlot.setCard(playerCard);
            }
        }
    }



    /**
     * @author Michael Grundie.
     *
     * @param player
     * @return
     */
    public CardHolder getActiveHolder(Player player)
    {
        if(player instanceof AI)
        {
            return ((CardHolder)aiSide.getContainers().get("ACTIVE").get(0));
        }
        else
            return ((CardHolder) humanSide.getContainers().get("ACTIVE").get(0));
    }



    /**
     * @author Chloe McMullan
     *
     * Used to swap the active with a card in your bench.
     *
     * @param newActive card that will be a new active
     * @param oldActive old active card
     */
    public void swapCards(Card newActive, Card oldActive)
    {
        Player currentPlayer = battleScreen.getCurrentPlayer();

        currentPlayer.getPlayerActiveContainer().removeCard(oldActive);

        currentPlayer.moveCardToDifferentContainer(currentPlayer.getPlayerBench(),
                currentPlayer.getPlayerActiveContainer(), newActive);

        currentPlayer.getPlayerBench().addCard(oldActive);

        syncPlayerContainerWithBoardContainer(currentPlayer, PlayArea.BENCH);
        syncPlayerContainerWithBoardContainer(currentPlayer, PlayArea.ACTIVE);
    }



    /**
     * @author Dariusz Jerzewski
     *
     * Checks if a touch is within the bounds of a cardHolder and returns that cardHolder
     * @param x
     * @param y
     * @return
     */
    public GameObject checkIfTouchedCards(float x, float y)
    {
        for (ArrayList<GameObject> goal : humanSide.getContainers().values()) {
            for (GameObject go : goal) {
                if (go != null)
                    if (go.getBound().contains(x, y)){
                        return go;
                    }
            }
        }

          for(ArrayList<GameObject> goal : aiSide.getContainers().values()){
            for (GameObject go : goal) {
                if (go != null)
                    if (go.getBound().contains(x, y)){
                        return go;
                    }
            }
        }

        return null;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// UPDATE /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void update(ElapsedTime elapsedTime)
    {

        aiPlayer.getPlayerActiveContainer().updateCards(elapsedTime);
        aiPlayer.getPlayerBench().updateCards(elapsedTime);


        humanPlayer.getPlayerBench().updateCards(elapsedTime);
        humanPlayer.getPlayerActiveContainer().updateCards(elapsedTime);
        humanPlayer.getPlayerHand().updateCards(elapsedTime);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// DRAW ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint) {
        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

        drawCardHolders(elapsedTime,graphics2D,layerViewport,screenViewport, paint);

        drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);

    }



    private void drawCardHolders(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint)
    {
        aiSide.drawCardHolders(elapsedTime,graphics2D,layerViewport,screenViewport, paint);

        humanSide.drawCardHolders(elapsedTime,graphics2D,layerViewport,screenViewport, paint);

        graveYardHolder.draw(elapsedTime, graphics2D, layerViewport, screenViewport, paint);
    }



    private void drawCards(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint)
    {
        aiPlayer.getPlayerActiveContainer().drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);
        aiPlayer.getPlayerBench().drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);

        humanPlayer.getPlayerActiveContainer().drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);
        humanPlayer.getPlayerBench().drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);
        humanPlayer.getPlayerHand().drawCards(elapsedTime,graphics2D, layerViewport, screenViewport, paint);

        if(graveYardHolder.getCard() != null) {
            graveYardHolder.getCard().draw(elapsedTime, graphics2D, layerViewport, screenViewport, paint);
        }
    }


}
