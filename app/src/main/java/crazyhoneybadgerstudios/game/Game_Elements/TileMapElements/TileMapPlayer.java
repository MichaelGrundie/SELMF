package crazyhoneybadgerstudios.game.Game_Elements.TileMapElements;

import android.graphics.Bitmap;
import android.util.Log;

import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.game.game_screens.EndlessScreen;
import crazyhoneybadgerstudios.game.game_screens.TileMapScreen;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.util.Vector2;

/**
 * @author Courtney Shek
 * &Dariusz Jerzewski refactoring
 * The player used on the tileMap
 */

public class TileMapPlayer extends Entity implements IMovable {
    private EntityType entityType;
    private Camera camera;

    private Movement movement;

    //Bitmaps of the player walking
    private Bitmap playerWalkDown;
    private Bitmap playerWalkLeft;
    private Bitmap playerWalkRight;
    private Bitmap playerWalkUp;

    /**
     * Creates a new TileMapPlayer object
     *
     * @param bitmap - The bitmap representing the player
     * @param tileMapScreen - The screen the player belongs to
     */
    public TileMapPlayer(Direction direction, float x, float y, float width, float height, Bitmap bitmap, TileMapScreen tileMapScreen, EntityType entityType)
    {
        super(direction,x,y,width,height,bitmap,tileMapScreen);
        this.entityType = entityType;
        setUpBitmaps();
        camera = new Camera(16,9,width,height,tileMapScreen.getLayerViewport());
//        generatePlayerStartPosition();
    }

    /**
     * new instance for Endless Screen
     * @author Dariusz Jerzewski
     * @param direction
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bitmap
     * @param endlessScreen
     * @param entityType
     */
    public TileMapPlayer(Direction direction, float x, float y, float width, float height, Bitmap bitmap, EndlessScreen endlessScreen, EntityType entityType)
    {
        super(direction,x,y,width,height,bitmap,endlessScreen);
        this.entityType = entityType;
        setUpBitmaps();
        camera = new Camera(16,9,width,height,endlessScreen.getLayerViewport());
    }

    private void setUpBitmaps()
    {
        AssetStore assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddBitmap("PlayerWalk", "img/TileMap/PlaceHolderPerson.png");
        Bitmap[] playerStrip = GraphicsHelper.createArrayOfImages(assetManager.
                getBitmap("PlayerWalk"), 32, 32, 36);

        playerWalkDown = playerStrip[22];
        playerWalkLeft = playerStrip[14];
        playerWalkRight = playerStrip[32];
        playerWalkUp = playerStrip[4];
    }

//    /**
//     * Generates the player in a random position on the map
//     */
//    private void generatePlayerStartPosition()
//    {
//        Random rand = new Random();
//        boolean validNum = false;
//        int playerXPos;
//        int playerYPos;
//        //Makes sure the character is generated in a valid space
//        do {
//            playerXPos = rand.nextInt(16);
//            playerYPos = rand.nextInt(11);
//
//            if(tileMap.getMapTiles()[playerXPos][playerYPos].getBitmap()
//                    .sameAs(tileMap.getTileBitmap(TileMap.typesOfTiles.WHITE_TILE)))
//            {
//                validNum = true;
//            }
//
//        } while(!validNum);
//
//        setPosition((tileMap.getMapTiles()[playerXPos][playerYPos].position.x),
//                (tileMap.getMapTiles()[playerXPos][playerYPos].position.y));
//    }

    /**
     * Update the TileMapPlayer
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    @Override
    public void update(ElapsedTime elapsedTime)
    {
        //Depending upon the right/up/down/left movement touch controls
        //Set an appropriate x or y velocity. If the user does not want
        //to move then the acceleration is zero and the velocity is zero.


        //Call the sprite's update method to apply the defined
        //accelerations and velocities to provide a new position
        super.update(elapsedTime);
        if(movement.isMoving())
            movement.move(direction);
        movement.update();
        camera.move(position);


        if(direction == Direction.UP) {
            mBitmap = playerWalkUp;
        }
        else if(direction == Direction.DOWN) {
            mBitmap = playerWalkDown;
        }
        else if(direction == Direction.LEFT) {
            mBitmap = playerWalkLeft;
        }
        else if(direction == Direction.RIGHT) {
            mBitmap = playerWalkRight;
        }

//        //Check that our new position has not collided by one of the
//        //defined walls. If so, then removing any overlap and ensure a valid
//        //velocity
//        checkForAndResolveCollisions(mapTiles, null);
    }

//    /**
//     * Checks for and then resolves any collision between the player and the walls of the
//     * tile map rooms. Also resolves collision between the player and an NPC.
//     *
//     * @param mapTiles - The map tiles to test for collision against
//     */
//    public void checkForAndResolveCollisions(Sprite[][] mapTiles, NPC npc)
//    {
//        CollisionDetector.CollisionType collisionType = CollisionDetector.CollisionType.None;
//
//        if(mapTiles != null)
//        {
//            for(Sprite sprite[] : mapTiles)
//            {
//                for(Sprite tiles : sprite)
//                {
//                    if(tiles.getBitmap().
//                            sameAs(tileMap.getTileBitmap(TileMap.typesOfTiles.GREY_BRICK)))
//                    {
//                        collisionType = CollisionDetector.determineAndResolveCollision(this, tiles);
//                    }
//                }
//            }
//        }
//
//        if(npc != null)
//        {
//            collisionType = CollisionDetector.determineAndResolveCollision(this, npc);
//        }
//
//        switch(collisionType)
//        {
//            case Top:
//                velocity.y = 0.0f;
//                break;
//            case Bottom:
//                velocity.y = 0.0f;
//                break;
//            case Left:
//                velocity.x = 0.0f;
//                break;
//            case Right:
//                velocity.x = 0.0f;
//                break;
//            case None:
//                break;
//        }
//
//    }

    /**
     * getters and setters
     * @param movement
     */
    @Override
    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    @Override
    public Movement getMovement() {
        return movement;
    }

    @Override
    public EntityType getType() {
        return entityType;
    }
}
