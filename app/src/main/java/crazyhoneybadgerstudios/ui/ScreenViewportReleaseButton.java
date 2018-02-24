package crazyhoneybadgerstudios.ui;

import android.graphics.Color;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie.
 */

public class ScreenViewportReleaseButton extends ReleaseButton {

    private final String TAG = this.getClass().getSimpleName();

    public ScreenViewportReleaseButton(float x, float y, float width, float height,
                                       String defaultBitmap,
                                       String pushBitmap,
                                       String releaseSound,
                                       String text,
                                       GameScreen gameScreen) {
        super(x, y, width, height,
                defaultBitmap, pushBitmap,releaseSound,text, gameScreen);

    }

    public ScreenViewportReleaseButton(float x, float y, float width, float height,
                                      String bitmap, String text,
                                      GameScreen gameScreen) {
        super(x, y, width, height, bitmap, text, gameScreen);

    }

    /**
     * Updates touches on the button using the raw touch even co-ordinates (screen space).
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime) {
        // Consider any touch events occurring in this update
        Input input = mGameScreen.getGame().getInput();


        // Check for a press release on this button
        for (TouchEvent touchEvent : input.getTouchEvents())
        {
            Vector2 v = new Vector2(touchEvent.x, touchEvent.y); //vector containing layer co-ordinates
            super.update(elapsedTime, touchEvent, v, input);
        }

        checkForRelease();

    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, ScreenViewport screenViewport)
    {
        super.draw(elapsedTime,graphics2D,null,screenViewport);
    }
}
