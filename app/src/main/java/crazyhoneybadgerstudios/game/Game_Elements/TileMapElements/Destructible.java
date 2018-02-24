package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a destructible object
 */

public class Destructible extends Tile implements IDestructibleTile{
    private int HP = 1; //default hp
    private boolean destroyed = false;

    /**
     * constructor initialising the object
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bitmap
     * @param gameScreen
     * @param tileType
     */
    Destructible(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, TileType tileType){
        super(x,y,width,height,bitmap,gameScreen, tileType);
    }

    /**
     * get type of object
     * @return
     */
    @Override
    public TileType getType() {
        return super.getType();
    }

    /**
     * return hp left
     * @return
     */
    @Override
    public int getHP() {
        return HP;
    }

    /**
     * attack the block
     * @param attackValue
     */
    @Override
    public void attack(int attackValue) {
        HP -= attackValue;
        if(HP < 0)
            destroyed = true;
    }

    /**
     * check if the object has been destroyed
     * @return
     */
    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
