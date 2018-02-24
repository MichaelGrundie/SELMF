package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.world.GameScreen;

import static crazyhoneybadgerstudios.game.Game_Elements.AssetLoading.soundOFF;
import static crazyhoneybadgerstudios.game.Game_Elements.AssetLoading.soundON;


public class MenuScreen extends GameScreen
{
    //Rectangle used to draw out the background image
    private Rect backgroundArea;

    //ScreenViewportReleaseButtons variables used for buttons
    private ScreenViewportReleaseButton soundButton;
    private ScreenViewportReleaseButton playButton;
    private ScreenViewportReleaseButton loadGameButton;

    /**
     * @author Courtney Shek and Jodie Burnside
     *
     * Creates a new menu screen when the user starts up their game
     *
     * @param game - The game instance to which the game screen belongs
     */
    public MenuScreen(Game game){

        super("Menu Screen",game);

        setUpScreenViewPort();

       // AssetLoading.loadMenu(game.getAssetManager());

        //Background Music, set to play looping is  sound is enabled.
        setUpMusic("backgroundMusic", "audio/backgroundMusic.mp3");

        //Define rects, to define positions of buttons etc. on screen
        setUpRects();

        setUpButtons();
    }

    /**
     * @author v1 Jodie Burnside - Using user touch inputs and rectangles
     * @author v2 Courtney Shek - Using screenViewportReleaseButtons
     *
     * Updates the menu screen. Invoked automatically by the game
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    public void update(ElapsedTime elapsedTime)
    {
        soundButton.update(elapsedTime);
        playButton.update(elapsedTime);
        loadGameButton.update(elapsedTime);

        if(soundButton.pushTriggered())
        {
            setSound();
        }
        else if(playButton.pushTriggered())
        {
            startNewGame();
        }
        else if(loadGameButton.pushTriggered())
        {
            loadGame();
        }
    }

    /**
     * @author v1 Jodie Burnside - Drawing bitmaps with rectangles
     * @author v2 Courtney Shek - Drawing out the screenViewportReleaseButtons
     *
     * Draws out the menu screen
     *
     * @param elapsedTime
     *            Elapsed time information for the frame
     * @param iGraphics2d
     *            Graphics instance used to draw out the screen
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D iGraphics2d)
    {
        iGraphics2d.drawBitmap(AssetLoading.background, null, backgroundArea, null);

        setSoundBitmap();
        soundButton.draw(elapsedTime, iGraphics2d, screenViewport);
        playButton.draw(elapsedTime, iGraphics2d, screenViewport);
        loadGameButton.draw(elapsedTime, iGraphics2d, screenViewport);
    }

    /**
     * @author Courtney Shek
     *
     * Creates buttons used on the screen
     */
    private void setUpButtons()
    {
        float buttonWidth = screenViewport.width / 3;
        float buttonHeight = screenViewport.height/ 5;

        float soundButtonWidthAndHeight = screenViewport.right / 15;
        float soundButtonX = screenViewport.left + soundButtonWidthAndHeight / 2;
        float soundButtonY = screenViewport.top + soundButtonWidthAndHeight / 2;

        float playLoadButtonY = screenViewport.bottom - buttonHeight;
        float playButtonX = screenViewport.left + buttonWidth / 4 * 3;
        float loadButtonX = screenViewport.right - buttonWidth / 4 * 3;

        soundButton = new ScreenViewportReleaseButton(soundButtonX, soundButtonY,
                soundButtonWidthAndHeight, soundButtonWidthAndHeight, null, null, this);
        playButton = new ScreenViewportReleaseButton(playButtonX, playLoadButtonY, buttonWidth,
                buttonHeight, "playNow", null, this);
        loadGameButton = new ScreenViewportReleaseButton(loadButtonX, playLoadButtonY, buttonWidth,
                buttonHeight, "loadBt", null, this);
    }

    /**
     * @author Courtney Shek
     *
     * Starts a new game for the user
     */
    private void startNewGame()
    {
        cleanScreen();
        mGame.getScreenManager().removeScreen(this.getName());
        ScreenManager screenManager = mGame.getScreenManager();
        LanyonLevel0 lanyonLevel0 = new LanyonLevel0(mGame);
        screenManager.addScreen(lanyonLevel0);
        screenManager.setAsCurrentScreen("Lanyon Level");
    }

    /**
     * @author Jodie Burnside, seperated to method during refactoring by Courtney Shek
     *
     * Loads game for the user, if the load slot is empty then the tutorial level will be loaded
     * and a new game will be started
     */
    private void loadGame()
    {
        mGame.loadData(); //Load data within the save data slot (shared prefs used).
        if(mGame.getPlayerProfile().getCurrentLevel() == 0)
        {
            startNewGame();  //if fail to find a save game, start a new game.
            Log.d("LOAD GAME ", "FAILED - NO SAVE FOUND");
        }
        else //if there was a save in prefs, resume game where player save left off
        {
            cleanScreen();
            mGame.getScreenManager().removeScreen(this.getName());
            MapScreen mapScreen = new MapScreen(mGame);
            mGame.getScreenManager().addScreen(mapScreen);
            mGame.getScreenManager().setAsCurrentScreen("Map Screen");
        }
    }

    /**
     * @author Jodie Burnside, seperated to method during refactoring by Courtney Shek
     *
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
     * @author Courtney Shek and Jodie Burnside
     *
     * Sets up rectangles used on current screen
     */
    public void setUpRects()
    {
        backgroundArea = new Rect(screenViewport.left, screenViewport.top, screenViewport.right,
                screenViewport.bottom);
    }

    /**
     * @author Courtney Shek and Jodie Burnside
     *
     * Changes the users sound option from on/off
     */
    private void setSound()
    {
        if (mGame.getPlayerProfile().getSoundEnabled())
        {
            backgroundMusic.setLopping(false);
            backgroundMusic.pause();
            mGame.getPlayerProfile().setSoundEnabled(false);
        }
        else if (mGame.getPlayerProfile().getSoundEnabled() == false)
        {
            backgroundMusic.setLopping(true);
            backgroundMusic.play();
            mGame.getPlayerProfile().setSoundEnabled(true);
        }
    }
}