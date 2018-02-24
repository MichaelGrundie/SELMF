package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Jodie Burnside - 4/16/2017.
 */public class HelperBook extends GameScreen
{
    public enum Section //Section of the book that is currently open
    {
        STARTCHOICE, UNIMON, BATTLECHOICE, BATTLETUTORIAL, BATTLETUTORIALCONTINUED, END;
    }

    private Game mGame;
    private ScreenManager screenManager;
    private Section sectionOpen; //Used to detect which page the book is open at (which page to draw).
    private String screenToResume = "";
    private boolean howToPlay; //True if the howToPlay/tutorial section of the book is to be displayed

    //Rects to draw bitmaps
    private Rect backgroundPages;
    private Rect resumeBt;
    private Rect howToPlayBt;
    private Rect yesBt;
    private Rect noBt;

    public HelperBook(Game game, String screenToResumeName)
    {
        super("Helper Book", game);
        sectionOpen = Section.STARTCHOICE; //Default books current section/chapter to the start
        mGame = game;
        screenManager = game.getScreenManager();
        screenToResume = screenToResumeName;

        setUpScreenViewPort();
       // AssetLoading.loadHelperBook(game.getAssetManager());
        setRects();
    }

    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();
        for(TouchEvent event : input.getTouchEvents())
        {
            if(event.type == TouchEvent.TOUCH_DOWN)
            {
                int x = (int) event.x;
                int y = (int) event.y;

                if (howToPlay)
                {
                    tutorialTouchHandler(x, y);
                }
                else //if not on tutorial/howtoplay, the user is - the main menu of helper book
                {
                    mainMenuTouchHandlers(x, y);
                }
            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D)
    {
        if (howToPlay)
        {
            drawOpenTutorialSection(graphics2D); //Draw the open sections pages to the background rect

            if (sectionOpen == Section.STARTCHOICE || sectionOpen == Section.BATTLECHOICE) // If section open is a choice section, draw the yes/no choice buttons
            {
                graphics2D.drawBitmap(AssetLoading.yesButton, null, yesBt, null);
                graphics2D.drawBitmap(AssetLoading.noButton, null, noBt, null);
            }
        }
        else //If not on howToPlay section, the user is on the main section, so draw the main section
        {
            graphics2D.drawBitmap(AssetLoading.bookBackgroundBlank, null, backgroundPages, null);
            graphics2D.drawBitmap(AssetLoading.resume, null, resumeBt, null);
            graphics2D.drawBitmap(AssetLoading.howToPlayTutorial, null, howToPlayBt, null);
        }

    }

    //HELPER BOOK METHODS BELOW:

    //Method draws the appropriate tutorial page that is to be displayed, indicated by the sectionOpen variable.
    //@params Igraphics2D graphics2D
    //@author Jodie Burnside (401500309)
    private void drawOpenTutorialSection(IGraphics2D graphics2D)
    {
        if (sectionOpen == Section.STARTCHOICE)
        {
            graphics2D.drawBitmap(AssetLoading.startChoice, null, backgroundPages, null);
        }
        else if (sectionOpen == Section.UNIMON)
        {
            graphics2D.drawBitmap(AssetLoading.unimon, null, backgroundPages, null);
        }
        else if (sectionOpen == Section.BATTLECHOICE)
        {
            graphics2D.drawBitmap(AssetLoading.battleChoice, null, backgroundPages, null);
        }
        else if (sectionOpen == Section.BATTLETUTORIAL)
        {
            graphics2D.drawBitmap(AssetLoading.battleTutorial, null, backgroundPages, null);
        }
        else if (sectionOpen == Section.BATTLETUTORIALCONTINUED)
        {
            graphics2D.drawBitmap(AssetLoading.battleTutorialContinued, null, backgroundPages, null);
        }
        else if (sectionOpen == Section.END)
        {
            graphics2D.drawBitmap(AssetLoading.end, null, backgroundPages, null);
        }
    }

    //Method assigns rect positions
    //@author Jodie Burnside (401500309)
    private void setRects()
    {
        backgroundPages = new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom); //cover entire screen
        yesBt = new Rect((screenViewport.left/6 + 250), screenViewport.top/6 + 750, screenViewport.right/6 + 250, screenViewport.bottom/6 + 750);
        noBt = new Rect((screenViewport.left/6 + 500), screenViewport.top/6 + 750, screenViewport.right/6 + 500, screenViewport.bottom/6 + 750);
        resumeBt = new Rect((screenViewport.left/4) + 300, screenViewport.top/4 + 200, screenViewport.right/4 + 350, screenViewport.bottom/4 + 150);
        howToPlayBt = new Rect((screenViewport.left/4) + 300, screenViewport.top/4 + 400, screenViewport.right/4 + 350, screenViewport.bottom/4 + 350);
    }

    //Method to detect and react appropriately to touch event on the 'main menu' book page
    //@params int x, int y
    //@author Jodie Burnside (401500309)
    private void mainMenuTouchHandlers(int x, int y)
    {
        if (resumeBt.contains(x, y) && !howToPlay)
        {
            resumeGame();
        }
        else if (howToPlayBt.contains(x, y) && !howToPlay)
        {
            howToPlay = true;
        }
    }

    //Method to detect and react appropriately to touch event on the 'tutorial' book pages. Detects the open section to determine appropriate response.
    //@params int x, int y
    //@author Jodie Burnside (401500309)
    private void tutorialTouchHandler(int x, int y)
    {
        if (backgroundPages.contains(x, y) && !(sectionOpen == Section.STARTCHOICE || sectionOpen == Section.BATTLECHOICE))
        {

            if(sectionOpen == HelperBook.Section.END)
            {
                howToPlay = false; //If clicked to proceed to next page on the final page, close tutorial and return to book main menu
            }
            else if (sectionOpen == Section.UNIMON)
            {
                sectionOpen = Section.BATTLECHOICE;                    }
            else if(sectionOpen == Section.BATTLETUTORIAL) //If on the non-choice screen battle tutorial or battle tutorial continued, proceed to next page. If not on these pages,
            //the use is on a 'choice page', so take no action on clicking proceed.
            {
                sectionOpen = Section.BATTLETUTORIALCONTINUED;
            }
            else if(sectionOpen == Section.BATTLETUTORIALCONTINUED)
            {
                sectionOpen = Section.END;
            }
        }
        else if (yesBt.contains(x, y))
        {
            if (sectionOpen == Section.STARTCHOICE) //If the click to the 'yes' (i know what unimon are) button was on the start choice screen, skip the unimon section.
            {
                sectionOpen = Section.BATTLECHOICE;
            }
            else if (sectionOpen == Section.BATTLECHOICE) //If yes clicked, player knows how a battle works, skip battle tutorial to end.
            {
                sectionOpen = HelperBook.Section.END;

            }
        }
        else if (noBt.contains(x, y))
        {
            if (sectionOpen == Section.STARTCHOICE) //No, player doesn't know what unimon are, so progress to unimon
            {
                sectionOpen = Section.UNIMON;
            }
            else if (sectionOpen == Section.BATTLECHOICE) //Don't know how battle works, display battle tutorial
            {
                sectionOpen = Section.BATTLETUTORIAL;
            }
        }
    }


     //Method removes the Helper Book screen and resumes the Battle Screen
     //@author Jodie Burnside (401500309)
    private void resumeGame()
    {
        screenManager.removeScreen(this.getName());
        screenManager.setAsCurrentScreen(screenToResume);
    }

}
