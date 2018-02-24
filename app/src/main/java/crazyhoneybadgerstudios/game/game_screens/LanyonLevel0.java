package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Jodie Burnside - 1/22/2017.
 */

public class LanyonLevel0 extends GameScreen
{
    private int bannerCounter; //Count number of text banners displayed

    private Rect backgroundArea;
    private Rect oldManArea;
    private Rect tutorialTextBannerArea;

    public LanyonLevel0(Game game)
    {
        super("Lanyon Level", game);

        bannerCounter = 0;
        setUpScreenViewPort();

        //AssetLoading.loadTutorial(game.getAssetManager());
        setUpMusic("Background Music", "audio/casualSceneMusic.mp3");
        setUpRects();
    }

    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();

        for (TouchEvent event : input.getTouchEvents())
        {
            if (event.type == TouchEvent.TOUCH_DOWN)
            {
                if (tutorialTextBannerArea.contains((int) event.x, (int) event.y))
                {
                    if (bannerCounter < AssetLoading.tutorialTextBanners.length - 1) //if banner is not the last in the tutorial, increase counter for next banner draw
                   {
                        bannerCounter++;
                       Log.d("TUTORIAL ", "BANNER INCREMENTED");
                   }
                   else //if the text banner is the last in the tutorial, proceed to level map
                   {
                        cleanScreen();
                        mGame.getPlayerProfile().setCurrentLevel(1);
                        mGame.getScreenManager().removeScreen(this.getName());
                        CharacterCreation charScreen = new CharacterCreation(mGame);
                        mGame.getScreenManager().addScreen(charScreen);
                        mGame.getScreenManager().setAsCurrentScreen("Character Creation");

//                        BattleLoadingScreen bls = new BattleLoadingScreen(mGame,1);
//                        mGame.getScreenManager().addScreen(bls);
//                        mGame.getScreenManager().setAsCurrentScreen("bls");
//
//                        TileMapScreen tileMapScreen = new TileMapScreen(mGame);
//                        mGame.getScreenManager().addScreen(tileMapScreen);
//                        mGame.getScreenManager().setAsCurrentScreen("TileMapScreen");


                 }
                }
            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D iGraphics2d)
    {
        iGraphics2d.drawBitmap(AssetLoading.tutorialBackground, null, backgroundArea, null);
        iGraphics2d.drawBitmap(AssetLoading.speakerOldMan, null, oldManArea, null);

        //Draw the text banner that is next to be displayed. (banner counter starts at 0, so first will be drawn when tutorial level is first opened.
        // If banner is clicked, the bannerCounter is incremented, so the next is drawn (until the length of the banner array is reached).
        iGraphics2d.drawBitmap(AssetLoading.tutorialTextBanners[bannerCounter], null, tutorialTextBannerArea, null);
    }

    /**
     * Sets up rectangles used on the current screen
     */
    public void setUpRects()
    {
        backgroundArea = new Rect(screenViewport.left, screenViewport.top, screenViewport.right,
                screenViewport.bottom);
        oldManArea = new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom);
        tutorialTextBannerArea = new Rect(screenViewport.left, screenViewport.bottom - screenViewport.height /3 - 50, screenViewport.right - 200, screenViewport.bottom);
    }

}