package crazyhoneybadgerstudios.game.Game_Elements;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.CanvasGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.ui.LayerViewportReleaseButton;
import crazyhoneybadgerstudios.ui.ReleaseButton;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie.
 */
public class PopUp extends GameObject {

    public enum SIZE {
        SMALL, MEDIUM, LARGE
    }

    private final String TAG = this.getClass().getSimpleName();
    private String message;
    private ReleaseButton button1, button2;
    private SIZE size;
    private int numberOfButtons;
    public boolean positivePush, negativePush;
    private float buttonWidth, buttonHeight;
    private String button1Text, button2Text, button1Bitmap, button2Bitmap;
    private GameScreen gameScreen;
    private LayerViewport layerViewport;

    /**
     * The constructor decides whether it should make a pop-up for the screen viewport or layer
     * viewport. It does this by checking that if layer viewport is null.
     *
     * Future developments would implement the option of screen viewport/ layer viewport using extensions
     * or constructor overloading.
     *
     * @param layerViewport
     * @param gameScreen
     * @param bitmap
     * @param button1Bitmap
     * @param button2Bitmap
     * @param size
     * @param button1Text
     * @param button2Text
     * @param message
     * @param noOfButtons
     */
    public PopUp(LayerViewport layerViewport, GameScreen gameScreen, String bitmap,String button1Bitmap,String button2Bitmap, SIZE size, String button1Text,
                 String button2Text, String message, int noOfButtons)
    {
        super(gameScreen);
        this.size = size;
        this.message = message;
        this.gameScreen = gameScreen;
        this.button1Bitmap = button1Bitmap;
        this.button2Bitmap = button2Bitmap;
        this.button1Text = button1Text;
        this.button2Text = button2Text;

        if (noOfButtons <= 1) numberOfButtons = 1;
        else numberOfButtons = 2;

        positivePush = negativePush = false;

        if(layerViewport != null) setSizeAndPosition(layerViewport);
        else setSizeAndPosition(gameScreen.getScreenViewPort());

        mBitmap = makeBitmap(gameScreen.getGame().getAssetManager().getBitmap(bitmap), message);

        buttonWidth = mBound.getWidth() / 5;
        buttonHeight = mBound.getHeight() / 5;

        if(layerViewport!=null) makeLayerButtons();
        else makeScreenButtons();
    }



    /**
     * Creates a one button pop-up
     * @param layerViewport
     * @param gameScreen
     * @param bitmap
     * @param buttonBitmap
     * @param size
     * @param button
     * @param message
     */
    public PopUp(LayerViewport layerViewport, GameScreen gameScreen, String bitmap,String buttonBitmap, SIZE size,
                 String button, String message)
    {
        this(layerViewport,gameScreen,bitmap,buttonBitmap,null,size,button,null,message,1);
    }



    private void makeLayerButtons()
    {
        if(numberOfButtons == 2)
        {
            this.button1 = new LayerViewportReleaseButton(mBound.x - buttonWidth,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button1Bitmap, button1Text, gameScreen);

            this.button2 = new LayerViewportReleaseButton(mBound.x + buttonWidth,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button2Bitmap, button2Text, gameScreen);
        }
        else
        {
            this.button1 = new LayerViewportReleaseButton(mBound.x,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button1Bitmap, button1Text, gameScreen);
        }
    }



    private void makeScreenButtons()
    {
        if(numberOfButtons == 2)
        {
            this.button1 = new ScreenViewportReleaseButton(mBound.x - buttonWidth,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button1Bitmap, button1Text, gameScreen);

            this.button2 = new ScreenViewportReleaseButton(mBound.x + buttonWidth,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button2Bitmap, button2Text, gameScreen);
        }
        else
        {
            this.button1 = new ScreenViewportReleaseButton(mBound.x,
                    mBound.getBottom() + buttonHeight / 2 + 5,
                    buttonWidth, buttonHeight, button1Bitmap, button1Text, gameScreen);
        }
    }



    /**
     * Sizes the pop-up for a screen viewport
     * @param screenViewport
     */
    private void setSizeAndPosition(ScreenViewport screenViewport)
    {
        float areaWidth = screenViewport.width;
        float areHeight = screenViewport.height;
        doSizing(areaWidth,areHeight);
        setPosition(screenViewport.centerX(), screenViewport.centerY());
    }



    /**
     * Sized the pop-up for a layer viewport.
     * @param layerViewport
     */
    private void setSizeAndPosition(LayerViewport layerViewport)
    {
        float areaWidth = layerViewport.getWidth();
        float areHeight = layerViewport.getHeight();
        doSizing(areaWidth,areHeight);
        setPosition(layerViewport.x, layerViewport.y);
    }



    /**
     * Works out  the sizing based upon the size of the pop-up chosen.
     * @param areaWidth
     * @param areaHeight
     */
    private void doSizing(float areaWidth, float areaHeight)
    {
        float width, height;

        switch (size) {
            case SMALL:
                width = areaWidth / 3;
                height = areaHeight / 3;
                break;
            case MEDIUM:
                width = areaWidth / 2;
                height = areaHeight / 2;
                break;
            default:
                width = areaWidth - 10;
                height = areaHeight - 10;
                break;
        }


        mBound.halfWidth = width / 2;
        mBound.halfHeight = height / 2;
    }



    /**
     * Creates a bitmap of the passed in text and the passed in background bitmap.
     *
     * (This is a crude method of text rendering).
     *
     * @param bitmap
     * @param text
     * @return
     */
    private Bitmap makeBitmap(Bitmap bitmap, String text)
    {
        //To hold the bitmaps and their size/position
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<Rect> rects = new ArrayList<>();

        //Used to make the images larger to improve text quality.
        int scale = 5;

        //Text styling
        Paint textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.BLACK);

        //Bitmaps to be added.
        Bitmap popUpText = new CanvasGraphics2D(mGameScreen.getGame().getActivity().getAssets()).drawText(text, textPaint);
        bitmaps.add(bitmap);
        bitmaps.add(popUpText);

        //Size and position rects.
        Rect popUpRect = new Rect(0, 0, scale * (int) mBound.getWidth(), scale * (int) mBound.getHeight());
        Rect textRect = new Rect(scale * (int)mBound.getWidth()/8, scale * ((int) mBound.getHeight() / 4),
                (scale * (int) mBound.getWidth()) - (scale * (int)mBound.getWidth()/8), scale * (2* (int) mBound.getHeight() / 4));
        rects.add(popUpRect);
        rects.add(textRect);

        //The merged bitmap
        return GraphicsHelper.mergeBitmaps(bitmaps.toArray(new Bitmap[bitmaps.size()]),
                rects.toArray(new Rect[rects.size()]), new Paint());
    }



    /**
     * Updates the buttons.
     *
     * @param elapsedTime
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        button1.update(elapsedTime);

        if(numberOfButtons == 2)
        {
            button2.update(elapsedTime);

            if(button1.pushTriggered())
            {
                negativePush = true;
                Log.d(TAG, "update: Cancel ");
            }

            if (button2.pushTriggered()) {
                positivePush = true;
                Log.d(TAG, "update: Confirm ");
            }

        }else if(button1.pushTriggered())
        {
            positivePush = true;
            Log.d(TAG, "update: Confirm ");
        }
    }



    /**
     * Draws the pop up to the screen or layer.
     *
     * Draws the button(s).
     *
     * @param elapsedTime
     *            Elapsed time information
     * @param graphics2D
     *            Graphics instance
     * @param layerViewport
     *            Game layer viewport
     * @param screenViewport
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport) {
        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

        button1.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        if (numberOfButtons == 2) {
            button2.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }
    }
}
