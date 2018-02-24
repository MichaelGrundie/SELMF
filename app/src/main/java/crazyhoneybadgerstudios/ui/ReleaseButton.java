package crazyhoneybadgerstudios.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import java.util.ArrayList;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.audio.Sound;
import crazyhoneybadgerstudios.engine.graphics.CanvasGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie
 *
 * Modified and extend this class to suit our needs. It's extensions allow a release button to be
 * created in world space or screen space. The main difference is in the scaling of the touch input.
 *
 * Also text can be rendered as bitmap to the button (Though this is not a great solution due to
 * text deformation/stretching).
 */
public class ReleaseButton extends GameObject {

    ///////////////////////////////////////////////////////////////////////////
    // Class data: PushButton look and sound                                 //
    ///////////////////////////////////////////////////////////////////////////

    private final String TAG = this.getClass().getSimpleName();
    /**
     * Name of the graphical asset used to represent the default button state
     */
    protected Bitmap mDefaultBitmap;

    /**
     * Name of the graphical asset used to represent the pushed button state
     */
    protected Bitmap mPushBitmap;

    /**
     * Name of the sound asset to be played whenever the button is clicked
     */
    protected Sound mReleaseSound;

    protected float pushHeightChangeVal;
    protected float pushWidthChangeVal;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors                                                          //
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Create a new release button.
     *
     * @param x             Centre y location of the button
     * @param y             Centre x location of the button
     * @param width         Width of the button
     * @param height        Height of the button
     * @param defaultBitmap Bitmap used to represent this control
     * @param pushBitmap    Bitmap used to represent this control
     * @param releaseSound  Bitmap used to represent this control
     * @param gameScreen    Gamescreen to which this control belongs
     */
    public ReleaseButton(float x, float y, float width, float height,
                         String defaultBitmap,
                         String pushBitmap,
                         String releaseSound,
                         String text,
                         GameScreen gameScreen) {
        super(x, y, width, height,
                null, gameScreen);

        AssetStore assetStore = gameScreen.getGame().getAssetManager();

        if(text != null) mBitmap = mDefaultBitmap = makeBitmap(assetStore.getBitmap(defaultBitmap), text);

        else mBitmap =  mDefaultBitmap = assetStore.getBitmap(defaultBitmap);

        mPushBitmap = (pushBitmap == null) ? null :assetStore.getBitmap(pushBitmap);

        mReleaseSound = (releaseSound == null) ? null : assetStore.getSound(releaseSound);

        pushHeightChangeVal =height/10;
        pushWidthChangeVal =width/10;

    }


    /**
     * Create a new release button.
     *
     * @param x             Centre y location of the button
     * @param y             Centre x location of the button
     * @param width         Width of the button
     * @param height        Height of the button
     * @param gameScreen    Gamescreen to which this control belongs
     */
    public ReleaseButton(float x, float y, float width, float height,
                         String bitmap, String text, GameScreen gameScreen)
    {
        super(x, y, width, height, null, gameScreen);


        AssetStore assetStore = gameScreen.getGame().getAssetManager();
        if(text == null)
        {
            mBitmap = mDefaultBitmap = assetStore.getBitmap(bitmap);
        }
        else mBitmap = mDefaultBitmap = makeBitmap(assetStore.getBitmap(bitmap), text);
        pushHeightChangeVal =height/10;
        pushWidthChangeVal =width/10;


        mPushBitmap = null;
        mReleaseSound = null;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////


    protected boolean mPushTriggered;
    protected boolean mIsPushed;

    protected Bitmap makeBitmap(Bitmap bitmap, String text)
    {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<Rect> rects = new ArrayList<>();

        //Converting the text to bitmap
        Paint textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.WHITE);
        Bitmap buttonText =  new CanvasGraphics2D(mGameScreen.getGame().getActivity().getAssets()).drawText(text, textPaint);

        //Adding the bitmaps to be merged
        bitmaps.add(bitmap);
        bitmaps.add(buttonText);

        //To improve text and bitmap quality.
        int scale =10;
        
        //Sizing and position of the bitmaps relative to each other.
        Rect buttonRect = new Rect(0, 0, scale*(int)mBound.getWidth(), scale*(int) mBound.getHeight());
        Rect buttText = new Rect(5*scale, scale * ((int)mBound.getHeight()/3), (scale*(int)mBound.getWidth())-5*scale, scale * (2* (int)mBound.getHeight()/3));;

        rects.add(buttonRect);
        rects.add(buttText);

        return GraphicsHelper.mergeBitmaps(bitmaps.toArray(new Bitmap[bitmaps.size()]),
                rects.toArray(new Rect[rects.size()]), new Paint());
    }

    /**
     * Update the button
     *
     * @param elapsedTime Elapsed time information
     */
    public void update(ElapsedTime elapsedTime,TouchEvent touchEvent, Vector2 v, Input i) {


        // Check for a press release on this button

        if (touchEvent.type == TouchEvent.SINGLE_TAP_CONFIRMED
                && mBound.contains(v.x, v.y)) {
            // A touch up has occurred in this control
            mPushTriggered = true;
            if(mPushBitmap != null) mBitmap = mPushBitmap;
            Log.d(TAG, "update: button triggered. ");
            if(mReleaseSound != null && mGameScreen.getGame().getPlayerProfile().getSoundEnabled()) mReleaseSound.play();
            return;
        }

        if ((touchEvent.type == TouchEvent.TOUCH_DOWN || touchEvent.type == TouchEvent.TOUCH_DRAGGED) && mBound.contains(v.x, v.y)) {
            if (!mIsPushed) {
                mBound.halfWidth -= pushWidthChangeVal;
                mBound.halfHeight -= pushHeightChangeVal;
                mIsPushed = true;
                Log.d(TAG, "update: button pushed. ");
                if(mPushBitmap != null) mBitmap = mPushBitmap;
            }

            return;
        }

    }

    protected void checkForRelease()
    {
        Input input = mGameScreen.getGame().getInput();
        if (mIsPushed && !input.fingerStillOnScreen()) {
            mBitmap = mDefaultBitmap;
            mBound.halfWidth += pushWidthChangeVal;
            mBound.halfHeight += pushHeightChangeVal;
            mIsPushed = false;
            if(mPushBitmap != null) mBitmap = mDefaultBitmap;
        }
    }


    /**
     * Return true if the button has been triggered.
     * <p>
     * Note: This method will return true once, and only once, per push event.
     *
     * @return True if there has been an unconsumed push event, false otherwise.
     */
    public boolean pushTriggered() {
        if (mPushTriggered) {
            mPushTriggered = false;
            return true;
        }
        return false;
    }

    public void reset()
    {
        mPushTriggered = false;
    }

    /**
     * Return the current state of the button.
     *
     * @return True if the button is pushed, otherwise false.
     */
    public boolean isPushed() {
        return mIsPushed;
    }


    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);


    }

}
