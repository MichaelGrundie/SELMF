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
 * @author Michael Grundie
 *
 * An animation that makes an image pop out from the centre of a game-object.
 *
 */
public class PopOutFromCentreOfGameObjectAnimation implements IAnimation
{
    private GameObject gameObject;
    private Paint paint;
    private GameObject affectedObject;
    private boolean completed;
    private boolean inflateComplete = false;

    public PopOutFromCentreOfGameObjectAnimation(GameObject affectedObject, Bitmap bitmap, GameScreen gameScreen)
    {
        this.affectedObject = affectedObject;
        completed = false;
        float widthAndHeight = affectedObject.getBound().getWidth()/2;

        gameObject = new GameObject(affectedObject.getBound().x,affectedObject.getBound().y,
                widthAndHeight, widthAndHeight, bitmap, gameScreen);

        paint = new Paint();
        paint.setAlpha(100);
    }

    public PopOutFromCentreOfGameObjectAnimation(GameObject affectedObject, String bitmap, GameScreen gameScreen)
    {
         this(affectedObject, gameScreen.getGame().getAssetManager().getBitmap(bitmap), gameScreen);
    }

    public boolean isFinished()
    {
        return completed;
    }

    /**
     * Progresses the image out and in from the affected object changing it's alpha value to
     * give it a fade in/out look.
     *
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime)
    {
        gameObject.position = affectedObject.position;

        if(gameObject.getBound().getWidth() < affectedObject.getBound().getWidth() && !inflateComplete)
        {
            int alpha = Math.min(255, paint.getAlpha()+20);
            paint.setAlpha(alpha);

            gameObject.getBound().halfHeight = gameObject.getBound().halfWidth += 5;

            if (gameObject.getBound().getWidth() >= affectedObject.getBound().getWidth())
            {
                gameObject.getBound().halfHeight =
                        gameObject.getBound().halfWidth = affectedObject.getBound().getWidth();

                inflateComplete = true;
            }
        }

        if (gameObject.getBound().getWidth() > 10 && inflateComplete)
        {
            int alpha = Math.max(0, paint.getAlpha()-10);
            paint.setAlpha(alpha);

            gameObject.getBound().halfHeight = gameObject.getBound().halfWidth -= 5;
        }
        else if(inflateComplete)
        {
            completed = true;
        }

    }


    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport)
    {
        if (gameObject != null) gameObject.draw(elapsedTime,graphics2D,layerViewport,screenViewport,paint);
    }

    public GameObject getParent(){return null;}
}


