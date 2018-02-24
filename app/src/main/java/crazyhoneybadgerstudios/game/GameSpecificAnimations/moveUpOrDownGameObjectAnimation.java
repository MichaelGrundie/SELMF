package crazyhoneybadgerstudios.game.GameSpecificAnimations;

import android.graphics.Bitmap;
import android.graphics.Paint;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.animation.IAnimation;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Michael Grundie.
 *
 * Moves 4 bitmaps (as objects) Up/Down a game object changing their alpha value to give them
 * a fade in/out look.
 */
public class moveUpOrDownGameObjectAnimation implements IAnimation {

    public enum Direction
    {
        UP,DOWN
    }

    private GameObject []gameObjects;
    private Paint []paints;
    private Direction direction;
    private GameObject affectedObject;
    private boolean completed;

    public moveUpOrDownGameObjectAnimation(GameObject affectedObject, String bitmap, Direction direction, GameScreen gameScreen)
    {
        this(affectedObject, gameScreen.getGame().getAssetManager().getBitmap(bitmap),  direction,  gameScreen);
    }

    public moveUpOrDownGameObjectAnimation(GameObject affectedObject, Bitmap bitmap, Direction direction, GameScreen gameScreen)
    {
        this.affectedObject = affectedObject;
        completed = false;
        this.direction = direction;
        gameObjects = new GameObject[4];
        paints = new Paint[4];
        for (int i=0;i<4;i++)
        {
            paints[i] = new Paint();
            paints[i].setAlpha(255);
        }

        createMovingObjects(bitmap, gameScreen);

    }

    /**
     * Creates and positions the objects that will move up/down the affected object.
     * @param bitmap
     * @param gameScreen
     */
    private void createMovingObjects(Bitmap bitmap, GameScreen gameScreen)
    {

        float widthAndHeight = affectedObject.getBound().getWidth()/6;

        for(int i=0; i<3; i++)
        {
            float x = affectedObject.getBound().getLeft() + ((i+1) * (affectedObject.getBound().getWidth()/4));
            float y;

            switch (i)
            {
                case 0 : y = direction == Direction.DOWN ? affectedObject.getBound().getTop() + widthAndHeight
                        : affectedObject.getBound().getBottom() - widthAndHeight;
                    break;
                case 1 : y = direction == Direction.DOWN ? affectedObject.getBound().getTop()
                        : affectedObject.getBound().getBottom();
                    break;
                default: y = direction == Direction.DOWN ? affectedObject.getBound().getTop() + 2*widthAndHeight
                        : affectedObject.getBound().getBottom() - 2*widthAndHeight;
                    break;
            }

            gameObjects[i] = new GameObject(x,y,widthAndHeight,widthAndHeight,bitmap, gameScreen);
        }

        float x =affectedObject.getBound().getLeft() + (1.5f * affectedObject.getBound().getWidth()/4);
        float y;
        if(direction == Direction.DOWN)
        {
            y = affectedObject.getBound().getTop() + 3 * widthAndHeight;
            bitmap = gameScreen.getGame().getAssetManager().getBitmap("MINUS");
        }
        else
        {
            y = affectedObject.getBound().getBottom() - 3 * widthAndHeight;
            bitmap = gameScreen.getGame().getAssetManager().getBitmap("PLUS");
        }

        gameObjects[3] = new GameObject(x,y,widthAndHeight,widthAndHeight,bitmap, gameScreen);
    }


    public boolean isFinished()
    {
        return completed;
    }


    /**
     * Moves the moving objects on each loop and changes alpha.
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime)
    {

        for(int i=0;i<4;i++)
        {
            float x = gameObjects[i].getBound().x;
            float y = gameObjects[i].getBound().y;

            if(direction == Direction.DOWN)
            {
                y-= affectedObject.getBound().getHeight()/32;
                if(y <= affectedObject.getBound().getBottom() + affectedObject.getBound().getHeight()/4)
                {
                    int alpha  = paints[i].getAlpha() - 20;
                    if (alpha<0) alpha = 0;
                    paints[i].setAlpha(alpha);
                    if(gameObjects[3].getBound().y <= affectedObject.getBound().getBottom()) completed =true;
                }

            }
            else
            {
                y+= affectedObject.getBound().getHeight()/32;
                if(y >= affectedObject.getBound().getTop() - affectedObject.getBound().getHeight()/4)
                {
                    int alpha  = paints[i].getAlpha() - 20;
                    if (alpha<0) alpha = 0;
                    paints[i].setAlpha(alpha);
                    if(gameObjects[3].getBound().y >= affectedObject.getBound().getTop()) completed =true;
                }
            }

            gameObjects[i].setPosition(x,y);


        }
    }


    /**
     * Draws the moving game objects.
     *
     * @param elapsedTime
     * @param graphics2D
     * @param layerViewport
     * @param screenViewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport)
    {
        for(int i =0;i<4;i++)
        {
            if(affectedObject.getBound().contains(gameObjects[i].getBound().x, gameObjects[i].getBound().y))
            {

                gameObjects[i].draw(elapsedTime,graphics2D,layerViewport,screenViewport,paints[i]);
            }
        }
    }

    public GameObject getParent(){
        return null;
    }
}



