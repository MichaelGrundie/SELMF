package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a tile that allows player to walk on it
 */

public class Floor extends Tile implements IModifyingTile, ITile{
    private float speedModifier = 0.f; //reduce/increase speed scalar

    /**
     *
     * @param speedModifier effect the tile has on movement speed of the player
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bitmap
     * @param gameScreen
     */
    Floor(float speedModifier, float x, float y, float width, float height,  Bitmap bitmap, GameScreen gameScreen, TileType tileType){
        super(x,y,width, height,bitmap, gameScreen, tileType);
        this.speedModifier = speedModifier;
    }

    //getters and setters
    @Override
    public float getModifier() {
        return speedModifier;
    }

    @Override
    public void setModifier(float modifier) {
        speedModifier = modifier;
    }

    @Override
    public TileType getType() {

        return super.getType();
    }
}
