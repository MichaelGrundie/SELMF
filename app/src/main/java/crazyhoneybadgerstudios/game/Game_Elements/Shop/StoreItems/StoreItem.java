package crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.game.game_screens.AccessoriesShop;

/**
 * @author Jodie Burnside - 4/23/2017.
 */

public class StoreItem
{
    ItemTypesENUM.itemTypes itemName; //Name of the item
    int cost; //Cost in gold coins
    int numberInStock; //Description of the item
    Bitmap itemSaleImage; //Image of the item when on sale
    Bitmap itemOUTOFSTOCK; //Image for the item when out of stock
    Bitmap equipImage;

    public StoreItem(ItemTypesENUM.itemTypes item, int cost, int stock, Bitmap saleImage, Bitmap outOfStock, Bitmap equipImage)
    {
        itemName = item;
        this.cost = cost;
        numberInStock = stock;
        itemSaleImage = saleImage;
        itemOUTOFSTOCK = outOfStock;
        this.equipImage = equipImage;

    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ItemTypesENUM.itemTypes getItemName() {
        return itemName;
    }

    public void setItemName(ItemTypesENUM.itemTypes itemName) {
        this.itemName = itemName;
    }

    public int getNumberInStock() { return numberInStock; }

    public void setNumberInStock(int numberInStock) {
        this.numberInStock = numberInStock;
    }

    public void setItemImage(Bitmap itemImage) {
        this.itemSaleImage = itemImage;
    }

    public Bitmap getItemSaleImage() { return itemSaleImage; }

    public void setItemSaleImage(Bitmap itemSaleImage) { this.itemSaleImage = itemSaleImage; }

    public Bitmap getItemOUTOFSTOCK() { return itemOUTOFSTOCK; }

    public void setItemOUTOFSTOCK(Bitmap itemOUTOFSTOCK) { this.itemOUTOFSTOCK = itemOUTOFSTOCK; }

    public Bitmap getEquipImage() { return equipImage; }


}
