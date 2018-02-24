package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a wall tile - providing easy way to checking if collision with tiles
 */

public class Wall extends Tile implements ITile {

    Wall(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, TileType tileType){
        super(x,y,width,height,bitmap,gameScreen, tileType);
    }

    @Override
    public TileType getType() {
        return super.getType();
    }
}
