package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.ScreenViewport;

import static crazyhoneybadgerstudios.game.Game_Elements.AssetLoading.pauseBackground;
import static crazyhoneybadgerstudios.game.Game_Elements.AssetLoading.soundOFF;
import static crazyhoneybadgerstudios.game.Game_Elements.AssetLoading.soundON;


/**
 * @author Courtney Shek
 *
 * Is called when the user pauses the game during battle
 */

public class PauseScreen extends GameScreen{

    //Rectangle variables - Used to draw bitmaps
    private Rect background;
    private Rect warningPopUp;
    private Rect gameSavedConfirmation;

    //ScreenViewportReleaseButton variables - used for buttons
    private ScreenViewportReleaseButton resumeGameButton;
    private ScreenViewportReleaseButton saveGameButton;
    private ScreenViewportReleaseButton helpButton;
    private ScreenViewportReleaseButton soundButton;
    private ScreenViewportReleaseButton returnToMapButton;
    private ScreenViewportReleaseButton returnToMainScreenButton;
    private ScreenViewportReleaseButton yesButton;
    private ScreenViewportReleaseButton noButton;
    private ScreenViewportReleaseButton okButton;

    //Button Variables
    private float buttonWidth;
    private float buttonHeight;

    //Used to change style of text
    private Paint paint;

    //Used to remove/add screens
    private ScreenManager screenManager;

    //The name of the screen the user wants to return to
    String screen = "";

    //Variables used to hold rectangle parameters
    private int rectTop;
    private int rectBottom;
    private int rectLeft;
    private int rectRight;

    //Checks to see if we need to display warning pop up
    private boolean displayPopUp = false;

    //Checks to see if the game has been saved
    private boolean gameSaved = false;

    /**
     * Creates a new Pause Screen when the user pauses the game
     *
     * @param game - The game instance the Pause Screen belongs to
     */
    public PauseScreen(Game game)
    {
        super("Pause Screen", game);
        paint = new Paint();
        screenManager = mGame.getScreenManager();

        setUpScreenViewPort();

        rectTop = screenViewport.top;
        rectBottom = screenViewport.bottom;
        rectLeft = screenViewport.left;
        rectRight = screenViewport.right;

        //AssetLoading.loadPauseMenu(game.getAssetManager());
        setUpRects();
        setUpButtons();
    }

    /**
     * Updates the Pause Screen. Invoked automatically from the game.
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    public void update(ElapsedTime elapsedTime)
    {
        updateButtons(elapsedTime);
        buttonPushed();
    }

    /**
     * Draws out the Pause Screen
     *
     * @param elapsedTime - Elapsed time information for the frame
     * @param graphics2D - Graphics instance used to draw the screen
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D)
    {
        //Draws Buttons, bitmaps and text for the screen
        graphics2D.drawBitmap(pauseBackground, null, background, null);
        drawPauseText(graphics2D);
        drawButtons(elapsedTime, graphics2D, screenViewport);

        //Displays the warning pop up and buttons if needed
        if(displayPopUp)
        {
            graphics2D.drawBitmap(AssetLoading.warningPopUpImage, null, warningPopUp, null);
            yesButton.draw(elapsedTime, graphics2D, screenViewport);
            noButton.draw(elapsedTime, graphics2D, screenViewport);
        }

        //Display save game confirmation and button when the user saves the game
        if(gameSaved)
        {
            graphics2D.drawBitmap(AssetLoading.saveConfirmationBackground, null,
                    gameSavedConfirmation, null);
            okButton.draw(elapsedTime, graphics2D, screenViewport);
        }
    }

    /**
     * Creates the rectangles used on the screen
     */
    public void setUpRects()
    {
        background = new Rect(rectLeft, rectTop, rectRight, rectBottom);
        warningPopUp = new Rect(rectLeft + background.width()/4, rectBottom/4,
                rectRight - background.width()/4, rectBottom/4 * 3);
        gameSavedConfirmation = new Rect(rectLeft + background.width()/4, rectBottom/3,
                rectRight - background.width()/4, rectBottom/3 * 2);
    }

    /**
     * Creates the buttons used on the screen
     */
    private void setUpButtons()
    {
        buttonWidth = screenViewport.width / 3;
        buttonHeight = screenViewport.height/ 5;

        float topRowY = screenViewport.top + screenViewport.height/4 + buttonHeight/2;
        float firtstColX = screenViewport.left + buttonWidth / 4 * 3;
        float secondRowY = topRowY + screenViewport.height/4 + buttonHeight/3;
        float secondColX =  screenViewport.right - buttonWidth / 4 * 3;

        float soundButtonWidthAndHeight = screenViewport.right / 15;
        float soundButtonX = screenViewport.left + soundButtonWidthAndHeight / 2;
        float soundButtonY = screenViewport.top + soundButtonWidthAndHeight / 2;

        float helpButtonX = screenViewport.right - screenViewport.width/2 ;
        float helpButtonY = screenViewport.bottom - buttonHeight/2;

        float yesNoOKButtonHeight = screenViewport.right/10;
        float yesNoOKButtonWidth = screenViewport.right/8;
        float yesNoOKButtonY = rectBottom/4 * 3 - yesNoOKButtonHeight;
        float yesButtonX = warningPopUp.left + yesNoOKButtonWidth;
        float noButtonX = warningPopUp.right - yesNoOKButtonWidth;
        float okButtonX = gameSavedConfirmation.left + yesNoOKButtonWidth * 2;

        soundButton = new ScreenViewportReleaseButton(soundButtonX, soundButtonY,
                soundButtonWidthAndHeight, soundButtonWidthAndHeight, null, null, this);
        resumeGameButton = new ScreenViewportReleaseButton(firtstColX, topRowY, buttonWidth,
                buttonHeight, "ResumeGame", null, this);
        returnToMapButton = new ScreenViewportReleaseButton(secondColX, topRowY,
                buttonWidth, buttonHeight, "ReturnToMap", null, this);
        saveGameButton = new ScreenViewportReleaseButton(firtstColX, secondRowY,
                buttonWidth, buttonHeight, "SaveGame", null, this);
        returnToMainScreenButton = new ScreenViewportReleaseButton(secondColX, secondRowY,
                buttonWidth, buttonHeight, "QuitGame", null, this);
        helpButton = new ScreenViewportReleaseButton(helpButtonX, helpButtonY, buttonWidth,
                buttonHeight, "Help", null, this);
        yesButton = new ScreenViewportReleaseButton(yesButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "YesButton", null, this);
        noButton = new ScreenViewportReleaseButton(noButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "NoButton", null, this);
        okButton = new ScreenViewportReleaseButton(okButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "OKButton", null, this);
    }

    /**
     * Draws buttons to the screen
     *
     * @param elapsedTime - Elapsed time information for the frame
     * @param graphics2D - Graphics instance used to draw buttons
     * @param screenViewport - Screen Viewport
     */
    private void drawButtons(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                             ScreenViewport screenViewport)
    {
        setSoundBitmap();
        soundButton.draw(elapsedTime, graphics2D, screenViewport);
        resumeGameButton.draw(elapsedTime, graphics2D, screenViewport);
        returnToMapButton.draw(elapsedTime, graphics2D, screenViewport);
        saveGameButton.draw(elapsedTime, graphics2D, screenViewport);
        returnToMainScreenButton.draw(elapsedTime, graphics2D, screenViewport);
        helpButton.draw(elapsedTime, graphics2D, screenViewport);
    }

    /**
     * Draws the pause text at the top of the screen
     *
     * @param graphics2D - Graphics instance used to draw text
     */
    private void drawPauseText(IGraphics2D graphics2D)
    {
        paint.setColor(Color.WHITE);
        paint.setTextSize(75);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.LEFT);
        graphics2D.drawText("Paused", background.width() / 2, background.bottom/10, paint);
        paint.setTextSize(50);
    }

    /**
     * Updates buttons
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    private void updateButtons(ElapsedTime elapsedTime)
    {
        if(displayPopUp == false && gameSaved == false)
        {
            soundButton.update(elapsedTime);
            resumeGameButton.update(elapsedTime);
            returnToMapButton.update(elapsedTime);
            saveGameButton.update(elapsedTime);
            returnToMainScreenButton.update(elapsedTime);
            helpButton.update(elapsedTime);
        }

        if(displayPopUp == true)
        {
            yesButton.update(elapsedTime);
            noButton.update(elapsedTime);
        }

        if(gameSaved == true)
        {
            okButton.update(elapsedTime);
        }
    }

    /**
     * Checks to see which button has been pressed by the user
     * Depending on which button has been pressed the appropriate action will be taken
     */
    private void buttonPushed()
    {
        if(displayPopUp == false && gameSaved == false)
        {
            if(resumeGameButton.pushTriggered())
            {
                resumeGame();
            }
            else if(saveGameButton.pushTriggered())
            {
                saveGame();
                mGame.saveData();
            }
            else if(helpButton.pushTriggered())
            {
                loadHelp();
            }
            else if(soundButton.pushTriggered())
            {

                setSound();
            }
            else if(returnToMainScreenButton.pushTriggered())
            {
                screen = "Main";
                displayPopUp = true;
            }
            else if(returnToMapButton.pushTriggered())
            {
                screen = "Map";
                displayPopUp = true;
            }
        }
        else if(displayPopUp == true)
        {
            if(yesButton.pushTriggered())
            {
                returnToScreen(screen);
            }
            else if(noButton.pushTriggered())
            {
                displayPopUp = false;
            }
        }
        else if(gameSaved == true)
        {
            if(okButton.pushTriggered())
            {
                gameSaved = false;
            }
        }
    }

    /**
     * Removes the Pause Screen and resumes the Battle screen
     */
    private void resumeGame()
    {
        screenManager.removeScreen(this.getName());
        screenManager.setAsCurrentScreen("Battle Screen");
    }

    /**
     * Saves the users current progress
     */
    private void saveGame()
    {
        gameSaved = true;

        //GameState.SaveOngoingBattle((BattleScreen)mGame.getScreenManager().getScreen("Battle Screen"));
    }

    /**
     * Loads the help screen
     */
    private void loadHelp()
    {
        HelperBook helperBook = new HelperBook(mGame, this.getName());
        screenManager.addScreen(helperBook);
        screenManager.setAsCurrentScreen("Helper Book");
    }

    /**
     * Draws the appropriate bitmap based on whether sound is on or off
     */
    private void setSoundBitmap()
    {
        if(mGame.getPlayerProfile().getSoundEnabled())
        {
            soundButton.setBitmap(soundON);
        }
        else
        {
            soundButton.setBitmap(soundOFF);
        }
    }

    /**
     * Allows the users to toggle the sound on and off
     */
    private void setSound()
    {

        if (mGame.getPlayerProfile().getSoundEnabled())
        {
            mGame.getPlayerProfile().setSoundEnabled(false);
        }
        else if (mGame.getPlayerProfile().getSoundEnabled() == false)
        {
            mGame.getPlayerProfile().setSoundEnabled(true);
        }
    }

    /**
     * Returns the player to the Main Menu Screen of the Map Screen
     *
     * @param screen - The screen which the player wishes to return to
     */
    private void returnToScreen(String screen)
    {
        screenManager.removeScreen(this.getName());
        screenManager.removeScreen("Battle Screen");
        if(screen.equals("Main"))
        {
            MenuScreen menuScreen = new MenuScreen(mGame);
            screenManager.addScreen(menuScreen);
            screenManager.setAsCurrentScreen("Menu Screen");
        }
        else if(screen.equals("Map"))
        {
            MapScreen mapScreen = new MapScreen(mGame);
            screenManager.addScreen(mapScreen);
            screenManager.setAsCurrentScreen("Map Screen");
        }
    }
}

