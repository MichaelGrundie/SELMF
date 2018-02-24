package crazyhoneybadgerstudios.engine.animation;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Dariusz Jerzewski - 10/03/2017.
 * interface to provide compability between multiple animations to be used with Animator
 * allows easy acces to all Animation types within one list
 */

public interface IAnimation {
    /**
     * require update function
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime);

    /**
     * if animation finished
     * @return
     */
    public boolean isFinished();

    /**
     * get parent object
     * @return
     */
    public GameObject getParent();
}
