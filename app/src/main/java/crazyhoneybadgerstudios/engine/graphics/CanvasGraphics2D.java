package crazyhoneybadgerstudios.engine.graphics;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Graphics2D class that provides basic draw functionality for a canvas.
 * 
 * @version 1.0
 */
public class CanvasGraphics2D implements IGraphics2D {

	// /////////////////////////////////////////////////////////////////////////
	// Methods: Properties
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Canvas onto which this graphics instance will render
	 */
	private Canvas mCanvas;

	/**
	 * Height and width of the canvas
	 */
	private int mWidth;
	private int mHeight;

	/**
	 * Asset manager
	 */
	private AssetManager mAssetManager;

	// /////////////////////////////////////////////////////////////////////////
	// Methods: Constructors
	// /////////////////////////////////////////////////////////////////////////

	public CanvasGraphics2D(AssetManager assets) {
		this.mAssetManager = assets;
	}

	// /////////////////////////////////////////////////////////////////////////
	// Methods: Draw
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Set the canvas onto which this graphics instance can render
	 * 
	 * @param canvas
	 */
	public void setCanvas(Canvas canvas) {
		mCanvas = canvas;
		mWidth = canvas.getWidth();
		mHeight = canvas.getHeight();
		mCanvas.drawRGB((000000 & 0xff0000) >> 16, (000000 & 0xff00) >> 8,
				(000000 & 0xff));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawBitmap(android.graphics
	 * .Bitmap, android.graphics.Rect, android.graphics.Rect,
	 * android.graphics.Paint)
	 */
	@Override
	public void drawBitmap(Bitmap bitmap, Rect srcRect, Rect desRect,
						   Paint paint) {
		mCanvas.drawBitmap(bitmap, srcRect, desRect, paint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawBitmap(android.graphics
	 * .Bitmap, android.graphics.Matrix, android.graphics.Paint)
	 */
	@Override
	public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
		mCanvas.drawBitmap(bitmap, matrix, paint);
	}

	/**
	 * @author ChloeMcMullan
	 * @param bitmap bitmap to draw
	 * @param x left x co-ord of the bitmap
	 * @param y top y co-ord of the bitmap
	 * @param paint paint to apply to this bitmap
	 */
    @Override
    public void drawBitmap(Bitmap bitmap, float x, float y, Paint paint) {
        mCanvas.drawBitmap(bitmap, x, y, paint);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#drawText(java.lang.String,
	 * float, float, android.graphics.Paint)
	 */
	@Override
	public void drawText(String text, float x, float y, Paint paint) {
		mCanvas.drawText(text, x, y, paint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#clear(int)
	 */
	@Override
	public void clear(int color) {
		mCanvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
				(color & 0xff));
	}

	// /////////////////////////////////////////////////////////////////////////
	// Methods: Configuration
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * @see uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D#clipRect(android.graphics.Rect)
	 */
	@Override
	public void clipRect(Rect clipRegion) {
		mCanvas.clipRect(clipRegion);
	}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#getSurfaceWidth()
	 */
	@Override
	public int getSurfaceWidth() {
		return mWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.eeecs.gage.interfaces.IGraphics2D#getSurfaceHeight()
	 */
	@Override
	public int getSurfaceHeight() {
		return mHeight;
	}


	/**
	 * @author Dariusz Jerzewski
	 * @param text to be drawn to bitmap
	 * @param paint used to draw the text
	 * @return
	 */
	public Bitmap drawText(String text, Paint paint) {
		float baseline = -paint.ascent(); // ascent() is negative
		int width = (int) (paint.measureText(text)); // round
		int height = (int) (baseline + paint.descent());

		//create empty bitmap
		Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		//treat bitmap as canvas for the text
		Canvas canvas = new Canvas(image);
		canvas.drawText(text, 0, baseline, paint);
		return  image;
	}

	/**
	 * @author Courtney Shek
	 *
	 * Draws the specified rectangle
	 *
	 * @param r - The Rectangle to be rendered
	 * @param paint - Paint parameters controlling how the Rectangle is rendered
	 * @return The rectangle to be rendered
	 */
	@Override
	public Rect drawRect(Rect r, Paint paint) {
		mCanvas.drawRect(r, paint);
		return r;
	}
}
