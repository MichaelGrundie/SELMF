package crazyhoneybadgerstudios.engine.animation;

import android.graphics.Bitmap;
import android.util.Log;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.util.CyclicQueue;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Dariusz Jerzewski - 10/03/2017.
 * animate gameObject - change images to make it appear as if it was moving
 */

public class GameObjectFlipBook implements IAnimation{
    private boolean animationSync = false;
    private boolean finished = false;
    private boolean foreverLoop = false;
    private float animationLength;
    private float currentTime;
    private CyclicQueue<Bitmap> images;
    GameObject parent;
    Bitmap originalImage;

    public GameObjectFlipBook(float animationLength, boolean foreverLoop, boolean animationSync, Bitmap[] images, GameObject parent){
        this.animationSync = animationSync;
        this.foreverLoop = foreverLoop;
        this.animationLength = animationLength;

        this.images = new CyclicQueue<>();
        this.images.enqueue(images);

        originalImage = parent.getBitmap();

        this.parent = parent;
    }

    public void finishAnimation(){
        finished = true;
    }

    @Override
    public boolean isFinished(){
        return finished;
    }

    @Override
    public GameObject getParent() {
        return parent;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        if(currentTime >= animationLength/images.getSize()){
            int flipcount = 1;
            if(animationSync)
                flipcount = (int) Math.ceil(currentTime%(animationLength/images.getSize()));
            if(!foreverLoop && flipcount + images.getIndex() < images.getSize())
                flipcount = images.getSize() - images.getIndex() - 1; // truncate flipcount to not exceed size, if animation has to be played once
            parent.setBitmap(images.peek());

            Log.d("cycles", "" + flipcount + "\tctime: " + currentTime + "\tanimationL: " + animationLength + "\timagesS: " + images.getSize() + "\tflips: " + Math.ceil(currentTime/(animationLength/images.getSize())));

            for(int i = 0; i < flipcount; i++)
                images.cycle();

            currentTime = 0f;
        }
        else{
            currentTime += elapsedTime.stepTime;
        }
        if(!foreverLoop && elapsedTime.stepTime >= animationLength){
            parent.setBitmap(originalImage);
            finished = true;
        }
    }

}
