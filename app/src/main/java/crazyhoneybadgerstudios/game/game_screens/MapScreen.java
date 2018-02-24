package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.world.GameScreen;


public class MapScreen extends GameScreen {


    private Rect level1Rect;
    private Rect level2Rect;
    private Rect level3Rect;
    private Rect backgroundPath;
    private Rect popUpRect;
    private boolean suPoppedUp;
    private boolean botanicPoppedUp;
    private boolean csbPoppedUp;



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// CONSTRUCTORS /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public MapScreen(Game game)
    {
        super("Map Screen", game);
        AssetStore assetManager = mGame.getAssetManager();

        setUpScreenViewPort();
        //AssetLoading.loadMapScreen(game.getAssetManager());
        setUpRects();

        suPoppedUp = false; //Used to detect if the info boxes are popped up (they pop up when level dot is clicked). default false;
        botanicPoppedUp = false;
        csbPoppedUp = false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// UTILITY METHODS ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Jodie Burnside
     *
     * Detects if a level that has been clicked on is locked - the level number is not
     * higher than the players current level.
     *
     * @param levelClicked
     * @return
     */
    private boolean levelLocked(int levelClicked)
    {
        if (mGame.getPlayerProfile().getCurrentLevel() < levelClicked )
            return true;
        else
            return false;

    }



    /**
     * @author Jodie Burnside
     * @author Courtney Shek
     *
     * Loads a battle screen level, of the appropriate level number.
     *
     * @param level
     */
    private void loadLevel(int level)
    {
        ScreenManager screenManager = mGame.getScreenManager();
        screenManager.removeScreen(this.getName());
        cleanScreen();

        EndlessScreen battleLoadingScreen = new EndlessScreen(mGame);
        screenManager.addScreen(battleLoadingScreen);
        screenManager.setAsCurrentScreen("Endless Screen");
    }



    /**
     * @author Chloe McMullan
     *
     * Draws a dashed 'path' from level to level on the map.
     *
     * @param drawFrom rect to draw from
     * @param drawTo rect to draw to
     * @param iG2D canvas to draw to
     */
    private void drawPath(Rect drawFrom, Rect drawTo, IGraphics2D iG2D)
    {
        float xDistance = drawTo.exactCenterX() - drawFrom.exactCenterX();
        float yDistance = drawTo.exactCenterY() - drawFrom.exactCenterY();
        float hypotenuseDistance = (float) Math.hypot(xDistance, yDistance);

        // number of rects = screen viewport width/20
        // -1 means the rects won't overlap the original dot
        int numberOfRects = (int)(hypotenuseDistance/(screenViewport.width/20)) - 1;
        float width = xDistance / numberOfRects;
        float height = yDistance / numberOfRects;

        for (int i = 1; i < numberOfRects; i++)
        {
            iG2D.drawBitmap(AssetLoading.redDash, null, new Rect( (int)(drawFrom.exactCenterX() + i*width - 10),
                    (int)(drawFrom.exactCenterY() + i*height - 10), (int)(drawFrom.exactCenterX() + i*width + 10),
                    (int)(drawFrom.exactCenterY() + i*height + 10)), null);
        }
    }



    /**
     * @author v.1 Jodie Burnside.
     * @author v.2 Michael Grundie, rescalablity to screen sizes
     *
     * Assigns rect positions.
     */
    public void setUpRects()
    {
        int widthAndHeight = screenViewport.height/10;

        level1Rect = new Rect(screenViewport.left + screenViewport.width/3,
                0,
                screenViewport.left + screenViewport.width/3 + widthAndHeight,
                widthAndHeight);

        level2Rect = new Rect(screenViewport.right - screenViewport.width/3,
                screenViewport.bottom - screenViewport.height/3 - widthAndHeight,
                screenViewport.right - screenViewport.width/3 + widthAndHeight,
                screenViewport.bottom - screenViewport.height/3);

        level3Rect = new Rect(screenViewport.left + screenViewport.width/3,
                screenViewport.bottom - screenViewport.height/4,
                screenViewport.left + screenViewport.width/3 + widthAndHeight,
                screenViewport.bottom - screenViewport.height/4 + widthAndHeight);

        popUpRect = new Rect(screenViewport.left + screenViewport.width/20,
                screenViewport.top + screenViewport.width/20,
                screenViewport.left + screenViewport.width/20 + screenViewport.width/3,
                screenViewport.top + screenViewport.width/20 + screenViewport.width/3);

        backgroundPath = new Rect(screenViewport.left,
                screenViewport.top,
                screenViewport.right,
                screenViewport.bottom);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// UPDATE /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @author v1.1 Jodie Burnside.
     * @author v1.2 Michael Grundie. -Small change to allow only one pop up at a time
     * @param elapsedTime
     */
    @Override
    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();

        for(TouchEvent event : input.getTouchEvents())
        {
            if(event.type == TouchEvent.SINGLE_TAP_CONFIRMED)
            {
                if (level1Rect.contains((int) event.x, (int) event.y))
                {
                    suPoppedUp = true; //clicking on the dot opens menu (bool is true), clicking again closes it.
                    botanicPoppedUp=csbPoppedUp = false;
                }
                else if (level2Rect.contains((int) event.x, (int) event.y))
                {
                    botanicPoppedUp = true;
                    suPoppedUp = csbPoppedUp = false;
                }
                else if (level3Rect.contains((int) event.x, (int) event.y))
                {
                    csbPoppedUp = true;
                    suPoppedUp = botanicPoppedUp = false;
                }
                else if (popUpRect.contains((int) event.x, (int) event.y)) //if the pop up rect is clicked, and one of the pop up's is drawn to the rect, open the relevant battle level
                {
                    if (suPoppedUp)
                    {
                        loadLevel(1);
                    } else if (botanicPoppedUp && !levelLocked(2))
                    {
                        loadLevel(2);
                    } else if (csbPoppedUp && !levelLocked(3))
                    {
                        loadLevel(3);
                    }
                    //If no level info is popped up, the touch does nothing.
                }else csbPoppedUp = botanicPoppedUp = suPoppedUp =false;
            }

        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// DRAW ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D iGraphics2d)
    {

        iGraphics2d.drawBitmap(AssetLoading.backgroundMap, null, backgroundPath, null); //mGame.getPlayerProfile().getCurrentLevel()
        iGraphics2d.drawBitmap(AssetLoading.suLevelDot, null, level1Rect, null);
        iGraphics2d.drawBitmap(AssetLoading.botanicLevelDot, null, level2Rect, null);
        iGraphics2d.drawBitmap(AssetLoading.csbLevelDot, null, level3Rect, null);


        if (suPoppedUp) //if pop up menu is set to true (set to be drawn), draw the menu.
        {
            iGraphics2d.drawBitmap(AssetLoading.suPopUp, null, popUpRect, null);
            drawPath(level1Rect, level2Rect, iGraphics2d);
        }
        else if (botanicPoppedUp)
        {
            if (levelLocked(2)) { //if level is locked (not yet at this level), present the locked image
                iGraphics2d.drawBitmap(AssetLoading.botanicPopUpLOCKED, null, popUpRect, null);

            }
            else {
                iGraphics2d.drawBitmap(AssetLoading.botanicPopUp, null, popUpRect, null); //if not locked, present the unlocked image
                drawPath(level2Rect, level3Rect, iGraphics2d);
            }

        }
        else if (csbPoppedUp)
        {
            if (levelLocked(3))
                iGraphics2d.drawBitmap(AssetLoading.csbPopUpLOCKED, null, popUpRect, null);
            else
                iGraphics2d.drawBitmap(AssetLoading.csbPopUp, null, popUpRect, null);

        }
    }
}