package crazyhoneybadgerstudios.game.Game_Elements.Shop.StoreItems;

import android.graphics.Bitmap;

/**
 * @Author: Jodie Burnside (40150039)
 */

public class Item
{
    private Bitmap equipImage;
    private ItemTypesENUM.itemTypes itemName;
    private boolean isEquipped;

    public Item(Bitmap equipImage, ItemTypesENUM.itemTypes itemName, boolean isEquipped)
    {
        this.equipImage = equipImage;
        this.itemName = itemName;
        this.isEquipped = isEquipped;
    }
}
