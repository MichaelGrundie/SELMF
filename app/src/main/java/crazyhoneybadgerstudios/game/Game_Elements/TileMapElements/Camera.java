package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.LayerViewport;

/**
 * Created by Dariusz Jerzewski on 06/05/2017.
 */

public class Camera {
    private LayerViewport camera; //layerviewport representing the camera

    /**
     * constructor initialising the camera
     * @param xTiles
     * @param yTiles
     * @param tileWidth
     * @param tileHeight
     * @param layerViewport
     */
    public Camera(int xTiles, int yTiles, float tileWidth, float tileHeight, LayerViewport layerViewport) {
        camera = layerViewport;
        layerViewport.set(layerViewport.x, layerViewport.y, (xTiles * tileWidth) / 2, (yTiles * tileHeight) / 2);
    }

    /**
     * update camera's position
     * @param pos
     */
    public void move(Vector2 pos){
        camera.set(pos.x, pos.y, camera.halfWidth, camera.halfHeight);
    }
}
