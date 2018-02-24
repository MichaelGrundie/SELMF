package crazyhoneybadgerstudios.engine.animation;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.Animation;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Dariusz Jerzewski - 10/03/2017.
 * a system to store, process and dispose of animations
 */

public class Animator {
    private ArrayList<IAnimation> animations;

    /**
     * Animator constructor
     */
    public Animator(){
        animations = new ArrayList<>();
    }

    /**
     * add animation to queue
     * @param ianimation
     */
    public void add(IAnimation ianimation){ animations.add(ianimation);}

    /**
     * clear the queue
     */
    public void clear(){
        animations.clear();
    }

    /**
     * update animations in queue, if finished dispose to preserve resources
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime){
        for(Iterator<IAnimation> iterator = animations.iterator(); iterator.hasNext(); ){
            IAnimation temp = iterator.next();
            if(temp.isFinished() || temp.getParent() == null){
                iterator.remove();
            }
            else{
                temp.update(elapsedTime);
            }
        }
    }
}
