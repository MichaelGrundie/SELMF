package crazyhoneybadgerstudios.engine.graphics;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * Allows for the creation, storage and drawing of multiple types of images
 * through one class
 * @author Chloe McMullan
 */

public class ImageHolder implements ImageTextHolder {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// INSTANCE VARIABLES ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // allows draw to know which type of draw to peform on canvas
    private enum TypeOfDraw{RECT_PAINT, MATRIX_PAINT, POSITION_PAINT};
    private Paint paint;
    private float x, y;
    private Bitmap bitmap;
    private Matrix matrix;
    private Rect rectSrc, rectDest;
    private TypeOfDraw type;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// CONSTRUCTORS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor for drawing images through the use of Rects and paints
     * @param bitmap bitmap to draw
     * @param src source rect
     * @param dest destination rect
     * @param paint paint to apply to bitmap
     */
    public ImageHolder(Bitmap bitmap, Rect src, Rect dest, Paint paint) {
        this.bitmap = bitmap;
        this.rectSrc = src;
        this.rectDest = dest;
        this.paint = paint;
        this.type = TypeOfDraw.RECT_PAINT;

    }

    /**
     * Constructor for drawing images through matrices and paint
     * @param bitmap bitmap to draw
     * @param matrix matrix to apply to bitmap
     * @param paint paint to apply to bitmap
     */
    public ImageHolder(Bitmap bitmap, Matrix matrix, Paint paint) {
        this.bitmap = bitmap;
        this.matrix = matrix;
        this.paint = paint;
        this.type = TypeOfDraw.MATRIX_PAINT;
    }

    /**
     * Constructor for drawing images through an x y co-ord, and a paint
     * @param bitmap bitmap to draw
     * @param x x co-ord
     * @param y y co-ord
     * @param paint paint to apply to bitmap
     */
    public ImageHolder(Bitmap bitmap, float x, float y, Paint paint) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.paint = paint;
        this.type = TypeOfDraw.POSITION_PAINT;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// DRAW METHODS ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to draw. Decides via the TypeOfDraw what draw method to use
     * @param ig2d Canvas to draw to
     */
    public void draw(IGraphics2D ig2d) {
        switch(type) {
            case RECT_PAINT: ig2d.drawBitmap(bitmap, rectSrc, rectDest, paint);
                break;
            case MATRIX_PAINT: ig2d.drawBitmap(bitmap, matrix, paint);
                break;
            case POSITION_PAINT: ig2d.drawBitmap(bitmap, x, y, paint);
                break;
        }
    }

    /**
     * draws to the layer view port
     * @param iGraphics2D canvas to draw to
     * @param screenViewport screen view port
     * @param layerViewport layer view port
     */
    @Override
    public void drawToLayerViewPort(IGraphics2D iGraphics2D, ScreenViewport screenViewport,
                                    LayerViewport layerViewport) {
        switch (type) {
            // checks if it should be clipped, and clips if required
            case RECT_PAINT:
                if (GraphicsHelper.xyWithinClippedBounds(rectDest.left, rectDest.top, rectDest.width(),
                        rectDest.height(), layerViewport, screenViewport, rectDest))
                    iGraphics2D.drawBitmap(bitmap, rectSrc, rectDest, paint);
                break;

            // very inaccurate. Maps matrix onto a rectF, which is rounded into a rect. Then clips and
            // draws
            case MATRIX_PAINT: RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                matrix.mapRect(rectF);
                rectDest = new Rect();
                rectF.round(rectDest);
                if (GraphicsHelper.xyWithinClippedBounds(rectF.left, rectF.top,
                        bitmap.getWidth(), bitmap.getHeight(), layerViewport, screenViewport,
                        rectDest)) {
                    iGraphics2D.drawBitmap(bitmap, matrix, paint);
                }
                break;

            // Creates a new rect for the drawing, then clips and draws
            case POSITION_PAINT: if (rectDest == null) rectDest = new Rect();
                if (GraphicsHelper.xyWithinClippedBounds(x, y,
                    bitmap.getWidth(), bitmap.getHeight(), layerViewport,
                    screenViewport, rectDest))
                    iGraphics2D.drawBitmap(bitmap, rectSrc, rectDest, paint);
                break;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// GENERAL ACCESSORS AND MUTATORS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Rect getRectSrc() {
        return rectSrc;
    }

    public void setRectSrc(Rect rectSrc) {
        this.rectSrc = rectSrc;
    }

    public Rect getRectDest() {
        return rectDest;
    }

    public void setRectDest(Rect rectDest) {
        this.rectDest = rectDest;
    }

    public TypeOfDraw getType() {
        return type;
    }

    public void setType(TypeOfDraw type) {
        this.type = type;
    }
}
