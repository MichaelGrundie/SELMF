package crazyhoneybadgerstudios.engine.animation;

import android.graphics.Bitmap;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Dariusz Jerzewski - 11/03/2017.
 * An object to get IAnimation objects, it adds them to the queue automatically
 */

public class AnimationFactory {
    /**
     * create animation by changing bitmaps of gameobject
     * @param animationLength
     * @param foreverLoop
     * @param animationSync
     * @param images
     * @param parent
     * @param animator
     * @return
     */
    public static IAnimation AnimateGameObject(float animationLength, boolean foreverLoop, boolean animationSync, Bitmap[] images, GameObject parent, Animator animator){
        IAnimation temp = new GameObjectFlipBook(animationLength, foreverLoop, animationSync, images, parent);

        animator.add(temp);
        return temp;
    }

    public static IAnimation FlipBook(){
        IAnimation temp = null;

        return temp;
    }
}
