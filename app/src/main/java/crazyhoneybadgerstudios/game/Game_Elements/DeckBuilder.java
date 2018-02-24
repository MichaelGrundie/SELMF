package crazyhoneybadgerstudios.game.Game_Elements;

import java.util.Random;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

/**
 * @author Michael Grundie
 *
 * Creates unimon from a text file containing stats.
 * Builds a deck of unimon cards and school cards
 */
public class DeckBuilder
{

    private Game game;
    private String[] unimonStats;
    private Unimon[] allUnimon;
    private int noOfDistinctUnimon;



    public DeckBuilder(Game game)
    {
        this.game = game;
        unimonStats = game.getAssetManager().getTxtFile("UnimonStats.txt");
        allUnimon = new Unimon[unimonStats.length-1];//First line of file has column names, it is skipped.
        createUnimonFromStats();
        noOfDistinctUnimon = allUnimon.length;
    }



    /**
     * @author Michael Grundie.
     *
     * Test Constructor
     * @param assetStore
     */
    public DeckBuilder(AssetStore assetStore)
    {
        game = null;
        unimonStats = assetStore.getTxtFile("UnimonStats.txt");
        allUnimon = new Unimon[unimonStats.length-1];//First line of file has column names, it is skipped.
        createUnimonFromStats();
        noOfDistinctUnimon = allUnimon.length;
    }



    public CardContainer getDeck(int noOfUnimon, int noOfSchoolCards, BattleScreen battleScreen)
    {
        CardContainer deck = new CardContainer(noOfUnimon+noOfSchoolCards);
        deck = addUnimonToDeck(deck, noOfUnimon, battleScreen);
        deck = addSchoolCardsToDeck(deck, noOfSchoolCards, battleScreen);
        deck.shuffleCards();

        return deck;
    }



    private void createUnimonFromStats()
    {
        //Starts at 1 to avoid column names line.
        for (int i=1; i<unimonStats.length; i++)
        {
            //Splits the string into an array of strings using a semicolon as the delimiter.
            Unimon unimon = new Unimon(unimonStats[i].split(";"));
            allUnimon[i-1] = unimon;
        }

    }



    public Unimon[] getAllUnimon() { return allUnimon; }



    /**
     * This method returns a deck with a specified number of
     * random Unimon from the pool, each unimon can only occur
     * twice in the return deck.
     * @return Randomised deck of 25 Unimon.
     */
    public CardContainer addUnimonToDeck(CardContainer deck, int noOfUnimon, BattleScreen battleScreen)
    {
        if(noOfUnimon > (2*noOfDistinctUnimon))
        {
            noOfUnimon = 2*noOfDistinctUnimon;
        }

        Random rand = new Random();
        int[] randomsUsed = new int[noOfUnimon];

        for(int i=0; i<noOfUnimon; i++)
        {

            boolean isValid = false;

            while (!isValid)
            {
                int usage = 0;

                //random number between 0 and number of distinct unimon (Parameter value is exclusive)
                int newRandom = rand.nextInt(allUnimon.length);

                for (int usedNumber: randomsUsed)
                {
                    if (newRandom == usedNumber)
                    {
                        usage++;
                    }

                }

                if (usage >= 2)
                {
                    isValid = false;
                }
                else
                {
                    isValid = true;
                    deck.addCard(new UnimonCard(battleScreen.getCardWidth(), battleScreen.getCardHeight(),
                            new Unimon(allUnimon[newRandom]), battleScreen));
                    randomsUsed[i] = newRandom;
                }
            }
        }
        return deck;
    }



    private CardContainer addSchoolCardsToDeck(CardContainer deck, int noOfSchoolCards,
                                               BattleScreen battleScreen)
    {
        Random rand = new Random();
        for(int i = 0; i<noOfSchoolCards; i++){

            int schoolInt = rand.nextInt(3)+1;
            School school;
            switch(schoolInt){
                case 1:
                    school = School.MEDICAL;
                    break;
                case 2:
                    school = School.STEM;
                    break;
                default:
                    school = School.HUMANITARIAN;
                    break;
            }

            deck.addCard(new SchoolCard(battleScreen.getCardWidth(), battleScreen.getCardHeight(),
                    battleScreen, school));
        }

        return deck;
    }
}
