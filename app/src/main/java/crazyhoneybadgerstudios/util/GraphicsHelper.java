package crazyhoneybadgerstudios.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

public final class GraphicsHelper {
	private final String TAG = this.getClass().getSimpleName();
	
	// /////////////////////////////////////////////////////////////////////////
	// Source and Desintation Rects
	// /////////////////////////////////////////////////////////////////////////
		
	/** 
	 * Determine the full source bitmap Rect and destination screen Rect if the
	 * specified game object bound falls within the layer's viewport.
	 * 
	 * The return rects are not clipped against the screen viewport.
	 * 
	 * @param gameObject
	 *            Game object instance to be considered
	 * @param layerViewport
	 *            Layer viewport region to check the entity against
	 * @param screenViewport
	 *            Screen viewport region that will be used to draw the
	 * @param sourceRect
	 *            Output Rect holding the region of the bitmap to draw
	 * @param screenRect
	 *            Output Rect holding the region of the screen to draw to
	 * @return boolean true if the entity is visible, false otherwise
	 */
	public static final boolean getSourceAndScreenRect(GameObject gameObject,
													   LayerViewport layerViewport, ScreenViewport screenViewport,
													   Rect sourceRect, Rect screenRect) {

		// Get the bounding box for the specified sprite
		BoundingBox spriteBound = gameObject.getBound();

		// Determine if the sprite falls within the layer viewport
		if (spriteBound.x - spriteBound.halfWidth < layerViewport.x + layerViewport.halfWidth && 
			spriteBound.x + spriteBound.halfWidth > layerViewport.x - layerViewport.halfWidth && 
			spriteBound.y - spriteBound.halfHeight < layerViewport.y + layerViewport.halfHeight && 
			spriteBound.y + spriteBound.halfHeight > layerViewport.y - layerViewport.halfHeight) {

			// Define the source rectangle
			Bitmap spriteBitmap = gameObject.getBitmap();
			sourceRect.set(0, 0, spriteBitmap.getWidth(), spriteBitmap.getHeight());

			// Determine the x- and y-aspect rations between the layer and screen viewports
			float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
			float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

			// Determine the screen rectangle
			float screenX = screenViewport.left + screenXScale * 					
							((spriteBound.x - spriteBound.halfWidth) 
									- (layerViewport.x - layerViewport.halfWidth));
			float screenY = screenViewport.top + screenYScale * 
							((layerViewport.y + layerViewport.halfHeight) 
									- (spriteBound.y + spriteBound.halfHeight));

			screenRect.set((int) screenX, (int) screenY,
					(int) (screenX + (spriteBound.halfWidth * 2) * screenXScale),
					(int) (screenY + (spriteBound.halfHeight * 2) * screenYScale));

			return true;
		}

		// Not visible
		return false;
	}

    /**
	 * @author Chloe McMullan
     * Checks if a square falls within the screen view port, and if it does, updates the screen
     * rect to contain the correct position.
     * @param x left border of square to check
     * @param y top of square to check
     * @param width width of square
     * @param height height of square
     * @param layerViewport layer viewport to check your entity against
     * @param screenViewport screen viewport region that will be used to draw the rect
     * @param screenRect
     *            Output Rect holding the region of the screen to draw to
     * @return boolean true if the entity is visible, false otherwise
     */
	public static final boolean xyWithinClippedBounds(float x, float y, int width, int height,
													  LayerViewport layerViewport,
                                                      ScreenViewport screenViewport,
                                                      Rect screenRect) {

        // Determine if the sprite falls within the layer viewport
        if (x - width/2 < layerViewport.x + layerViewport.halfWidth &&
                x + width/2 > layerViewport.x - layerViewport.halfWidth &&
                y - height/2 < layerViewport.y + layerViewport.halfHeight &&
                y + height/2 > layerViewport.y - layerViewport.halfHeight) {


            // Determine the x- and y-aspect rations between the layer and screen viewports
            float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
            float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

            // Determine the screen rectangle
            float screenX = screenViewport.left + screenXScale *
                    ((x - width/2)
                            - (layerViewport.x - layerViewport.halfWidth));
            float screenY = screenViewport.top + screenYScale *
                    ((layerViewport.y + layerViewport.halfHeight)
                            - (y + height/2));

            screenRect.set((int) screenX, (int) screenY,
                    (int) (screenX + (width) * screenXScale),
                    (int) (screenY + (height) * screenYScale));

            return true;
        }

        // Not visible
        return false;
    }
	
	
	/** 
	 * Determine a source bitmap Rect and destination screen Rect if the
	 * specified game object bound falls within the layer's viewport.
	 * 
	 * The returned Rects are clipped against the layer and screen viewport
	 * 
	 * @param gameObject
	 *            Game object instance to be considered
	 * @param layerViewport
	 *            Layer viewport region to check the entity against
	 * @param screenViewport
	 *            Screen viewport region that will be used to draw the
	 * @param sourceRect
	 *            Output Rect holding the region of the bitmap to draw
	 * @param screenRect
	 *            Output Rect holding the region of the screen to draw to
	 * @return boolean true if the entity is visible, false otherwise
	 */
	public static final boolean getClippedSourceAndScreenRect(GameObject gameObject,
															  LayerViewport layerViewport, ScreenViewport screenViewport,
															  Rect sourceRect, Rect screenRect) {

		// Get the bounding box for the specified sprite
		BoundingBox spriteBound = gameObject.getBound();

		// Determine if the sprite falls within the layer viewport
		if (spriteBound.x - spriteBound.halfWidth < layerViewport.x + layerViewport.halfWidth && 
			spriteBound.x + spriteBound.halfWidth > layerViewport.x - layerViewport.halfWidth && 
			spriteBound.y - spriteBound.halfHeight < layerViewport.y + layerViewport.halfHeight && 
			spriteBound.y + spriteBound.halfHeight > layerViewport.y - layerViewport.halfHeight) {

			// Work out what region of the sprite is visible within the layer viewport,

			float sourceX = Math.max(0.0f,
					(layerViewport.x - layerViewport.halfWidth)
							- (spriteBound.x - spriteBound.halfWidth));
			float sourceY = Math.max(0.0f,
					(spriteBound.y + spriteBound.halfHeight)
							- (layerViewport.y + layerViewport.halfHeight));

			float sourceWidth = ((spriteBound.halfWidth * 2 - sourceX) - Math
					.max(0.0f, (spriteBound.x + spriteBound.halfWidth)
							- (layerViewport.x + layerViewport.halfWidth)));
			float sourceHeight = ((spriteBound.halfHeight * 2 - sourceY) - Math
					.max(0.0f, (layerViewport.y - layerViewport.halfHeight)
							- (spriteBound.y - spriteBound.halfHeight)));

			// Determining the scale factor for mapping the bitmap onto this
			// Rect and set the sourceRect value.

			Bitmap spriteBitmap = gameObject.getBitmap();
			
			float sourceScaleWidth = (float) spriteBitmap.getWidth()
					/ (2 * spriteBound.halfWidth);
			float sourceScaleHeight = (float) spriteBitmap.getHeight()
					/ (2 * spriteBound.halfHeight);

			sourceRect.set((int) (sourceX * sourceScaleWidth),
					(int) (sourceY * sourceScaleHeight),
					(int) ((sourceX + sourceWidth) * sourceScaleWidth),
					(int) ((sourceY + sourceHeight) * sourceScaleHeight));

			// Determine =which region of the screen viewport (relative to the
			// canvas) we will be drawing to.

			// Determine the x- and y-aspect rations between the layer and screen viewports
			float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
			float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

			float screenX = screenViewport.left + screenXScale * Math.max(
							0.0f,
							((spriteBound.x - spriteBound.halfWidth) 
									- (layerViewport.x - layerViewport.halfWidth)));
			float screenY = screenViewport.top + screenYScale * Math.max(
							0.0f,
							((layerViewport.y + layerViewport.halfHeight) 
									- (spriteBound.y + spriteBound.halfHeight)));

			// Set the region to the canvas to which we will draw
			screenRect.set((int) screenX, (int) screenY,
					(int) (screenX + sourceWidth * screenXScale),
					(int) (screenY + sourceHeight * screenYScale));

			return true;
		}

		// Not visible
		return false;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Aspect Ratios
	// /////////////////////////////////////////////////////////////////////////
			
	/**
	 * Create a 3:2 aspect ratio screen viewport
	 * 
	 * @param game Game view for which the screenport will be defined
	 * @param screenViewport Screen viewport to be defined
	 */
	public static void create3To2AspectRatioScreenViewport(
			Game game, ScreenViewport screenViewport) {
		
		// Create the screen viewport, size it to provide a 3:2 aspect
		float aspectRatio = (float) game.getScreenWidth() / (float) game.getScreenHeight();
		
		if (aspectRatio > 1.5f) { // 16:10/16:9
			int viewWidth = (int) (game.getScreenHeight() * 1.5f);
			int viewOffset = (game.getScreenWidth() - viewWidth) / 2;
			screenViewport.set(viewOffset, 0, viewOffset + viewWidth, game.getScreenHeight());
		} else { // 4:3
			int viewHeight = (int) (game.getScreenWidth() / 1.5f);
			int viewOffset = (game.getScreenHeight() - viewHeight) / 2;
			screenViewport.set(0, viewOffset, game.getScreenWidth(), viewOffset + viewHeight);
		}
	}


	/**
	 * MERGE BITMAP ARRAY INTO ONE BITMAP
	 * @author Dariusz Jerzewski
	 * @param bitmaps -bitmaps to process
	 * @param rects - store x,y for each bitmap and its width,height with corresponding index
     * @return
     */
	public static Bitmap mergeBitmaps(Bitmap[] bitmaps, Rect[] rects, Paint paint){
		if(bitmaps.length != rects.length)
		{
			return null; //not equal therefore cannot perform merging operation
		}

		Bitmap tempBitmap = null; //temporary bitmap to store changes

		int width = 0, height = 0;

		//first, determine height and width of the bitmap to be outputted
		for(int i = 0; i < rects.length; i++){
			if(rects[i].right - rects[i].left > width) {
				width = rects[i].right - rects[i].left;
			}
			if(rects[i].bottom  - rects[i].top > height) {
				height = rects[i].bottom  - rects[i].top;
			}
		}

		tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // create an empty bitmap

		Canvas imageMerger = new Canvas(tempBitmap); // canvas that we will use to write the images with

		//merge bitmaps
		for(int i = 0; i < bitmaps.length; i++){
			imageMerger.drawBitmap(bitmaps[i],null, rects[i], paint);
		}


		return tempBitmap;
	}


	/**
	 * @author Chloe McMullan
	 * Creates an array of images using the provided bitmap
	 * @param images bitmap with the images
	 * @param imageWidth width of each image
	 * @param imageHeight height of each image
	 * @param numImages number of images
	 * @return a bitmap containing each individual image
	 */
	public static Bitmap[] createArrayOfImages(Bitmap images, int imageWidth, int imageHeight, int numImages) {
		int numInRow = images.getWidth() / imageWidth;
		int numInColumn = images.getHeight() / imageHeight;

		Bitmap[] arrOfBitmaps = new Bitmap[numImages];
		int count = 0;

		// goes along bitmap and allocates imagewidth*imageheight space for ea. bitmap in arrOfBitmaps
		for (int i = 0; i < numInColumn && count < numImages; i++) {
			for (int j = 0; j < numInRow && count < numImages; j++) {
				//i*imageheight and j*imagewidth finds the top y and left x
				arrOfBitmaps[count] = Bitmap.createBitmap(images, j*imageWidth, i*imageHeight,
						imageWidth, imageHeight);
				count++;
			}
		}
		return arrOfBitmaps;
	}



	/**
	 * @author Chloe McMullan
	 * Returns the width of the specified text when using the paint provided
	 * @param text the string to measure
	 * @param textPaint the paint to apply to it (can be null)
	 * @return width of the text in pixels
	 */
	public static float getTextWidth(String text, Paint textPaint) {
		if (textPaint == null) textPaint = new Paint();
		Rect textBounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), textBounds);
		return textBounds.left + textBounds.width();
	}



	/**
	 * @author Chloe McMullan
	 * Works out the maximum size text can be in given dimensions
	 * @param maxHeight the max height of the text
	 * @param text the text to measure
	 * @param maxWidth the max width of the text
	 * @param differential how much to reduce by each time (i.e. accuracy of this height)
	 * @return the size to make the paint the text should be in
	 */
	public static float workOutBestTextSize(float maxHeight, String text, int maxWidth, float differential) {
		if (maxHeight <= 0) return 0;
		Paint paint = new Paint();

		paint.setTextSize(maxHeight); Rect measureText = new Rect();
		paint.getTextBounds(text, 0, text.length(), measureText);

		if (measureText.height() <= maxHeight && measureText.width() < maxWidth) return maxHeight;
		return workOutBestTextSize(maxHeight - differential, text, maxWidth, differential);
	}
}