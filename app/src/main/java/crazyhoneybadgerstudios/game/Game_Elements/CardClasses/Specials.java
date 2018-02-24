package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;

/**
 * @author Jodie Burnside - 23/11/2016.
 */

public class Specials {

    public enum TypeOfSpecial{
        ATTACK, HEALTH, DEFENSE;
    }

    private TypeOfSpecial type;
    private int value;
    private int coolDown; //Number of turns

    //Constructor
    public Specials(TypeOfSpecial typeOfSpecial, int val, int coolDownNum)
    {
        type = typeOfSpecial; //TypeOfSpecial ENUM
        value = val;
        coolDown = coolDownNum;
    }
    //Getters/Setters

    public TypeOfSpecial getType()
    {
        return type;
    }

    public  void setType(TypeOfSpecial specialType)
    {
        type = specialType;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int val)
    {
        value = val;
    }

    public int getCoolDown()
    {
        return coolDown;
    }

    public void setCoolDown(int coolDownVal)
    {
        coolDown = coolDownVal;
    }


}
