package crazyhoneybadgerstudios.game.Game_Elements;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.ImageHolder;
import crazyhoneybadgerstudios.engine.graphics.ImageTextHolder;
import crazyhoneybadgerstudios.engine.graphics.TextHolder;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.util.GraphicsHelper;

/**
 * @author Chloe McMullan - 22/04/2017.
 * @author Chloe McMullan
 */

public class RockPaperScissors {

    private final String TAG = this.getClass().getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// FINAL VALUES ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private final int MAXIMUM_ROATION = 20; //maximum rotation to either side in degrees
    private final int ROTATION_PER_UPDATE = 1;
    private final String CHOOSE_CARD = "Fight for the first turn! Choose your card.";
    private final String AI_WINS = "AI WINS!";
    private final String PLAYER_WINS = "PLAYER WINS!";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// NON-IMAGE VALUES ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private BattleScreen battleScreen;

    private int aiDifficulty;
    private int playerChoice = -1;
    private int aiChoice = -1;
    private boolean drawChoices = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// IMAGE VALUES /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<ImageTextHolder> imageTextHolders = new ArrayList<>(); //holders to be redrawn
    private ImageHolder[] cards = new ImageHolder[3]; //order: hum, medical, stem
    private int[] lastRotationInDegrees = {-7, 0, 13}; //starting rotations in degrees
    private boolean[] isRotationPositive = {true, false, true}; //links to []lastRotationInDegrees
    private float MAXIMUM_SCALE;
    private float CHANGE_SCALING_AMOUNT;
    private float currentScale; //size to scale by
    private int PADDING;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// IMAGES/RECTS/HOLDERS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Matrix matrix = new Matrix(); // matrix for all cards to use
    private Rect whiteWashBackground; //whitewashes the background
    private Paint whiteWashBackgroundPaint = new Paint();
    private Rect cardSpace; //space for the cards to be drawn to
    private TextHolder topTextHolder; // holder for the text at the top

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// BOOLEAN CHECKS FOR BATTLESCREEN //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private boolean playerWins = false;
    private boolean completed = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// INITIALIZATION ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Sets up all images, rotation values, and other instance variables
     * @param battleScreen battle screen this will be drawn onto
     * @param aiDifficulty difficulty of the AI - affects chance of AI winning
     */
    public RockPaperScissors(BattleScreen battleScreen, int aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
        this.battleScreen = battleScreen;

        PADDING = battleScreen.getScreenViewPort().height/20;

        setUpImages();

        MAXIMUM_SCALE = workOutMaximumScaling(cardSpace, battleScreen.getCardHeight(),
                battleScreen.getCardWidth());
        currentScale = MAXIMUM_SCALE;
        CHANGE_SCALING_AMOUNT = MAXIMUM_SCALE/50;
    }



    /**
     * Sets up images, rects, and holders required
     */
    private void setUpImages() {
        loadBitmaps(); // loads in the bitmaps

        loadWhiteWash(); // loads in the fade in white wash

        // The 'background image' - what all the text and following images will be drawn into
        ImageHolder background = new ImageHolder(battleScreen.getGame().getAssetManager().
                getBitmap("WhiteButton"), null,
                new Rect(whiteWashBackground.left, whiteWashBackground.top,
                        whiteWashBackground.right, whiteWashBackground.bottom), null);
        imageTextHolders.add(background);

        loadTopTextPaint(background); // adds the text that will be drawn to the top

        loadInitialCardsAndCardSpace(background); // loads the first cards to be drawn
    }



    /**
     * Loads the bitmaps for use in the rock paper scissors. If any can't be loaded, an System.exit
     * is called.
     */
    private void loadBitmaps() {
        //only adds images if not already added
        try {
            if (battleScreen.getGame().getAssetManager().getBitmap("HUMANITARIANCARD") == null)
                battleScreen.getGame().getAssetManager().loadAndAddBitmap("HUMANITARIANCARD",
                        "img/CardImages/HumanitarianCard.png");
            if (battleScreen.getGame().getAssetManager().getBitmap("MEDICALCARD") == null)
                battleScreen.getGame().getAssetManager().loadAndAddBitmap("MEDICALCARD",
                        "img/CardImages/MedicalCard.png");
            if (battleScreen.getGame().getAssetManager().getBitmap("STEMCARD") == null)
                battleScreen.getGame().getAssetManager().loadAndAddBitmap("STEMCARD",
                        "img/CardImages/StemCard.png");
            if (battleScreen.getGame().getAssetManager().getBitmap("WhiteButton") == null)
                battleScreen.getGame().getAssetManager().loadAndAddBitmap("WhiteButton",
                        "img/white-button.png");
        }
        catch (Exception e) {
            Log.d(TAG, "setUpImages: Problem setting up an image in Rock Paper scissors");
            Log.d(TAG, "setUpImages: message: " + e.getMessage());
            Log.d(TAG, "setUpImages: Exiting program. Check all images are intact as required");
            System.exit(1);
        }
    }


    /**
     * Loads the white washed background that causes a 'fade in' effect. This prevents touches
     * not intended for the rock paper scissors to be excluded from here.
     */
    private void loadWhiteWash() {
        // initalizes white washed background (allows for fade in)
        whiteWashBackground = new Rect(battleScreen.getScreenViewPort().left,
                battleScreen.getScreenViewPort().top,
                battleScreen.getScreenViewPort().right, battleScreen.getScreenViewPort().bottom);
        whiteWashBackgroundPaint.setColor(Color.WHITE);
        whiteWashBackgroundPaint.setAlpha(100);

    }


    /**
     * Loads in the text that will be drawn along the top
     * @param background the background to be drawn to - allows for padding to be accounted for
     */
    private void loadTopTextPaint(ImageHolder background) {

        // holder for the top text - paddings are due to the 'rounded rectangle' of the background
        Paint topTextPaint = new Paint(); String topText = CHOOSE_CARD;

        // for setting the best size of text for this
        topTextPaint.setTextSize(GraphicsHelper.workOutBestTextSize(background.getRectDest().height() / 5 - 2*PADDING,
                topText, background.getRectDest().width() - 2*PADDING, 0.5f));

        topTextHolder = new TextHolder(topText,
                new Rect(background.getRectDest().left + PADDING,
                        background.getRectDest().top + 2*PADDING, //accounts for curved rect
                        background.getRectDest().right - PADDING,
                        background.getRectDest().height()/5 + background.getRectDest().top),
                topTextPaint, null);

        imageTextHolders.add(topTextHolder);
    }



    /**
     * Draws the cards which are for the user to choose from. This is defaulted to start 2/5
     * down the screen, allowing for (approximately) 3/5 of the background to draw to
     * @param background the imageholder
     */
    private void loadInitialCardsAndCardSpace(ImageHolder background) {
        // Card space is where the cards should be drawn to
        // top = bottom of text holder (roughly 1/5 of background but accounts for padding),
        // plus 1/5 of background, + padding. Ensures elements are spaced
        cardSpace = new Rect(background.getRectDest().left,
                topTextHolder.getRect().bottom + background.getRectDest().height() / 5 + PADDING,
                background.getRectDest().right, background.getRectDest().bottom - PADDING);

        cards[0] = new ImageHolder(battleScreen.getGame().getAssetManager().
                getBitmap("HUMANITARIANCARD"), null, workOutRectPos(0, 3, cardSpace), null);
        cards[1] = new ImageHolder(battleScreen.getGame().getAssetManager().
                getBitmap("MEDICALCARD"), null, workOutRectPos(1, 3, cardSpace), null);
        cards[2] = new ImageHolder(battleScreen.getGame().getAssetManager().getBitmap("STEMCARD"),
                null, workOutRectPos(2, 3, cardSpace), null);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// DRAW METHODS /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * The overall draw method that draws the other correct draws
     * @param iGraphics2D canvas to draw to
     */
    public void draw(IGraphics2D iGraphics2D) {

        //draws each imagetextholder
        for (ImageTextHolder h: imageTextHolders) {
            h.draw(iGraphics2D);
        }

        // draws the options
        if (!drawChoices) {
            drawOptions(iGraphics2D);
            // the scale will only reduce when we want to draw the choices instead
            if (currentScale <= 0) drawChoices = true;
        }
        // draws the picked choices
        else {
            drawChoices(iGraphics2D);
        }

        //only draw whilst the white wash will actually be seen
        if (whiteWashBackgroundPaint.getAlpha() > 0) {
            iGraphics2D.drawRect(whiteWashBackground, whiteWashBackgroundPaint);
            whiteWashBackgroundPaint.setAlpha(whiteWashBackgroundPaint.getAlpha() - 2);
        }
    }



    /**
     * Draws the options for the player to pick
     * @param iGraphics2D canvas to draw to
     */
    private void drawOptions(IGraphics2D iGraphics2D) {
        //draws each option
        for (int i = 0; i < cards.length; i++) {
            drawCard(iGraphics2D, cards[i], i, cards.length);
        }
        if (playerChoice != -1 && currentScale > 0f) currentScale -= CHANGE_SCALING_AMOUNT;
    }



    /**
     * Draws the choices picked by the player and the AI, as well as updating the text to say the
     * winner
     * @param iGraphics2D canvas to draw to
     */
    private void drawChoices(IGraphics2D iGraphics2D) {

        if (topTextHolder.getText().equals(CHOOSE_CARD)) {
            updateText(); // sets the text to indicate who won

            //updates the positions of the two choices to take up half the box now
            cards[aiChoice].setRectDest(workOutRectPos(0, 2, cardSpace));
            cards[playerChoice].setRectDest(workOutRectPos(1, 2, cardSpace));
        }

        if (currentScale < MAXIMUM_SCALE) currentScale += CHANGE_SCALING_AMOUNT; // causes zoom in effect

        drawCard(iGraphics2D, cards[aiChoice], 0, 2);
        drawCard(iGraphics2D, cards[playerChoice], 1, 2);

    }



    /**
     * Draws cardHolders to make them rotate and scale accordingly.
     * @param iGraphics2D canvas to draw to
     * @param card imageholder containing all the information about the card
     * @param i where the card is, in the arraylist cards, to know what rotation to use
     */
    private void drawCard(IGraphics2D iGraphics2D, ImageHolder card, int i, int maxCards) {
        matrix.reset(); // use the same matrix for every card for efficiency

        // works out what the next rotation update will be
        int rotation = updateRotationAndReturn(i);

        // updates matrix using current card details
        matrix.postScale(currentScale, currentScale);

        // x = left of entire space + (i* width of space/number of cards) - the left side of space to draw to
        //      + cardspace.width/(2*maxCards)                            - finds centre of this space
        //      + cardwidth/2 * currentScale                              - + half the card width scaled
        matrix.postTranslate(cardSpace.left + (i * cardSpace.width() / maxCards) + cardSpace.width()/(2*maxCards)
                        - ((card.getBitmap().getWidth()/2) * currentScale),
                        cardSpace.top);

        // rotates the rotation around the centre point
        matrix.preRotate(rotation, card.getBitmap().getWidth()/2, card.getBitmap().getHeight()/2);

        iGraphics2D.drawBitmap(card.getBitmap(), matrix, null);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// TEXT ALTERING METHODS ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////






    /**
     * Decides the correct text to reflect who won
      * @return the correct string to show who has won
     */
    private String decideText() {

        // playerwins is defaulted to false, so no need to update if AI wins
        if (aiChoice == playerChoice + 1 || (aiChoice == 0 && playerChoice == 2)) {
            return AI_WINS;
        }

        playerWins = true;
        return PLAYER_WINS;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// UPDATES //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Causes any relevant updates and takes inputs
     * @param elapsedTime time that has elapsed
     */
    public void update(ElapsedTime elapsedTime) {

        //works around touches accidentally causing a card to be clicked when first appears
        if (whiteWashBackgroundPaint.getAlpha() > 70) return;

        // if the player has chosen but the AI hasn't, make the AI choose
        if (playerChoice != -1 && aiChoice == -1) decideAIChoice();

        Input input = battleScreen.getGame().getInput();
        for (TouchEvent e: input.getTouchEvents()) {

            //tests for a touch within the options if the player hasn't chosen yet
            if (playerChoice == -1 && e.type == TouchEvent.SINGLE_TAP_CONFIRMED) {
                for (int i = 0; i < cards.length; i++) {
                    if (cards[i].getRectDest().contains((int)e.x, (int)e.y)) {
                        playerChoice = i;
                        break;
                    }
                }
            }

            // if all these conditions are met, then the final screen is shown and finished
            // animating, so a touch down will destroy this rock paper scissors
            else if (drawChoices && currentScale == MAXIMUM_SCALE && e.type ==
                        TouchEvent.TOUCH_DOWN) completed = true;

        }

    }




    /**
     * Updates and adds relevant text to show who has won, and what their choices were, after
     * choices have been made.
     */
    private void updateText() {

        // changes text to reflect who won
        String decideText = decideText();
        topTextHolder.getPaint().setTextSize(GraphicsHelper.workOutBestTextSize(
                topTextHolder.getRect().height(),
                decideText, topTextHolder.getRect().width(), 0.5f));
        topTextHolder.setText(decideText);

        addChoicesTextHolders(); // shows who chose what on the screen
    }


    /**
     * Adds the text holders that display the 'player choice' and 'ai choice' text
     */
    private void addChoicesTextHolders() {
        Paint choicesPaints = new Paint();

        // Finds text size for "Player Choice", since that's the larger of the two texts
        choicesPaints.setTextSize(GraphicsHelper.workOutBestTextSize(cardSpace.height()/4,
                "Player Choice:", cardSpace.width()/2, 0.5f));

        imageTextHolders.add(new TextHolder("AI Choice",
                new Rect(cardSpace.left + PADDING, // padding accommodates for rounded rectangle
                        // cardspace height is 3/5 of background, so -1/3 gives top to be 2/5 of
                        // background
                        cardSpace.top - (cardSpace.height()/3),
                        // Looks neater than 1/2 as this makes them appear left-justified
                        cardSpace.left + cardSpace.width()/3,
                        cardSpace.top),
                choicesPaints, null));

        imageTextHolders.add(new TextHolder("Player Choice",
                new Rect(cardSpace.left + cardSpace.width()/2, // halfway across card space
                        cardSpace.top - (cardSpace.height()/3),
                        cardSpace.right,
                        cardSpace.top), choicesPaints,
                null));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// PICK FOR AI ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Overall method to upadte the AI choice. This is based on AI difficulty.
     */
    private void decideAIChoice() {
        int randomNum = (int)Math.round(Math.random() * 100);

        // If AI difficulty is double the random number, AI automatically gets first turn
        if (aiDifficulty > randomNum * 2) {
            if (playerChoice == 2) aiChoice = 0;
            else aiChoice = playerChoice + 1;
        }

        // if AI difficulty is greater than random number, then AI has 50/50 chance of winning
        else if (aiDifficulty > randomNum) {
            switch (playerChoice) {
                case 0: decideBetweenChoices(new int[]{1, 2, -1}); break;
                case 1: decideBetweenChoices(new int[]{0, 2, -1}); break;
                case 2: decideBetweenChoices(new int[]{0, 1, -1}); break;
            }
        }

        // allows for 'It was a draw' implementation if desired, currently still provides a
        // 50/50 chance of winning
        else decideBetweenChoices(new int[]{0, 1, 2});
    }

    /**
     * Takes in an Array of choices, and chooses one (no weighting at this point)
     * @param choices array of numbers the AI can choose
     */
    private void decideBetweenChoices(int[] choices) {

        // if there is a -1 passed in, it means we only want to choose between the first 2 options
        if (choices[2] == -1) aiChoice = choices[(int)(Math.random() * 2)];

        //ensures there is no draw for now - still provides a 50/50 chance of winning
        else {
            choices[playerChoice] = -1; // what the player chose is set to -1 to prevent a draw
            while (aiChoice == -1) {
                aiChoice = choices[(int)(Math.random() * 3)];
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// UTILTY METHODS FOR POSITIONING/ALTERING /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Splits a rectangle into numOfCards number of rectangles, each occupying an even space
     * @param cardNum the number in the array of rects that we should return
     * @param numOfCards Number of rects the original space should be split into
     * @param space the space to split up
     * @return a rect containing the padded bounds
     */
    private Rect workOutRectPos(int cardNum, int numOfCards, Rect space) {
        int sectionWidth = space.width()/numOfCards;
        Rect rect = new Rect(space.left, space.top,
                space.left + sectionWidth - PADDING, space.bottom - 3*PADDING);
        rect.offset(((space.width() / numOfCards) * cardNum), 0);
        return rect;
    }


    /**
     * Works out the maximum scaling we can apply to an image to still fit in the pace provided
     * @param spaceToDrawTo the space in which we're drawing to. Padding is subrtracted within method
     * @param heightOfImage the height of the image, with no scalings applied
     * @param widthOfImage the width of the image, with no scalings applied
     * @return the maximum scaling we can apply, as a float
     */
    private float workOutMaximumScaling(Rect spaceToDrawTo, float heightOfImage, float widthOfImage) {
        // works out the ratio of the space to the original image, and divides by 2 for the scale
        float maxHeightScaling = ((spaceToDrawTo.height() - 2*PADDING) / heightOfImage) / 2;
        float maxWidthScaling = ((spaceToDrawTo.width()/3 - 2*PADDING) / widthOfImage) / 2;

        // returns the smaller of the scales
        return (maxHeightScaling > maxWidthScaling ? maxWidthScaling : maxHeightScaling);
    }


    /**
     * Updates the rotoation value for the value i in the arrays, and returns the next rotation
     * @param i where the update should occur in all the array
     * @return the rotation that should next occur for the value i
     */
    private int updateRotationAndReturn(int i) {
        // works out what the next rotation update will be
        int rotation = (isRotationPositive[i] ? lastRotationInDegrees[i] + ROTATION_PER_UPDATE :
                lastRotationInDegrees[i] - ROTATION_PER_UPDATE);

        // if the next rotation update is valid, update the current lastRotationInDegrees[i]
        if ((isRotationPositive[i] && rotation <= MAXIMUM_ROATION) || (!isRotationPositive[i] &&
                rotation >= 0 - MAXIMUM_ROATION)) {
            lastRotationInDegrees[i] = rotation;
        }
        else { // if its not valid, we've hit a boundary and want to flip back again.
            isRotationPositive[i] = !isRotationPositive[i];
            rotation = lastRotationInDegrees[i];
        }
        return rotation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GETTERS FOR OBJECT USING THIS /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Used to work out if rockPaperScissors has been completed
     * @return true = completed, false = not completed
     */
    public boolean isComplete() { return completed; }

    /**
     * Used to work out if the player won
     * @return true = player won, false = player lost
     */
    public boolean playerWon() { return playerWins; }

}
