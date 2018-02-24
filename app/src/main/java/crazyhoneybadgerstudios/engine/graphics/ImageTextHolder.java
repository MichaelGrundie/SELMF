package crazyhoneybadgerstudios.engine.graphics;

import android.graphics.Paint;

import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * Interface to allow for constant text and images to be stored in the same arraylist
 * @author Chloe McMullan
 */

public interface ImageTextHolder {

    /**
     * method to draw text/image
     * @param iGraphics2D canvas to draw to
     */
    void draw(IGraphics2D iGraphics2D);

    /**
     * Allows drawing to layer view port
     * @param iGraphics2D canvas to draw to
     * @param screenViewport screen view port
     * @param layerViewport layer view port
     */
    void drawToLayerViewPort(IGraphics2D iGraphics2D, ScreenViewport screenViewport, LayerViewport layerViewport);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// ACCESSOR/MUTATORS FOR PAINTS /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Paint getPaint();

    void setPaint(Paint paint);

}
