package crazyhoneybadgerstudios.engine.animation;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Dariusz Jerzewski - 10/03/2017.
 */

public class FlipBook implements IAnimation{

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {

    }

    @Override
    public GameObject getParent() {
        return null;
    }
}
