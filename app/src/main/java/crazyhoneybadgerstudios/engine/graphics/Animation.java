package crazyhoneybadgerstudios.engine.graphics;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.Vector2;

/**
 * Basic animator
 * @author Chloe McMullan
 */

public class Animation {

    //All times are in seconds

    private int nextImage; //next image for the animator to draw
    private double timeAtLastAnimation; // time, in seconds, at last animation
    private double timeBetweenAnimations; // time, in seconds, between animations
    private Bitmap[]arrOfImages; //Each image stored in an array
    private boolean foreverLoop;
    private int numImages;
    private float lastX; //Where the last animation was performed
    private float lastY;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// CONSTRUCTORS ////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Basic constructor
     * @param images bitmap with all the images
     * @param numImages number of images on the bitmap
     * @param imageWidth width of each image
     * @param imageHeight height of each individual image
     * @param timeBetweenAnimations time in seconds between each animation ( 1 = 1 second)
     * @param foreverLoop boolean to know if the animation should loop or repeat the last image
     */
    public Animation(Bitmap images, int numImages, int imageWidth,
                     int imageHeight, double timeBetweenAnimations, boolean foreverLoop) {
        nextImage = 0;
        this.foreverLoop = foreverLoop;
        this.timeBetweenAnimations = timeBetweenAnimations;
        arrOfImages = GraphicsHelper.createArrayOfImages(images, imageWidth, imageHeight, numImages);
        this.timeAtLastAnimation = -1.;
        this.numImages = numImages;
        lastX = 0; lastY = 0;
    }

    /**
     * Allows for the entry of a constant x/y for it to be permanently drawn there
     * @param images bitmap with all the images
     * @param numImages number of images on the bitmap
     * @param imageWidth width of each image
     * @param imageHeight height of each individual image
     * @param timeBetweenAnimations time in seconds between each animation ( 1 = 1 second)
     * @param foreverLoop boolean to know if the animation should loop or repeat the last image
     * @param lastX where to draw, x co-ord
     * @param lastY where to draw, y co-ord
     */
    public Animation(Bitmap images, int numImages, int imageWidth, int imageHeight,
                     double timeBetweenAnimations, boolean foreverLoop, float lastX, float lastY) {
        nextImage = 0;
        this.foreverLoop = foreverLoop;
        this.timeBetweenAnimations = timeBetweenAnimations;
        arrOfImages = GraphicsHelper.createArrayOfImages(images, imageWidth, imageHeight, numImages);
        this.timeAtLastAnimation = -1.;
        this.numImages = numImages;
        this.lastX = lastX; this.lastY = lastY;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////// DRAWS AND UPDATES ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if it's time to draw, and if it is the image is drawn using x and y float
     * @param currentTime time at method calling
     * @param x x co-ord to draw to if enough time has elapsed
     * @param y y co-ord to draw to if enough time has elapsed
     * @param iGraphics2D canvas to draw to
     * @return true if the image has changed, false if the image hasn't changed
     */
    public boolean checkForAnimations(double currentTime, float x, float y, IGraphics2D iGraphics2D) {
        draw(x, y, iGraphics2D);
        return isUpdated(currentTime);
    }

    /**
     * Checks if it's time to draw, and if it is the image is drawn using vector
     * @param currentTime time at method calling
     * @param drawTo vector containing x and y co-ords to draw if enough time has elapsed
     * @param iGraphics2D canvas to draw to
     * @return true if the image has changed, false if the image hasn't changed
     */
    public boolean checkForAnimations(double currentTime, Vector2 drawTo, IGraphics2D iGraphics2D) {
        draw(drawTo.x, drawTo.y, iGraphics2D);
        return isUpdated(currentTime);
    }

    /**
     * Method to draw to the last position or the default position entered into constructor
     * @param currentTime time at method calling
     * @param iGraphics2D canvas to draw to
     * @return true if the image has changed, false if the image hasn't changed
     */
    public boolean checkForAnimations(double currentTime, IGraphics2D iGraphics2D) {
        draw(lastX, lastY, iGraphics2D);
        return isUpdated(currentTime);
    }

    /**
     * Updates lastX and lastY to relevant co-ords, and draws next image
     * @param x x to draw to and update to lastX
     * @param y y to draw to and update to lastY
     * @param iGraphics2D canvas to draw to
     */
    private void draw(float x, float y, IGraphics2D iGraphics2D) {
        lastX = x; lastY = y;
        iGraphics2D.drawBitmap(arrOfImages[nextImage], lastX, lastY, null);
    }

    /**
     * Checks the time of the last animation to see if the next image should be drawn
     * @param currentTime time of method calling
     * @return true = next image has been changed, false = next image is the same image as before
     */
    private boolean isUpdated(double currentTime) {
        if (timeAtLastAnimation == -1. || currentTime - timeAtLastAnimation >= timeBetweenAnimations) {
            timeAtLastAnimation = currentTime;
            nextImage = (nextImage < numImages - 1 && !foreverLoop) ? nextImage++ : 0;
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////// GETTERS / SETTERS //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Gets the number of the next image to be drawn
     * @return number of next image to be drawn
     */
    public int getNextImage() {
        return nextImage;
    }

    /**
     * Sets the next image to be drawn. Can allow skipping if desired.
     * @param nextImage
     */
    public void setNextImage(int nextImage) {
        if (nextImage < numImages && nextImage >= 0) this.nextImage = nextImage;
    }

    /**
     * Returns the time between each animation
     * @return the time betweene each animation, in seconds
     */
    public double getTimeBetweenAnimations() {
        return timeBetweenAnimations;
    }


    /**
     * Allows you to set the time between animations for varying speeds
     * @param timeBetweenAnimations the new time between animations, in seconds
     */
    public void setTimeBetweenAnimations(double timeBetweenAnimations) {
        if (timeBetweenAnimations > 0) this.timeBetweenAnimations = timeBetweenAnimations;
    }

    /**
     * If this animation will forever loop, or stop on the last image
     * @return true = forever loop, false = stop on last image
     */
    public boolean isForeverLoop() {
        return foreverLoop;
    }

    /**
     * Allows you to change if this animation will stop on the last image or not
     * @param foreverLoop false = stop on last image, true = forever loop
     */
    public void setForeverLoop(boolean foreverLoop) {
        this.foreverLoop = foreverLoop;
    }

    /**
     * Gets the x co-ord of the last animation
     * @return the x co-ord of the last animation
     */
    public float getLastX() {
        return lastX;
    }

    /**
     * Sets the x co-ord of the last animation
     * @param lastX
     */
    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    /**
     * Gets the y co-ord of the last animation
     * @return the y co-ord of the last animation
     */
    public float getLastY() {
        return lastY;
    }


    /**
     * Set the y co-ord of the last animation
     * @param lastY where you want the y co-ord of the last animation to be
     */
    public void setLastY(float lastY) {
        this.lastY = lastY;
    }
}
