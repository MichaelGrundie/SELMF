package crazyhoneybadgerstudios.ui;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.util.InputHelper;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie.
 *
 */

public class LayerViewportReleaseButton extends ReleaseButton {

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
    public LayerViewportReleaseButton(float x, float y, float width, float height,
                         String defaultBitmap,
                         String pushBitmap,
                         String releaseSound,
                         String text,
                         GameScreen gameScreen) {
        super(x, y, width, height,
                defaultBitmap,pushBitmap,releaseSound, text, gameScreen);

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
    public LayerViewportReleaseButton(float x, float y, float width, float height,
                         String bitmap, String text, GameScreen gameScreen) {
        super(x, y, width, height, bitmap, text, gameScreen);

    }


    /**
     * Updates touches made to the button by first converting them to layer viewport space.
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime) {
        // Consider any touch events occurring in this update
        Input input = mGameScreen.getGame().getInput();
        BattleScreen battleScreen = (BattleScreen)mGameScreen;
        ScreenViewport screenViewport = battleScreen.getScreenViewPort();
        LayerViewport layerViewport = battleScreen.getLayerViewport();



        // Check for a press release on this button
        for (TouchEvent touchEvent : input.getTouchEvents())
        {
            Vector2 v = new Vector2(touchEvent.x, touchEvent.y); //vector containing layer co-ordinates
            InputHelper.convertScreenPosIntoLayer(screenViewport, touchEvent.x, touchEvent.y, layerViewport,
                    v);
            super.update(elapsedTime, touchEvent, v, input);
        }

        checkForRelease();

    }


    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

    }
}
