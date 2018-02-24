package crazyhoneybadgerstudios.engine.graphics;

import android.graphics.Paint;
import android.graphics.Rect;

import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * Allows for multiple ways of drawing text to be stored as the same object
 * @author Chloe McMullan
 */

public class TextHolder implements ImageTextHolder {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// INSTANCE VARIABLES ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private enum TypeOfText{POSITION_PAINT, RECT};
    private Paint textPaint; //has accessor/mutator - can be null if desired
    private float x,y;
    private String text;
    private TypeOfText tot;
    private Rect rect, srcRect;
    private Paint rectPaint; //can be null if desired

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// CONSTRUCTORS ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Allows for drawing of text using x,y co-ords and paint
     * @param text text to draw
     * @param x top left corner
     * @param y top left corner
     * @param paint paint to apply to this
     */
    public TextHolder(String text, float x, float y, Paint paint) {
        this.text = text;
        this.x = x; this.y = y;
        this.textPaint = paint;
        tot = TypeOfText.POSITION_PAINT;
    }

    /**
     * Allows drawing of an image through the creation of a rect, with paints applied to text/rect
     * @param text text to draw
     * @param left x co-ord of left of rect
     * @param top y co-ord of top of rect
     * @param right x co-ord of right of rect
     * @param bottom y co-ord of bottom of rect
     * @param textPaint paint to apply to the text
     * @param rectPaint paint to apply to the rect
     */
    public TextHolder(String text, int left, int top, int right, int bottom, Paint textPaint,
                      Paint rectPaint) {
        this.text = text;
        rect = new Rect(left, top, right, bottom);
        this.rectPaint = rectPaint;
        this.textPaint = textPaint;
        tot = TypeOfText.RECT;
    }

    /**
     * Allows drawing of text by passing in an already made rect, with text paint and rect paint
     * @param text text to draw
     * @param rect rect to draw text in
     * @param textPaint paint to apply to the text
     * @param rectPaint paint to apply to the rect
     */
    public TextHolder(String text, Rect rect, Paint textPaint, Paint rectPaint) {
        this.text = text;
        this.rect = rect;
        this.textPaint = textPaint;
        this.rectPaint = rectPaint;
        tot = TypeOfText.RECT;
    }

    /**
     * Constructor for creating a padded rect for around the text
     * @param text text to draw
     * @param leftX x co-ord of the left border of the rect
     * @param topY y co-ord of the top border of the rect
     * @param xPadding padding to be applied to both left and right
     * @param yPadding padding to be applied to both the top and bottom
     * @param textPaint paint applied to text
     */
    public TextHolder(String text, int leftX, int topY, Paint rectPaint, Paint textPaint,
                      int xPadding, int yPadding) {
        this.text = text;
        this.x = leftX;
        this.y = topY;
        this.rectPaint = rectPaint;
        this.textPaint = textPaint;
        tot = TypeOfText.RECT;
        this.rect = new Rect();
        createPaddedRect(rect, xPadding, yPadding);
    }

    /**
     * Creates the padded rect
     * @param rect rect to make padded
     * @param xPadding x padding at either side of the rect
     * @param yPadding y padding at both top and bottom of the rect
     */
    private void createPaddedRect(Rect rect, int xPadding, int yPadding) {
        if (textPaint == null) textPaint = new Paint();

        Rect textBounds = new Rect();
        // finds the size of the text that will be added
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        // sets the rect to start at the specified xy, with padding added to the bottom/right
        // text will be centered in this later
        rect.set((int)x, (int)y, (int)x + textBounds.width() + 2*xPadding,
                (int)y + textBounds.height() + 2*yPadding);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// DRAWING METHODS ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called to draw the text to the canvas.
     * In the case of Rects, the text is auto-centered to an appropriate x value
     * @param ig2d canvas to draw the text to
     */
    public void draw(IGraphics2D ig2d) {
        switch (tot) {
            case POSITION_PAINT: ig2d.drawText(text, x, y, textPaint);
                break;
            case RECT: drawTextCenteredInRect(ig2d);
                break;
        }
    }

    /**
     * Allows you to draw text to the layer view port, and clips what isn't in view
     * @param ig2d canvas to draw to
     * @param screenViewport screen view port, for conversion
     * @param layerViewport layer viewport to draw to
     */
    public void drawToLayerViewPort(IGraphics2D ig2d, ScreenViewport screenViewport,
                                    LayerViewport layerViewport) {
        if (srcRect == null) srcRect = new Rect();
        switch(tot) {
            // clips it to match what is in view
            case POSITION_PAINT: if (GraphicsHelper.xyWithinClippedBounds(x, y,
                    (int)textPaint.measureText(text), (int)textPaint.getTextSize(), layerViewport,
                    screenViewport, srcRect)) drawTextCenteredInRect(ig2d);
                break;
            case RECT: if (GraphicsHelper.xyWithinClippedBounds(rect.exactCenterX(),
                    rect.exactCenterY(), (int)textPaint.measureText(text), (int)textPaint.getTextSize(), layerViewport,
                    screenViewport, srcRect))
                drawTextCenteredInRect(ig2d);
                break;
        }
    }

    /**
     * Centres the text within a rect, and draws both the rect and the text
     * @param ig2d canvas to draw to
     */
    private void drawTextCenteredInRect(IGraphics2D ig2d) {
        if (textPaint == null) textPaint = new Paint();
        Rect centredRect = new Rect();

        // gets size of text
        textPaint.getTextBounds(text, 0, text.length(), centredRect);
        if (rectPaint == null) rectPaint = new Paint();

        // draws in exactcentrex - half text width, exact y - half text size
        ig2d.drawText(text, rect.exactCenterX() - centredRect.width()/2,
                rect.exactCenterY() - centredRect.height()/2, textPaint);

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// GENERAL ACCESSORS/MUTATORS //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Allows you to change the padded text so that the rect will still be the correct size
     * @param text
     */
    public void setPaddedText(String text) {
        Rect tempRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), tempRect);

        int xPadding = (rect.width() - tempRect.width()) / 2;
        int yPadding = (rect.height() - tempRect.height()) / 2;

        this.text = text;
        createPaddedRect(rect, xPadding, yPadding);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// GENERAL ACCESSORS AND MUTATORS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Paint getPaint() {
        return textPaint;
    }

    public void setPaint(Paint paint) {
        this.textPaint = paint;
    }

    public String getText() { return this.text; }

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

    public void setText(String text) {
        this.text = text;
    }

    public TypeOfText getTot() {
        return tot;
    }

    public void setTot(TypeOfText tot) {
        this.tot = tot;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Paint getRectPaint() {
        return rectPaint;
    }

    public void setRectPaint(Paint rectPaint) {
        this.rectPaint = rectPaint;
    }


}
