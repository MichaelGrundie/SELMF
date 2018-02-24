package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * enemy
 */

public class Enemy extends Entity implements IMovable {
    private EntityType entityType;//type of entity
    private TiledMovement tiledMovement; //movement behaviour for the enemy

    public Enemy(Direction direction, float x, float y, float width, float height, Bitmap bitmap, GameScreen tileMapScreen, EntityType entityType){
        super(direction, x, y, width, height, bitmap, tileMapScreen);
    }

    /**
     * set new movement
     * @param movement
     */
    @Override
    public void setMovement(Movement movement) {
        tiledMovement = (TiledMovement)movement;
    }

    /**
     * return movement used by entity
     * @return
     */
    @Override
    public Movement getMovement() {
        return tiledMovement;
    }

    /**
     * get entity type
     * @return
     */
    @Override
    public EntityType getType() {
        return entityType;
    }
}
