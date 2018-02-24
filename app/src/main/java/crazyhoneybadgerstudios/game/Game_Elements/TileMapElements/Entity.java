package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.Sprite;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 */

public abstract class Entity extends Sprite implements IEntity{
    protected Direction direction; //entity's direnction
    boolean active = false; //is active?

    /**
     * initialising the entity, set max velocity and acceleration
     * @param direction
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bitmap
     * @param gameScreen
     */
    public Entity(Direction direction, float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen){
        super(x,y,width,height,bitmap,gameScreen);
        this.direction = direction;
        this.maxAcceleration = 15f;
        this.maxVelocity = 50f;
        this.maxAngularVelocity =40f;
        this.maxAngularAcceleration = 11f;
        active = true;
    }

    /**
     * initialise the object with position
     * @param position
     */
    @Override
    public void init(Vector2 position){
        this.position = new Vector2(position);
    }

    /**
     * get direction facing by the object
     * @return
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * set new direction
     * @param direction
     */
    @Override
    public void setDirection(Direction direction){
        this.direction = direction;
    }

    /**
     * activate entity
     */
    @Override
    public void activate(){
        active = true;
    };

    /**
     * deactivate entity
     */
    @Override
    public void deactivate(){
        active = false;
    };

    /**
     * check if the entity is currently active
     * @return
     */
    @Override
    public boolean isActive(){
        return active;
    }
}
