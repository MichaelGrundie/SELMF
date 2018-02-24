package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.Direction;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.Enemy;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.Entity;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.EntityType;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.Movement;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.NPC;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.TileMap;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.TileMapPlayer;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.util.BoundingBox;
import crazyhoneybadgerstudios.util.CollisionDetector;
import crazyhoneybadgerstudios.util.SimpleControl;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;

/**
 * @author Courtney Shek
 *
 * A tile map created as additional functionality for the game. A player has the freedom to roam
 * around on a map, in search of opponents to battle. Future development would include objects
 * for the user to interact with to give help/advice to help defeat unimon. We would also have
 * liked to work on randomly generating text files rather than loading them from text files.
 */

public class TileMapScreen extends GameScreen {

    //Constructor variables
    private TileMap tileMap;

    private TileMapPlayer player;
    private ArrayList<Entity> entities = new ArrayList<>();

    private boolean playerCollided = false;
    private boolean popupDisplayed = false;
    private Entity collidedEntity = null;

    private LayerViewport layerViewport;

    //Define the width and height of the game world
    private final float LEVEL_WIDTH = 700.0f;
    private final float LEVEL_HEIGHT = 520.0f;
    
    //Touch controls for player input
    private SimpleControl moveLeft;
    private SimpleControl moveRight;
    private SimpleControl moveUp;
    private SimpleControl moveDown;
    private List<SimpleControl> simpleControls = new ArrayList<>();

    //The player's opponent
    private Enemy enemy;

    //Rectangle to draw pop ups
    private Rect popUpRect;

    //Bitmap to draw appropriate NPC advice
    private Bitmap advice;

    //Popup buttons
    private ScreenViewportReleaseButton yesButton;
    private ScreenViewportReleaseButton noButton;
    private ScreenViewportReleaseButton okButton;

    //An array list of NPCs created for the Tile Map Screen
//    private ArrayList<NPC> npcs;

    /**
     * Creates a new Tile Map Screen
     *
     * @param game - The game instance the Tile Map Screen belongs to
     */
    public TileMapScreen(Game game)
    {
        super("TileMap", game);

        setUpScreenViewPort();
        layerViewport = new LayerViewport();

        popUpRect = new Rect(screenViewport.left + screenViewport.width/4, screenViewport.bottom/4,
                screenViewport.right - screenViewport.width/4, screenViewport.bottom/4 * 3);


        player = new TileMapPlayer(Direction.UP,0f, 0f, (float)AssetLoading.player.getWidth(), (float)AssetLoading.player.getHeight(),AssetLoading.player, this, EntityType.PLAYER);
        player.setMovement(new Movement(player));

        createNPCs();
        tileMap = new TileMap(this);

        setUpArrows();
        setUpButtons();
    }

    /**
     * Creates the NPCs on screen
     */
    private void createNPCs()
    {
        for(int i = 0; i <4; i++){
            NPC temp = new NPC(Direction.UP, 0f, 0f, (float)AssetLoading.enemy.getWidth(), (float)AssetLoading.enemy.getHeight(), AssetLoading.enemy, this, (i%2 == 0)?EntityType.CLASSMATE : EntityType.PROFESSOR);
            temp.setMovement(new Movement(temp));
            entities.add(temp);
        }
        enemy = new Enemy(Direction.LEFT,0f,0f,(float)AssetLoading.enemy.getWidth(), (float)AssetLoading.enemy.getHeight(), AssetLoading.enemy, this,EntityType.ENEMY);
        entities.add(enemy);
//        npcs.add(enemy);
//        npcBoy = new NPC(AssetLoading.npcboy, this, tileMap, NPCType.CLASSMATE, npcs);
//        npcs.add(npcBoy);
//        npcGirl = new NPC(AssetLoading.npcgirl, this, tileMap, NPCType.CLASSMATE, npcs);
//        npcs.add(npcGirl);
//        npcProfessor = new NPC(AssetLoading.professor, this, tileMap, NPCType.PROFESSOR, npcs);
//        npcs.add(npcProfessor);
    }


    /**
     * Sets up arrows on the screen
     */
    private void setUpArrows()
    {
        float upDownControlHeight = screenViewport.width/10;
        float upDownControlWidth = screenViewport.width/8;

        float leftRightControlWidth = screenViewport.width/10;
        float leftRightControlHeight = screenViewport.width/8;

        float upDownControlX = screenViewport.left + upDownControlWidth;
        float upControlY = screenViewport.bottom - upDownControlHeight * 2;
        float downControlY = screenViewport.bottom - (upDownControlHeight/2);

        float leftRightControlY = screenViewport.bottom - leftRightControlHeight;
        float leftControlX = screenViewport.left + (leftRightControlWidth / 2);
        float rightControlX = screenViewport.left + leftRightControlWidth * 2;

        moveDown = new SimpleControl(upDownControlX, downControlY, upDownControlWidth, 
                upDownControlHeight, "DownControl", this);
        moveUp = new SimpleControl(upDownControlX, upControlY, upDownControlWidth, 
                upDownControlHeight, "UpControl", this);
        moveLeft = new SimpleControl(leftControlX, leftRightControlY, leftRightControlWidth,
                leftRightControlHeight, "LeftControl", this);
        moveRight = new SimpleControl(rightControlX, leftRightControlY, leftRightControlWidth,
                leftRightControlHeight, "RightControl", this);
        simpleControls.add(moveDown);
        simpleControls.add(moveLeft);
        simpleControls.add(moveRight);
        simpleControls.add(moveUp);
    }

    private void handleTouchEvent(TouchEvent touchEvent){
        Vector2 lPos = new Vector2(touchEvent.x, touchEvent.y);
        //InputHelper.convertScreenPosIntoLayer(screenViewport,new Vector2(touchEvent.x, touchEvent.y), layerViewport,lPos);
        if(moveUp.getBound().contains(lPos.x, lPos.y)){
            player.getMovement().start();
            player.getMovement().move(Direction.UP);
            player.setDirection(Direction.UP);
        }
        else if(moveRight.getBound().contains(lPos.x, lPos.y)){
            player.getMovement().start();
            player.getMovement().move(Direction.RIGHT);
            player.setDirection(Direction.RIGHT);
        }
        else if(moveDown.getBound().contains(lPos.x, lPos.y)){
            player.getMovement().start();
            player.getMovement().move(Direction.DOWN);
            player.setDirection(Direction.DOWN);
        }
        else if(moveLeft.getBound().contains(lPos.x, lPos.y)){
            player.getMovement().start();
            player.getMovement().move(Direction.LEFT);
            player.setDirection(Direction.LEFT);
        }
    }

    /**
     * Updates the Tile Map Screen. Invoked automatically by the game.
     *
     * @param elapsedTime - Elapsed time information for the frame
     */
    public void update(ElapsedTime elapsedTime)
    {
        //Update the player
        player.update(elapsedTime);
        Input input = mGame.getInput();
        for(TouchEvent e : input.getTouchEvents()){
            switch(e.type){
                case TouchEvent.SINGLE_TAP_CONFIRMED:
                case TouchEvent.LONG_PRESS:
                case TouchEvent.TOUCH_DOWN:{
                    handleTouchEvent(e);
                    break;
                }
            }
        }
        player.getMovement().stop();

        // Ensure the player cannot leave the confines of the world
        BoundingBox playerBound = player.getBound();
        if (playerBound.getLeft() < 0)
            player.position.x -= playerBound.getLeft();
        else if (playerBound.getRight() > LEVEL_WIDTH)
            player.position.x -= (playerBound.getRight() - LEVEL_WIDTH);

        if (playerBound.getBottom() < 0)
            player.position.y -= playerBound.getBottom();
        else if (playerBound.getTop() > LEVEL_HEIGHT)
            player.position.y -= (playerBound.getTop() - LEVEL_HEIGHT);

        //Focus the layer viewport on the player's position
        layerViewport.x = player.position.x;
        layerViewport.y = player.position.y;

        // Ensure the viewport cannot leave the confines of the world
        if (layerViewport.getLeft() < 0)
            layerViewport.x -= layerViewport.getLeft();
        else if (layerViewport.getRight() > LEVEL_WIDTH)
            layerViewport.x -= (layerViewport.getRight() - LEVEL_WIDTH);

        if (layerViewport.getBottom() < 0)
            layerViewport.y -= layerViewport.getBottom();
        else if (layerViewport.getTop() > LEVEL_HEIGHT)
            layerViewport.y -= (layerViewport.getTop() - LEVEL_HEIGHT);

//        //Update the NPCS
//        for(NPC npc : npcs)
//        {
//            npc.update(elapsedTime);
//            NPCCollide(npc, playerBound);
//        }

        for(Entity e : entities){
            if(CollisionDetector.isCollision(player.getBound(), e.getBound())){
                player.getMovement().stop();
                playerCollided = true;
                collidedEntity = e;

            }
        }

        tileMap.checkForAndResolveCollisions(player);

        //If player has collided with enemy do these updates
        if(playerCollided && collidedEntity instanceof Enemy)
        {
            yesButton.update(elapsedTime);
            noButton.update(elapsedTime);

            if(yesButton.pushTriggered())
            {
                popupDisplayed = false;
                loadBattle();
            }
            else if(noButton.pushTriggered())
            {
                collidedEntity = null;
                playerCollided = false;
                popupDisplayed = false;
            }
        }
        else if(playerCollided && collidedEntity instanceof NPC)
        {

            okButton.update(elapsedTime);
            if(!popupDisplayed)
                advice = getAdvice(collidedEntity.getType());
            if(okButton.pushTriggered())
            {
                advice = null;
                popupDisplayed = false;
                collidedEntity = null;
                playerCollided = false;
            }
        }

    }
//
//    /**
//     * Checks if the player has collided with the NPC
//     *
//     * @param npc - NPC the player has collided with
//     * @param playerBound - The bound box of the player
//     */
//    private void NPCCollide(NPC npc, BoundingBox playerBound)
//    {
//        if(playerBound.intersects(npc.getBound()))
//        {
//            advice = getAdvice(npc.getType());
//            npc.setPlayerCollide(true);
//            player.checkForAndResolveCollisions(null, npc);
//        }
//    }

    /**
     * Takes the user to the load battle screen
     */
    private void loadBattle()
    {
        mGame.getScreenManager().removeScreen(this.getName());
        BattleLoadingScreen bls = new BattleLoadingScreen(mGame,1);
        mGame.getScreenManager().addScreen(bls);
        mGame.getScreenManager().setAsCurrentScreen("bls");
    }

    /**
     * Sets up yes, no and ok buttons for popups
     */
    private void setUpButtons()
    {
        float yesNoOKButtonHeight = screenViewport.right/10;
        float yesNoOKButtonWidth = screenViewport.right/8;
        float yesNoOKButtonY = screenViewport.bottom/4 * 3 - yesNoOKButtonHeight;
        float yesButtonX = popUpRect.left + yesNoOKButtonWidth;
        float noButtonX = popUpRect.right - yesNoOKButtonWidth;
        float okButtonX = popUpRect.left + yesNoOKButtonWidth * 2;

        yesButton = new ScreenViewportReleaseButton(yesButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "YesButton", null, this);
        noButton = new ScreenViewportReleaseButton(noButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "NoButton", null, this);
        okButton = new ScreenViewportReleaseButton(okButtonX, yesNoOKButtonY,
                yesNoOKButtonWidth, yesNoOKButtonHeight, "OKButton", null, this);
    }

    /**
     * Gets appropriate advice/message popup depending on which NPC the player has collided with
     *
     * @param type - The type of NPC the player has collided with
     * @return - The message bitmap to be drawn
     */
    private Bitmap getAdvice(EntityType type)
    {
        Random rand = new Random();
        boolean validNum = false;
        int messageNum;
        do {
            messageNum = rand.nextInt(5);

            if(messageNum > 0 && messageNum <= 4)
            {
                validNum = true;
            }

        } while(!validNum);

        if(type == EntityType.CLASSMATE)
        {
            String message = "Message" + Integer.toString(messageNum);
            System.out.println(message);
            return assetManager.getBitmap(message);
        }
        else if(type == EntityType.PROFESSOR)
        {
            String message = "Advice" + Integer.toString(messageNum);
            System.out.println(message);
            return assetManager.getBitmap(message);
        }
        return  null;
    }

    /**
     * Draws out the Tile Map
     *
     * @param elapsedTime - Elapsed time information for the frame
     * @param graphics2D - Graphics instance used to draw the screen
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        tileMap.drawTileMap(elapsedTime, graphics2D, layerViewport, screenViewport);

        player.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

        if(playerCollided && collidedEntity instanceof NPC){
            graphics2D.drawBitmap(advice, null, popUpRect, null);
            okButton.draw(elapsedTime, graphics2D, screenViewport);
            popupDisplayed = true;
        }
        else if ( playerCollided && collidedEntity instanceof Enemy)
        {
            graphics2D.drawBitmap(AssetLoading.battlePopUp, null, popUpRect, null);
            yesButton.draw(elapsedTime, graphics2D, screenViewport);
            noButton.draw(elapsedTime, graphics2D, screenViewport);
            popupDisplayed = true;
        }


        for (Entity e : entities) {
            e.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }

        for (SimpleControl simpleControl : simpleControls) {
            simpleControl.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
        }


    }

    //Getters and setters

    public ArrayList<Entity> getEntities(){
        return entities;
    }

    public TileMapPlayer getPlayer() {
        return player;
    }

    public void setPlayer(TileMapPlayer player) {
        this.player = player;
    }

    public LayerViewport getLayerViewport(){ return layerViewport; }

    /**
     * Checks that new NPCs don't collide with existing NPCs
     *
     * @param NPCs - An array list of already created NPCs
     */
//    private void doesntCollide(ArrayList<NPC> NPCs)
//    {
//        for(NPC npc : NPCs)
//        {
//            if(position.x != npc.position.x && position.y != npc.position.y)
//            {
//                NPCcollide = true;
//            }
//        }
//        NPCcollide = false;
//    }
}
