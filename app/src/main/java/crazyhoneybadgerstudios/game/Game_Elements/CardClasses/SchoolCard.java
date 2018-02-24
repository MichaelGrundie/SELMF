package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;

import android.graphics.Paint;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;


/**
 * @author Michael Grundie.
 */
public class SchoolCard extends Card {


    public SchoolCard (float width, float height, GameScreen gameScreen, School school)
    {
        super(gameScreen);
        mBound.halfWidth = width/2;
        mBound.halfHeight = height/2;
        type = TypeOfCard.SCHOOL;
        super.school = school;
        AssetStore assetManager = gameScreen.getGame().getAssetManager();
        switch(school)
        {
            case MEDICAL: name = "MEDICAL";
                mBitmap = assetManager.getBitmap("MEDICALCARD");
                break;
            case HUMANITARIAN: name = "HUMANITARIAN";
                mBitmap = assetManager.getBitmap("HUMANITARIANCARD");
                break;
            case STEM: name = "STEM";
                mBitmap = assetManager.getBitmap("STEMCARD");
                break;
            default: name = "BASIC";
                break;
        }

    }

    /**
     * Constructor for testing purposed
     * @param gameScreen screen this will be drawn on, can be mocked
     * @param school school the card will belong to
     */
    public SchoolCard(GameScreen gameScreen, School school) {
        super(gameScreen, school);
        this.school = school;
    }


    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport,
                     ScreenViewport screenViewport, Paint paint) {

        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport, paint);
    }
}
