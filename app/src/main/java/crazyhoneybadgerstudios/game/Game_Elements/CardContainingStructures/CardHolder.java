package crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures;

import android.graphics.Paint;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Dariusz Jerzewski
 */

public class CardHolder extends GameObject
{
    //variables to store card and the are this object belongs to
    private Card card = null;
    private PlayArea slotType;

    /**
     * make a new cardholder
     * @param x
     * @param y
     * @param battleScreen
     * @param slotType
     */
    public CardHolder(float x, float y, BattleScreen battleScreen, PlayArea slotType)
    {
        super(x, y, battleScreen.getCardWidth(), battleScreen.getCardHeight(), null, battleScreen);
        mBitmap = battleScreen.getGame().getAssetManager().getBitmap("CARDHOLDER");
        this.slotType = slotType;
    }

    /**
     * @author Michael Grundie.
     * @return
     */
    public PlayArea getSlotType(){
        return slotType;
    }

    /**
     * get card strored by the holder
     */
    public Card getCard()
    {
        return card == null? null: card;
    }


    /**
     * set card and update its position if its not null
     */
    public void setCard(Card c)
    {
        card = c;
        if(c != null)
            c.setPosition(position.x, position.y);
    }
    /**
     *  assign new card without changing its position
     */
    public void setCardWithoutChangingPosition(Card c)
    {
        card = c;
    }

    /**
     * set card to null
     */
    public void setNull(){card=null;}

    /**
     * draw cardholder to screen
     * @param elapsedTime
     *            Elapsed time information
     * @param graphics2D
     *            Graphics instance
     * @param layerViewport
     *            Game layer viewport
     * @param screenViewport
     *            Screen viewport
     * @param paint
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint)
    {
        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport, paint);
    }
}
