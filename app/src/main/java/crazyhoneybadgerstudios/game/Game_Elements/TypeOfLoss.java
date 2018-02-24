package crazyhoneybadgerstudios.game.Game_Elements;


/**
 * @author Michael Grundie.
 */

public enum TypeOfLoss {
    OUT_OF_CARDS("run out of cards"), OUT_OF_WITPOINTS("run out of witpoints"),

    NO_LOSS("no loser yet");

    private final String descriptionOfLoss;

    TypeOfLoss(String value)
    {
        descriptionOfLoss = value;
    }

    public String getDescriptionOfLoss()
    {
        return descriptionOfLoss;
    }
}
