package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.util.Log;

import crazyhoneybadgerstudios.util.Vector2;

/**
 * @author Courtney Shek
 * & Dariusz Jerzewski - refactoring and some code
 * enables movement of an entity
 */

public class Movement {
    protected boolean isMoving = false; //is the target object moving?

    protected Entity target; //target entity

    /**
     * constructor to initialise new movement instance
     * @param target
     */
    public Movement(Entity target){
        this.target = target;
    }

    /**
     * stop movement of target
     */
    public void stop(){
        isMoving = false;
        target.velocity = Vector2.Zero;
        target.acceleration = Vector2.Zero;
    }

    /**
     * start movement
     */
    public void start(){
        isMoving = true;
    }

    /**
     * check if is moving
     * @return
     */
    boolean isMoving(){
        return isMoving;
    }

    /**
     * perform move of the target into specific directoin
     * @param direction
     */
    public void move(Direction direction){
        target.setDirection(direction);
        if(isMoving){
            if(direction == Direction.UP)
            {
                target.velocity.y = target.maxVelocity;
            }
            else if(direction == Direction.DOWN)
            {
                target.velocity.y = -target.maxVelocity;
            }
            else if(direction == Direction.RIGHT)
            {
                target.velocity.x = target.maxVelocity;
            }
            else if(direction == Direction.LEFT)
            {
                target.velocity.x = -target.maxVelocity;
            }
        }
        else{
            target.velocity = Vector2.Zero;
            target.acceleration = Vector2.Zero;
        }
        stop();
    }

    public void update(){
    }
}
