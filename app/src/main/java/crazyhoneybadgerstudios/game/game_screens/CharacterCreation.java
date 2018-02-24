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
 * @author Jodie Burnside - 4/20/2017.
 */

public class CharacterCreation extends GameScreen
{
    private Game mGame;
    private int currentHeadNum; //Used to cycle through array of head/clothing options
    private int currentRobesNum;

    //All Rects
    private Rect backgroundRect;
    private Rect arrowLeftHead;
    private Rect arrowRightHead;
    private Rect arrowLeftRobes;
    private Rect arrowRightRobes;
    private Rect confirmBtRect;
    private Rect currentHead;
    private Rect currentRobes;

    public CharacterCreation(Game game)
    {
        super("Character Creation", game);
        currentHeadNum = 0; //Start at the beginning of the array of heads/robes
        currentRobesNum = 0;
        mGame = game;

        setUpScreenViewPort();
       // AssetLoading.loadCharacterCreation(game.getAssetManager());
        loadRects();
    }

    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();
        for (TouchEvent event : input.getTouchEvents())
        {
            if (event.type == TouchEvent.TOUCH_DOWN)
            {
                if (confirmBtRect.contains((int) event.x, (int) event.y))
                {
                    //Set users head/robes to the the currently displayed when confirm is pressed.
                    //When game is saved, the selected head/robe number is saved and can be used to access the array via heads[num] etc.
                    mGame.getPlayerProfile().setPlayerSelectedHead(currentHeadNum);
                    mGame.getPlayerProfile().setPlayerSelectedRobes(currentRobesNum);
                    Log.d("Character Creation ", "HEAD+ROBES SAVED");

                    //Load map screen to continue
                    mGame.getScreenManager().removeScreen(this.getName());
                    MapScreen mapScreen = new MapScreen(mGame);
                    mGame.getScreenManager().addScreen(mapScreen);
                    mGame.getScreenManager().setAsCurrentScreen("Map Screen");

                }
                else if (arrowLeftHead.contains((int) event.x, (int) event.y) &&  currentHeadNum > 0)
                {
                    currentHeadNum--;
                }
                else if (arrowRightHead.contains((int) event.x, (int) event.y) && currentHeadNum < AssetLoading.heads.length - 1)
                {
                    currentHeadNum++;
                }
                else if (arrowLeftRobes.contains((int) event.x, (int) event.y) && currentRobesNum > 0)
                {
                    currentRobesNum--;
                }
                else if (arrowRightRobes.contains((int) event.x, (int) event.y) && currentRobesNum < AssetLoading.robes.length - 1)
                {
                    currentRobesNum++;
                }
            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D)
    {
        //Draw all the constant features of the character creation page
        graphics2D.drawBitmap(AssetLoading.charCreationbackground, null, backgroundRect, null);
        graphics2D.drawBitmap(AssetLoading.confirmBt, null, confirmBtRect, null);
        drawAppropriateArrows(graphics2D);

        //Draw current head/robes
        graphics2D.drawBitmap(AssetLoading.robes[currentRobesNum], null, currentRobes, null);
        graphics2D.drawBitmap(AssetLoading.heads[currentHeadNum], null, currentHead, null);

    }

    //Method draws the appropriate arrows (e.g. the left arrow is not shown, it it is the first option (there is no option to the left).
    //@params Igraphics2D graphics2D
    //@author Jodie Burnside (401500309)
    private void drawAppropriateArrows(IGraphics2D graphics2D) //Draw appropriate selection arrows - If first options, only show right arrow etc.
    {
        if (currentHeadNum > 0 && currentHeadNum < AssetLoading.heads.length - 1) //If not the first or the last option, draw both selection arrows
        {
            graphics2D.drawBitmap(AssetLoading.arrowLeft, null, arrowLeftHead, null);
            graphics2D.drawBitmap(AssetLoading.arrowRight, null, arrowRightHead, null);
        }
        else if(currentHeadNum == 0) //If the first option, only draw the right selection arrow (there is no previous)
        {
            graphics2D.drawBitmap(AssetLoading.arrowRight, null, arrowRightHead, null);
        }
        else //if the last option, only draw left arrow (no further option to show)
        {
            graphics2D.drawBitmap(AssetLoading.arrowLeft, null, arrowLeftHead, null);
        }

        if (currentRobesNum > 0 && currentRobesNum < AssetLoading.robes.length - 1) //If not the first or the last option, draw both selection arrows
        {
            graphics2D.drawBitmap(AssetLoading.arrowLeft, null, arrowLeftRobes, null);
            graphics2D.drawBitmap(AssetLoading.arrowRight, null, arrowRightRobes, null);
        }
        else if(currentRobesNum == 0) //If the first option, only draw the right selection arrow (there is no previous)
        {
            graphics2D.drawBitmap(AssetLoading.arrowRight, null, arrowRightRobes, null);
        }
        else //if the last option, only draw left arrow (no further option to show)
        {
            graphics2D.drawBitmap(AssetLoading.arrowLeft, null, arrowLeftRobes, null);
        }
    }

    //Method assigns rect positions
    //@author Jodie Burnside (401500309) -  also refactored for scalability
    private void loadRects()
    {
        //Rects
        backgroundRect = new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom);

        currentHead = new Rect
                (screenViewport.width / 10 + screenViewport.width / 3,
                screenViewport.height / 3,
                screenViewport.width/3 +  screenViewport.width / 3,
                screenViewport.height - mGame.getScreenHeight() / 4);

        currentRobes = new Rect
                (screenViewport.width / 10 + screenViewport.width / 3,
                        screenViewport.height / 3,
                        screenViewport.width/3 +  screenViewport.width / 3,
                        screenViewport.height - mGame.getScreenHeight() / 4);

        arrowLeftHead = new Rect
                ((screenViewport.width / 10 + screenViewport.width / 3) - (screenViewport.width / 10) * 3,
                        screenViewport.height / 3,
                        (screenViewport.width/3 +  screenViewport.width / 3) - (screenViewport.width / 10) * 3,
                        screenViewport.height - mGame.getScreenHeight() / 2);

        arrowRightHead = new Rect(
                (screenViewport.width / 10 + screenViewport.width / 3) + (screenViewport.width / 10) * 3,
                screenViewport.height / 3,
                (screenViewport.width/3 +  screenViewport.width / 3) + (screenViewport.width / 10) * 3,
                screenViewport.height - mGame.getScreenHeight() / 2);

        arrowLeftRobes = new Rect
                ((screenViewport.width / 10 + screenViewport.width / 3) - (screenViewport.width / 10) * 3,
                        (screenViewport.height / 3)  + screenViewport.height / 4,
                        (screenViewport.width/3 +  screenViewport.width / 3) - (screenViewport.width / 10) * 3,
                        (screenViewport.height - mGame.getScreenHeight() / 2) + screenViewport.height / 4);

        arrowRightRobes = new Rect
                ((screenViewport.width / 10 + screenViewport.width / 3) + (screenViewport.width / 10) * 3,
                        (screenViewport.height / 3)  + screenViewport.height / 4,
                (screenViewport.width/3 +  screenViewport.width / 3) + (screenViewport.width / 10) * 3,
                        (screenViewport.height - mGame.getScreenHeight() / 2) + screenViewport.height / 4);

        confirmBtRect =  new Rect
                (screenViewport.width / 10 + screenViewport.width / 3 - (screenViewport.width / 15) ,
                        screenViewport.height - mGame.getScreenHeight() / 4 + screenViewport.height/10,
                        screenViewport.width/3 +  screenViewport.width / 3 + (screenViewport.width / 15),
                        screenViewport.height - mGame.getScreenHeight() / 4 + (screenViewport.height/10) * 2);

    }
}
