package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Dariusz Jerzewski
 */
public class Card extends GameObject
{
    //denotes if the card is of type Unimon Card or School Card
    public enum TypeOfCard
    {
        SCHOOL, UNIMON
    }

    //necessary parameters for card to be used in the game
    protected String name;
    protected School school;
    protected TypeOfCard type;
    // denotes if the player has long pressed this card
    protected boolean selected = false;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// CONSTRUCTORS //////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor used to set up the card.
     * @param gameScreen gamescreen on which this card exists
     */
    public Card(GameScreen gameScreen)
    {
        super(gameScreen);
        setBitmap(gameScreen.getGame().getAssetManager().getBitmap(name));
    }



    /**
     * @author Courtney Shek
     *
     * Constructor used for testing purposes.
     * See CardTest class
     *
     * @param gameScreen - Game Screen the card belongs to
     * @param school - School the card belongs to
     */
    public Card(GameScreen gameScreen, School school)
    {
        super(gameScreen);
        this.school = school;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// GENERAL GETTERS/SETTERS ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public String getName()
    {
        return this.name;
    }

    public Bitmap getCardImage()
    {
        return mBitmap;
    }

    public TypeOfCard getType()
    {
        return type;
    }

    public School getSchool()
    {
        return school;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// UTILITY METHODS ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Checks what school type this card counters
     * @return if evolved, returns what this school counters, else null
     * @author Chloe McMullan
     */
    public School thisSchoolCounters()
    {
        School counter = null;
        switch (school)
        {
            case STEM:
                counter = School.MEDICAL;
                break;
            case HUMANITARIAN:
                counter = School.STEM;
                break;
            case MEDICAL:
                counter = School.HUMANITARIAN;
                break;
        }
        return counter;
    }

    /**
     * Returns what school this card is weak to
     * @return if none, returns null. Else, returns what this is weak to
     * @author Chloe McMullan
     */
    public School thisSchoolIsWeakTo()
    {
        School weakness = null;
        switch (school)
        {
            case STEM:
                weakness = School.HUMANITARIAN;
                break;
            case HUMANITARIAN:
                weakness = School.MEDICAL;
                break;
            case MEDICAL:
                weakness = School.STEM;
                break;
        }
        return weakness;
    }

    /**
     * @author Michael Grundie.
     * Increases card image by 10 pixels for when long pressed
     */
    public void select()
    {
        if(!selected)
        {

            mBound.halfHeight += 10;
            mBound.halfWidth += 10;
            selected = true;

        }
    }

    /**
     * @author Michael Grundie.
     *
     * Decreases card image by 10 pixels for when no longer selected
     */
    public void deselect()
    {
        if(selected)
        {

            mBound.halfHeight -= 10;
            mBound.halfWidth -= 10;
            selected = false;

        }
    }
}
