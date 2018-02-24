package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * allows a tile to be destroyed
 */

public interface IDestructibleTile {
    /**
     * get current hp of tile
     * @return
     */
    int getHP();

    /**
     * damage the tile by passed value
     * @param attackValue
     */
    void attack(int attackValue);

    /**
     * check if the tile has been destroyed
     * @return
     */
    boolean isDestroyed();
}
