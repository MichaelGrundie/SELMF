package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * iterface for a entity that is able to move
 */

public interface IMovable {
    /**
     * set new movement behaviour
     * @param movement
     */
    void setMovement(Movement movement);

    /**
     * get movement
     * @return
     */
    Movement getMovement();
}
