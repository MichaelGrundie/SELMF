package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * allows setting modifiers with different effects
 */

public interface IModifyingTile {
    /**
     * get modifier of the tile
     * @return
     */
    float getModifier();

    /**
     * set new modifier
     * @param modifier
     */
    void setModifier(float modifier);
}
