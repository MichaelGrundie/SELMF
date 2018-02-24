package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.game_screens.EndlessScreen;
import crazyhoneybadgerstudios.util.BoundingBox;
import crazyhoneybadgerstudios.util.CollisionDetector;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.PerlinNoise;
import crazyhoneybadgerstudios.util.RNG;
import crazyhoneybadgerstudios.util.Vector2;

import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * Created by Dariusz Jerzewski on 06/05/2017.
 * create a randongly generated tile map using perlin noise
 */

public class PerlinMap{

    private PerlinNoise ps; //perlin noise object

    //arrays to store the calculated perlin weights and store tiles
    private List<Map.Entry<Vector2, Float>> tiles = new ArrayList<>();
    private List<Map.Entry<Vector2, Tile>> tileList = new ArrayList<>();

    private long seed; //seed used to generate map

    private int xMax, yMax; //max x tiles and maax y tiles
    private float width, height; //width and height of each tiles

    private EndlessScreen endlessScreen; //screen to get player etc

    private Tile tempTile = null;

    private Bitmap[] tileTypes; //spritesheet

    private boolean mapNeedsUpdate = true; //used to rerender map

    /**
     * constructur taking in a string as a seed
     * @param seed
     * @param xtiles
     * @param ytiles
     * @param width
     * @param height
     * @param gameScreen
     */
    public PerlinMap(String seed, int xtiles, int ytiles, float width, float height, EndlessScreen gameScreen)
    {
        this.width = width;
        this.height = height;
        endlessScreen = gameScreen;
        this.seed = RNG.generateSeed(seed); //convert a string to long and pass it as a new seed
        ps = new PerlinNoise(this.seed);
        xMax = xtiles;
        yMax = ytiles;

        tileTypes = GraphicsHelper.createArrayOfImages(AssetLoading.mapSheet, 32, 32, 15);
    }

    /**
     * get new position for an entity
     * @param e - an entity to be placed in the world
     * @param avoid - a position to avoid
     */
    public void getNewPosition(Entity e, Vector2 avoid) {
        for (AbstractMap.Entry t : tileList) {
            Tile temp = (Tile) t.getValue();
            if (temp.getType() == TileType.FLOOR) {
                if (avoid != null) {
                    if (temp.position != avoid){
                        Log.d("position", temp.toString() + " " + avoid.toString());
                        e.position = new Vector2(temp.position);
                    }
                } else {
                    Log.d("position", temp.toString());
                    e.position = new Vector2(temp.position);
                }
            }
        }
    }

    /**
     * check for collisions with tiles
     * @param v - vector
     * @return result
     */
    public boolean isCollision(Entity e, Vector2 v){
        for(AbstractMap.Entry t : tileList){
            Tile temp = (Tile)t.getValue();
            if(!(temp instanceof Wall))
                continue;;
            BoundingBox b = new BoundingBox(v.x, v.y, e.getBound().halfWidth, e.getBound().halfHeight);

            Log.d("col", e.position.toString() + "   " + v.toString());

            if(CollisionDetector.isCollision(temp.getBound(), b)){
                Log.d("col", "collided");
                return true;
            }
        }
        return false;
    }

    /**
     * constructor taking in predefined seed as long
     * @param seed
     * @param xtiles
     * @param ytiles
     * @param width
     * @param height
     * @param gameScreen
     */
    public PerlinMap(long seed, int xtiles, int ytiles, float width, float height, EndlessScreen gameScreen)
    {
        endlessScreen = gameScreen;
        this.seed = seed;
        ps = new PerlinNoise(this.seed);
        xMax = xtiles;
        yMax = ytiles;
    }

    /**
     * perform map update - rerender the frames based on the player position
     * @param elapsedTime
     */
    public void update(ElapsedTime elapsedTime){
        if(!mapNeedsUpdate) return;

        //tile type thresholds
        float floorVal = 0.0165f;
        float floorVal1 = -0.075f;
        tileList = new ArrayList<>();

        //get the player from gamescreen
        TileMapPlayer player = endlessScreen.getPlayer();

        //iterate through x andy coordinates
        for(int x = 0; x < xMax; x++){
            for(int y = 0; y < yMax; y++){
                Vector2 vector2 = new Vector2((x - xMax/2) * width, (y - yMax/2) * height); //scale the tile count to actual world position
                vector2.add(new Vector2(player.position));

                //calculate the perlin noise value based on the scaled coordinates and add the value to the hashmap
                Vector2 tmps = new Vector2(vector2);
                tmps.multiply(0.00234f);
                float newPS = ps.sample(tmps);
                AbstractMap.SimpleEntry<Vector2, Float> tmp = new AbstractMap.SimpleEntry<Vector2, Float>(vector2, newPS);
                tiles.add(tmp);

                //get a tile depending on thresholds definad at the beginning of this function
                if(tmp.getValue() < floorVal){
                    tempTile = new Floor(1f, tmp.getKey().x, tmp.getKey().y, 32f,32f,  tileTypes[2], endlessScreen, TileType.FLOOR);
                }
                else if(tmp.getValue() < floorVal1){
                    tempTile = new Floor(1f, tmp.getKey().x, tmp.getKey().y, 32f,32f,  tileTypes[3], endlessScreen, TileType.FLOOR);
                }
                else{
                    tempTile =new Wall(tmp.getKey().x, tmp.getKey().y, 32f,32f,  tileTypes[10], endlessScreen, TileType.WALL);
                }
                tileList.add(new AbstractMap.SimpleEntry<Vector2, Tile>(tmp.getKey(), tempTile));
            }
        }
        ps.resetRNG();
        mapNeedsUpdate = false;
    }


    /**
     * draw the map to the screen
     * @param elapsedTime
     * @param graphics2D
     * @param layerViewport
     * @param screenViewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport,
                            ScreenViewport screenViewport)
    {

        for(AbstractMap.Entry<Vector2, Tile> t : tileList){
            t.getValue().draw(elapsedTime,graphics2D,layerViewport,screenViewport);
        }

        tiles.clear();// up here since draw is executed after update
    }

    public void needsUpdate(){
        mapNeedsUpdate = true;
    }
}
