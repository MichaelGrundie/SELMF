package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Random;

import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.util.CollisionDetector;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Courtney Shek
 * &Dariusz Jerzewski - refactoring
 *
 * Creates a TileMap for the TileMap screen
 */

public class TileMap {

    /**
     * Enum of types of tiles the map can have
     * Can be updated with more, as the sprite sheet we have has more options than we need
     */
    public enum typesOfTiles
    {
        WHITE_TILE, GREY_BRICK
    }

    //Constructor variables
    private Tile[][] mapTiles; //The tiles that are going to be drawn to the map
    private String[][] tileMap; //The map read from the text file
    private Bitmap[] tileTypes; //The types of tiles that can be drawn to the map

    private final int tileHeight;
    private final int tileWidth;

    private AssetStore assetManager;

    private TileMapScreen tileMapScreen;

    protected TileMap(){
        tileHeight = 32;
        tileWidth = 32;
    }

    protected void setScreen(TileMapScreen tileMapScreen){this.tileMapScreen = tileMapScreen;}

    public TileMap(TileMapScreen tileMapScreen)
    {
        tileTypes = GraphicsHelper.createArrayOfImages(AssetLoading.mapSheet, 32, 32, 15);

        tileHeight = 32;
        tileWidth = 32;

        assetManager = tileMapScreen.getGame().getAssetManager();
        this.tileMapScreen = tileMapScreen;

        readTileMap(23, 17);
        for(Entity e : tileMapScreen.getEntities())
        {
            generateNPCPosition(e);
        }
        generateNPCPosition(tileMapScreen.getPlayer());
    }

    /**
     * Creates a 2D array of the tileMap from reading a text file
     *
     * @param noOfRows - Number of rows in the text file
     * @param noOfColumns - Number of columns in the text file
     */
    private void readTileMap(int noOfRows, int noOfColumns)
    {
        //Randomly chooses 1 of 4 TileMap text files to read in
        Random rand = new Random();
        boolean validNum = false;
        int mapNum;
        do {
            mapNum = rand.nextInt(5);

            if(mapNum > 0 && mapNum <= 4)
            {
                validNum = true;
            }
        } while(!validNum);

        String tileMapLocation = "TileMaps/TileMap" + Integer.toString(mapNum) + ".txt";
        assetManager.loadAndAddTxtFile("TileMap", tileMapLocation);
        String[] tileLines = assetManager.getTxtFile("TileMap");
        tileMap = new String[noOfRows][noOfColumns];

        for(int i = 0; i < noOfRows; i++)
        {
            String[] tileChar = tileLines[i].split(" ");
            for(int j = 0; j < noOfColumns; j++)
            {
                tileMap[i][j] = tileChar[j];
            }
        }

        createRoomTiles(noOfRows, noOfColumns);
    }


    /**
     * Creates tiles for the rooms created
     */
    private void createRoomTiles(int noOfRows, int noOfColumns)
    {
        mapTiles = new Tile[noOfRows][noOfColumns];

        for(int i = 0; i < noOfRows; i++)
        {
            for(int j = 0; j < noOfColumns; j++)
            {
                int x = i % noOfRows;
                int y = j % noOfColumns;

                if(tileMap[i][j].equals("."))
                {
                    mapTiles[i][j] = new Wall((x * tileWidth), (y * tileHeight), tileWidth, tileHeight,
                            getTileBitmap(typesOfTiles.GREY_BRICK), tileMapScreen, TileType.WALL);
                }
                else
                {
                    mapTiles[i][j] = new Floor(1f, (x * tileWidth), (y * tileHeight), tileWidth, tileHeight,
                            getTileBitmap(typesOfTiles.WHITE_TILE), tileMapScreen, TileType.FLOOR);
                }
            }
        }
    }

    /**
     * Returns the appropriate Bitmap for the type of tile that should be drawn
     *
     * @return The appropriate Tile Bitmap
     */
    public Bitmap getTileBitmap(typesOfTiles typeOfTile)
    {
        Bitmap tile = null;
        switch(typeOfTile)
        {
            case WHITE_TILE:
                tile = tileTypes[2];
                break;
            case GREY_BRICK:
                tile = tileTypes[10];
                break;
            default:
                break;
        }
        return tile;
    }

    /**
     * Draws the Tile Map
     *
     * @param elapsedTime - Elapsed time information for the frame
     * @param graphics2D - Graphics instance used to draw the tile map
     * @param layerViewport - The layer viewport the map is drawn to
     * @param screenViewport - The screen viewport the map is drawn to
     */
    public void drawTileMap(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport,
                       ScreenViewport screenViewport)
    {
        for(Tile sprite[] : mapTiles)
        {
            for(Tile tiles : sprite)
            {
                tiles.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            }
        }
    }

    /**
     * Generates the position of the NPC on the tile map
     */
    public void generateNPCPosition(Entity e)
    {
        Random rand = new Random();
        boolean validNum = false;
        int playerXPos;
        int playerYPos;
        //Makes sure the character is generated in a valid space
        do {
            playerXPos = rand.nextInt(23);
            playerYPos = rand.nextInt(17);

            if(mapTiles[playerXPos][playerYPos].getBitmap()
                    .sameAs(getTileBitmap(TileMap.typesOfTiles.WHITE_TILE)) &&
                    playerXPos != tileMapScreen.getPlayer().position.x &&
                    playerYPos != tileMapScreen.getPlayer().position.y)
            {
                validNum = true;
            }

        } while(!validNum);

        e.position = new Vector2((mapTiles[playerXPos][playerYPos].position.x),
                (mapTiles[playerXPos][playerYPos].position.y));
    }
    //Getters and setters

    /**
     * Checks for and then resolves any collision between the NPC and the walls of the
     * tile map rooms.
     *
     */
    public void checkForAndResolveCollisions(TileMapPlayer e)
    {
        CollisionDetector.CollisionType collisionType = CollisionDetector.CollisionType.None;

        if(mapTiles != null)
        {
            for(Tile sprite[] : mapTiles)
            {
                for(Tile tiles : sprite)
                {
                    if(tiles.getBitmap().
                            sameAs(getTileBitmap(TileMap.typesOfTiles.GREY_BRICK)))
                    {
                        collisionType = CollisionDetector.determineAndResolveCollision(e, tiles);
                    }
                }
            }
        }

        if(collisionType != CollisionDetector.CollisionType.None)
            e.getMovement().stop();

    }

    public ArrayList<Wall> getWallTiles(){
        return null;
    }
    public ArrayList<Floor> getFloorTiles(){
        return null;
    }

    public ArrayList<Destructible> getDestructibleTiles(){
        return null;
    }
//
//    public Sprite[][] getMapTiles() {
//        return mapTiles;
//    }
//
//    public void setMapTiles(Sprite[][] mapTiles) {
//        this.mapTiles = mapTiles;
//    }
}
