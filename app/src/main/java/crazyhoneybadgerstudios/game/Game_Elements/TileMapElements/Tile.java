package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a basic tile object, allowing
 */

public class Tile extends GameObject implements ITile{
    /**
     * constructor allowing you to create new Tiles
     */
    private TileType tileType;
    public Tile(float x, float y, float width, float height, Bitmap bitmap, GameScreen gameScreen, TileType tileType){
        super(x,y,width,height,bitmap, gameScreen);
    }

    /**
     * get tile type
     * @return
     */
    @Override
    public TileType getType() {
        return tileType;
    }

}
