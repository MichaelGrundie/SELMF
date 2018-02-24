package crazyhoneybadgerstudios.game.Game_Elements;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.CanvasGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Courtney Shek
 *
 * This class handles the drawing out of the WitPoints bar for both the player and the AI
 * It keeps track of when WitPoints have been reduced and when WitPoints have reached 0 it will
 * end the battle
 */
public class WitPointsBar extends GameObject {

    private final String TAG = this.getClass().getSimpleName();


    private int witPoints;
    private int maxWitPoints;
    private Player player;
    private Bitmap text;
    private Bitmap red;
    private Bitmap green;



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////// CONSTRUCTORS //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Michael Grundie
     *
     * Creates a scaled witpoints bar for a screen viewport or layer viewport.
     */
    public WitPointsBar(float x, float y, float width, float height, GameScreen gameScreen, Player player, int maxWitPoints) {
        super(x, y,width,height, null, gameScreen);

        this.witPoints = this.maxWitPoints = maxWitPoints;
        this.player = player;
        makeBitmap();

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// BITMAP CREATION /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Michael Grudie
     *
     * Works out which percentage of the witpoints bar should be creen and creats the bitmap.
     */
    private void makeBitmap()
    {
        //Red bar fills the bar behind the green bar.
        Rect redBar = new Rect(0,0,(int)mBound.getWidth(),(int)mBound.getHeight());

        //Players health percentage is applied to the width of the green bar.
        //As it's width reduces it reveals the red bar.
        double percentageGreen = ((float)witPoints/(float)maxWitPoints) * mBound.getWidth();
        Rect greenBar = new Rect(0,0,(int)percentageGreen, (int)mBound.getHeight());

        //Create the bar bitmaps just once.
        if(red == null || green == null) makeBarBitmaps();
        if(text == null) makeTextBitmap();

        //Position the text.
        Rect txtRect = new Rect(0,0,text.getWidth(), text.getHeight());

        //Merge the bitmaps.
        Bitmap[] bitmaps = {red,green,text};
        Rect[] rects = {redBar,greenBar,txtRect};
        mBitmap = GraphicsHelper.mergeBitmaps(bitmaps,rects, new Paint());
    }



    /**
     * @author Michael Grundie
     *
     * Creates a red and green bar as a bitmap.
     */
    private void makeBarBitmaps()
    {
        //Create bit map for the green bard and the red bar.
        Bitmap.Config conf  = Bitmap.Config.ARGB_8888;
        red = Bitmap.createBitmap((int)mBound.getWidth(), (int)mBound.getHeight(), conf);
        green = Bitmap.createBitmap((int)mBound.getWidth(), (int)mBound.getHeight(), conf);

        //Color the pixels.
        for(int i = 0; i< red.getHeight(); i++)
        {
            for(int j=0; j<red.getWidth(); j++)
            {
                red.setPixel(j,i,Color.RED);
                green.setPixel(j,i,Color.GREEN);
            }
        }
    }



    /**
     * @author Michael Grundie
     *
     * Creates a bitmap of the witpoint bar text.
     */
    private void makeTextBitmap()
    {
        //The text's styling paint.
        Paint textPaint = new Paint();
        textPaint.setTextSize(mBound.getHeight());
        textPaint.setColor(Color.BLACK);

        //Set the text and make it into a bitmap.
        if(player instanceof AI)
        {
            text  = new CanvasGraphics2D(mGameScreen.getGame().getActivity().getAssets())
                    .drawText("Opponent's WitPoints", textPaint);
        }
        else
        {
            text  = new CanvasGraphics2D(mGameScreen.getGame().getActivity().getAssets())
                    .drawText("Your WitPoints", textPaint);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// UPDATE /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @author Courtney Shek.
     *
     * @param elapsedTime
     */
    @Override
    public void update(ElapsedTime elapsedTime)
    {
        if(player.getWitPoints() < witPoints)
        {
            witPoints = player.getWitPoints();
            makeBitmap();
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// DRAW ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport,
                     ScreenViewport screenViewport)
    {
        super.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
    }

}
