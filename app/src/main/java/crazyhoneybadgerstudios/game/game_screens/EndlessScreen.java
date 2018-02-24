package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
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
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.NPC;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.PerlinMap;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.TileMapPlayer;
import crazyhoneybadgerstudios.game.Game_Elements.TileMapElements.TiledMovement;
import crazyhoneybadgerstudios.ui.ScreenViewportReleaseButton;
import crazyhoneybadgerstudios.util.CollisionDetector;
import crazyhoneybadgerstudios.util.MathHelper;
import crazyhoneybadgerstudios.util.RNG;
import crazyhoneybadgerstudios.util.SimpleControl;
import crazyhoneybadgerstudios.util.Vector2;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;


/**
 * Created by Dariusz Jerzewski on 07/05/2017.
 * a variant of game screen enabling perlin generated maps
 */

public class EndlessScreen extends GameScreen{
    private TileMapPlayer player; //player object

    private ScreenViewportReleaseButton yesButton;
    private ScreenViewportReleaseButton noButton;
    private ScreenViewportReleaseButton okButton;
    //screen properties
    private LayerViewport layerViewport = new LayerViewport();

    private final float layerViewportWidth = 700.0f;
    private final float layerViewportHeight = 520.0f;

    //Rectangle to draw pop ups
    private Rect popUpRect;

    //Bitmap to draw appropriate NPC advice
    private Bitmap advice;

    private final int maxEntities = 5; //max amount of entities on screen

    private boolean playerCollided = false;
    private boolean popupDisplayed = false;
    private Entity collidedEntity = null;

    //perlinmap for getting map chunks
    private PerlinMap perlinMap;

    //Touch controls for player input
    private SimpleControl moveLeft;
    private SimpleControl moveRight;
    private SimpleControl moveUp;
    private SimpleControl moveDown;
    private List<SimpleControl> simpleControls = new ArrayList<>();

    private ArrayList<Entity> entities = new ArrayList<>(); //nearby entities

    /**
     * constructor initialising new map
     * @param game
     */
    public EndlessScreen(Game game){
        super("Endless Screen", game);
        layerViewport.set(0,0, layerViewportWidth/2, layerViewportHeight/2);

        player = new TileMapPlayer(Direction.UP, 300.5f,300.5f,32f,32f, AssetLoading.player,this, EntityType.PLAYER);
        player.setMovement(new TiledMovement(player));
        perlinMap = new PerlinMap("random seeed here", 24, 12, 32f,32f,this);
        perlinMap.getNewPosition(player, null);

        layerViewport.set(player.position.x, player.position.y, layerViewport.halfWidth, layerViewport.halfHeight);
        setUpScreenViewPort();
        popUpRect = new Rect(screenViewport.left + screenViewport.width/4, screenViewport.bottom/4,
                screenViewport.right - screenViewport.width/4, screenViewport.bottom/4 * 3);

        setUpArrows();
        setUpButtons();
    }

    /**
     * handling touch events performed by player
     * @param touchEvent
     */
    private void handleTouchEvent(TouchEvent touchEvent){
        Vector2 lPos = new Vector2(touchEvent.x, touchEvent.y);
        //InputHelper.convertScreenPosIntoLayer(screenViewport,new Vector2(touchEvent.x, touchEvent.y), layerViewport,lPos);
        if(moveUp.getBound().contains(lPos.x, lPos.y)){
            if(perlinMap.isCollision(player, new Vector2(player.position.x, player.position.y + 32f)))
                return;
            if(blockMov(Direction.UP))
                return;
            player.getMovement().start();
            player.getMovement().move(Direction.UP);
            player.setDirection(Direction.UP);
            perlinMap.needsUpdate();
        }
        else if(moveRight.getBound().contains(lPos.x, lPos.y)){
            if(perlinMap.isCollision(player, new Vector2(player.position.x +32f, player.position.y )))
                return;
            if(blockMov(Direction.RIGHT))
                return;
            player.getMovement().start();
            player.getMovement().move(Direction.RIGHT);
            player.setDirection(Direction.RIGHT);
            perlinMap.needsUpdate();
        }
        else if(moveDown.getBound().contains(lPos.x, lPos.y)){
            if(perlinMap.isCollision(player, new Vector2(player.position.x, player.position.y - 32f)))
                return;
            if(blockMov(Direction.DOWN))
                return;
            player.getMovement().start();
            player.getMovement().move(Direction.DOWN);
            player.setDirection(Direction.DOWN);
            perlinMap.needsUpdate();
        }
        else if(moveLeft.getBound().contains(lPos.x, lPos.y)){
            if(perlinMap.isCollision(player, new Vector2(player.position.x- 32f, player.position.y )))
                return;
            if(blockMov(Direction.LEFT))
                return;
            player.getMovement().start();
            player.getMovement().move(Direction.LEFT);
            player.setDirection(Direction.LEFT);
            perlinMap.needsUpdate();
        }
        player.getMovement().stop();
    }

    /**
     * update the screen
     * @param elapsedTime
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        perlinMap.update(elapsedTime);

        //Update the player
        Input input = mGame.getInput();
        for (TouchEvent e : input.getTouchEvents()) {
            switch (e.type) {
                case TouchEvent.TOUCH_DOWN: {
                    handleTouchEvent(e);

                    RNG rng = new RNG();
                    float val = Math.abs(MathHelper.linearConvertion(rng.nextLong()));
                    Log.d("val", val + "");
                    if(val < 0.05f){
                        spawnEntity();
                    }
                    break;
                }
            }
        }
        player.update(elapsedTime);

        for(Entity e : entities){
            if(CollisionDetector.isCollision(player.getBound(), e.getBound())){
                player.getMovement().stop();
                playerCollided = true;
                collidedEntity = e;

            }
        }
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
                entities.remove(collidedEntity);
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
                entities.remove(collidedEntity);
                collidedEntity = null;
                playerCollided = false;
            }
        }


        for(Iterator<Entity> i = entities.iterator(); i.hasNext();){
            Entity e = i.next();
            if(Math.abs(e.position.x) > Math.abs(player.position.x + (9f * 32f)) || Math.abs(e.position.y) > Math.abs(player.position.y + (9f * 32f))) {
                Log.d("entityR", e.position.toString() + "   " + Math.abs(player.position.x + (9f * 32f)) + "  " + Math.abs(player.position.y + (9f * 32f)));
                i.remove();
            }
        }
    }
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

    /**
     * method to create new entity in a location nearby player
     */
    private void spawnEntity(){

        Log.d("spawn", "entity");

        if(entities.size() >= maxEntities) return;

        RNG rng = new RNG();
        float val = MathHelper.linearConvertion(rng.nextLong());

        Random random = new Random(System.nanoTime());

        int spawnRadius = 4;

        Vector2 pos = new Vector2(player.position);
        pos.add(((random.nextInt(spawnRadius) - spawnRadius/2) + 1) *32f, ((random.nextInt(spawnRadius) - spawnRadius/2) + 1)*32f);

        Entity newEntity = null;
        if(val > 0.6){
            newEntity = new Enemy(Direction.UP, pos.x, pos.y, 32f, 32f, AssetLoading.enemy, this, EntityType.ENEMY);
        }
        else if(val > 0.3){
            newEntity = new NPC(Direction.UP, pos.x, pos.y, 32, 32, (val > 0.45)?AssetLoading.npcgirl : AssetLoading.npcboy, this, EntityType.CLASSMATE);
        }
        else{
            newEntity = new NPC(Direction.UP, pos.x, pos.y, 32, 32, AssetLoading.professor , this, EntityType.PROFESSOR);
        }
        perlinMap.getNewPosition(newEntity, new Vector2(player.position));
        if(newEntity != null){
            perlinMap.getNewPosition(newEntity,player.position);
            entities.add(newEntity);
        }
    }
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

    private boolean blockMov(Direction direction){
        if(player.position.y < 50.5f &&  direction == Direction.DOWN )
            return true;
        else if(player.position.x < 50.5f &&  direction == Direction.LEFT )
            return true;

        return false;
    }


    //getters
    public LayerViewport getLayerViewport(){
        return layerViewport;
    }
    public TileMapPlayer getPlayer(){return player;}

    /**
     * draw screen
     * @param elapsedTime
     *            Elapsed time information for the frame
     * @param graphics2D
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        perlinMap.draw(elapsedTime,graphics2D,layerViewport,screenViewport);

        for(Entity e: entities){
            e.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
        }

        player.draw(elapsedTime,graphics2D,layerViewport,screenViewport);

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
        for(SimpleControl sc : simpleControls)
            sc.draw(elapsedTime,graphics2D,layerViewport,screenViewport);
    }
}
