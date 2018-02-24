package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import crazyhoneybadgerstudios.util.Vector2;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * interface to help get a consistant behaviour throughout multiple entities, allows extensibility
 */

public interface IEntity {
    /**
     * type of entity
     * @return
     */
    EntityType getType();

    /**
     * initialise entity with position
     * @param position
     */
    void init(Vector2 position);

    /**
     * get direction the entity is facing at the moment
     * @return
     */
    Direction getDirection();

    /**
     * set new direction for the entity to face
     * @param direction
     */
    void setDirection(Direction direction);

    /**
     * make entity active
     */
    void activate();

    /**
     * deactivate entity
     */
    void deactivate();

    /**
     * check if the entity is active
     * @return
     */
    boolean isActive();
}
