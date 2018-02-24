package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Courtney Shek
 * & Dariusz Jerzewski - refactoring
 *
 * Creates non-playable characters for the Tile Map
 */

public class NPC extends Entity implements IMovable {
    private Movement movement;
    private EntityType npcType;

    /**
     * Creates a new NPC
     *
     * @param bitmap - Bitmap representing the NPC
     * @param tileMapScreen - The Tile Map Screen the NPC belongs to
     */
    public NPC(Direction direction, float x, float y, float width, float height, Bitmap bitmap, GameScreen tileMapScreen, EntityType type)
    {
        super(direction, x, y, width, height, bitmap, tileMapScreen);
        npcType = type;
    }

    /**
     * Update the NPC
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    @Override
    public void update(ElapsedTime elapsedTime)
    {
        super.update(elapsedTime);
        //checkForAndResolveCollisions(mapTiles);
        //movement.move();
    }


    /**
     * sets new movement behaviour for the entity
     * @param movement
     */
    @Override
    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    /**
     * gets current mvoment behaviour
     * @return
     */
    @Override
    public Movement getMovement() {
        return movement;
    }

    /**
     * get entity type
     * @return
     */
    @Override
    public EntityType getType() {
        return npcType;
    }
}
