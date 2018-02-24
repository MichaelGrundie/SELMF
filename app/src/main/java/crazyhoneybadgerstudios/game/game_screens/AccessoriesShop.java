package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Rect;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.input.TouchEvent;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems.Item;
import crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems.ItemTypesENUM;
import crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems.StoreItem;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Jodie Burnside - 4/23/2017.
 *INCOMPLETE - The accessory store additional functionality is not complete, and does not function without errors.
 *Unable to debug and finish in time for submission, current code state included in project as code but is not run from within the game.
 *Intended usage - To draw accessories on top of the users customised character on the tile map screen.
 */


public class AccessoriesShop extends GameScreen
{
    private StoreItem[] storeItemsInventory = new StoreItem[3];
    private Rect itemOne;
    private Rect itemTwo;
    private Rect itemThree;
    private Rect backgroundRect;
    private Rect popUpRect;
    private boolean purchasePopUp;
    private boolean outOfStockPopUp;
    private boolean notEnoughCoinPopUp;

    public AccessoriesShop(Game mGame)
    {
        super("Accessories Shop", mGame);
        AssetLoading.loadAccessoriesShop(mGame.getAssetManager());
        setUpScreenViewPort();
        setUpRectPositions();
        storeItemsInventory[0] =  new StoreItem(ItemTypesENUM.itemTypes.WIZARDS_STAFF, 10, 1, AssetLoading.wizardsStaffSALE, AssetLoading.wizardsStaffOUTOFSTOCK, AssetLoading.wizardsStaff);
        storeItemsInventory[1] =  new StoreItem(ItemTypesENUM.itemTypes.NECKLACE, 2, 1, AssetLoading.necklaceSALE, AssetLoading.necklaceOUTOFSTOCK, AssetLoading.necklace);
        storeItemsInventory[2] =  new StoreItem(ItemTypesENUM.itemTypes.HAIR_FLOWER, 10, 1, AssetLoading.hairFlowerSALE, AssetLoading.hairFlowerOUTOFSTOCK, AssetLoading.hairFlower);
        purchasePopUp = false;
        outOfStockPopUp = false;
        notEnoughCoinPopUp = false;

    }

    public void update(ElapsedTime elapsedTime)
    {
        Input input = mGame.getInput();
        for(TouchEvent event : input.getTouchEvents())
        {
            if(event.type == TouchEvent.TOUCH_DOWN)
            {
                if (itemOne.contains((int) event.x, (int) event.y))
                {
                    takeAppropriateClickAction(0);
                }
                else if (itemTwo.contains((int) event.x, (int) event.y))
                {
                    takeAppropriateClickAction(1);
                }
                else if (itemThree.contains((int) event.x, (int) event.y))
                {
                    takeAppropriateClickAction(2);
                }
                else if (popUpRect.contains((int) event.x, (int) event.y))
                {
                    purchasePopUp = notEnoughCoinPopUp = outOfStockPopUp = false;
                }

            }
        }
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D)
    {
        graphics2D.drawBitmap(AssetLoading.stallBackground, null, backgroundRect, null);

        drawAppropriateSaleImage(0, graphics2D, itemOne);
        drawAppropriateSaleImage(1, graphics2D, itemTwo);
        drawAppropriateSaleImage(2, graphics2D, itemThree);

        if (purchasePopUp)
        {
            graphics2D.drawBitmap(AssetLoading.buyPopUp, null, popUpRect, null);
        }
        else if (notEnoughCoinPopUp)
        {
            graphics2D.drawBitmap(AssetLoading.coinPopUp, null, popUpRect, null);
        }
        else if (outOfStockPopUp)
        {
            graphics2D.drawBitmap(AssetLoading.stockPopUp, null, popUpRect, null);
        }


    }

    //@author: Jodie Burnside
    private void takeAppropriateClickAction(int itemNum)
    {
        if(storeItemsInventory[itemNum].getNumberInStock() != 0) //if in stock, click will buy the item.
        {
            if (mGame.getPlayerProfile().getGoldCoins() < storeItemsInventory[itemNum].getCost())
            {
                notEnoughCoinPopUp = true;
                Log.d("Shop: ", "NOT ENOUGH COIN!");
            }
            else
            {
                purchasePopUp = true;
                storeItemsInventory[itemNum].setNumberInStock((storeItemsInventory[itemNum].getNumberInStock()) - 1); //decrement the stock number
                mGame.getPlayerProfile().setGoldCoins(mGame.getPlayerProfile().getGoldCoins() - storeItemsInventory[itemNum].getCost()); //Take gold cost off playermGame.getPlayerProfile().getInventory().add(new Item())
                mGame.getPlayerProfile().getInventory().add(
                        new Item(storeItemsInventory[itemNum].getEquipImage(), storeItemsInventory[itemNum].getItemName(), false)); //Add pruchased item to inventory
                Log.d("Shop: ", "ITEM PURCHASED!");
            }
        }
        else
        {
            outOfStockPopUp = true;
            Log.d("Shop: ", "OUT OF STOCK!");
        }
    }

// @Author Jodie Burnside
    private void drawAppropriateSaleImage(int itemSlot, IGraphics2D graphics2D, Rect drawSlot)
    {
        if (storeItemsInventory[itemSlot].getNumberInStock() != 0)
        {
            graphics2D.drawBitmap(storeItemsInventory[itemSlot].getItemSaleImage(), null, drawSlot, null);
        }
        else
        {
            graphics2D.drawBitmap(storeItemsInventory[itemSlot].getItemOUTOFSTOCK(), null, drawSlot, null);
        }
    }


    //@author Jodie Burnside (40150039) - rescales to different screen sizes
    private void setUpRectPositions()
    {
        backgroundRect =  new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom);

        itemOne = new Rect
                (screenViewport.width / 10,
                        screenViewport.height / 3,
                        screenViewport.width/3,
                        screenViewport.height - mGame.getScreenHeight() / 3);

        itemTwo = new Rect
                (screenViewport.width / 10 + screenViewport.width / 3,
                        screenViewport.height / 3,
                        screenViewport.width/3 +  screenViewport.width / 3,
                        screenViewport.height - screenViewport.height / 3);

        itemThree =  new Rect
                (screenViewport.width / 10 + (screenViewport.width / 3) * 2,
                        screenViewport.height / 3,
                        screenViewport.width/3 +  (screenViewport.width / 3) * 2,
                        screenViewport.height - screenViewport.height / 3);

        popUpRect = new Rect
                (screenViewport.width / 10 + screenViewport.width / 3,
                screenViewport.height / 3,
                screenViewport.width/3 +  screenViewport.width / 3,
                screenViewport.height - mGame.getScreenHeight() / 4);

    }


}
