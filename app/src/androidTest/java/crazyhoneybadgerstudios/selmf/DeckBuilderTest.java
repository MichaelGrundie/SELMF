package crazyhoneybadgerstudios.selmf;

import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.MainActivity;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.io.FileIO;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.DeckBuilder;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Michael Grundie.
 */

@RunWith(AndroidJUnit4.class)
public class DeckBuilderTest
{
    private Context context;
    private DeckBuilder deckBuilder;
    MainActivity activity;

    @Before
    public void setUp()
    {
        context = InstrumentationRegistry.getTargetContext();
    }

    /**
     * @author Michael Grundie
     *
     * Tests that the DeckBuilder creates all uimon in the text file.
     */
    @Test
    public void getAllUnimonTest()
    {
        AssetStore assetStore = new AssetStore(new FileIO(context));
        assetStore.loadAndAddTxtFile("UnimonStats.txt", "UnimonStats.txt");
        deckBuilder = new DeckBuilder(assetStore);

        Unimon[] unimons = deckBuilder.getAllUnimon();

        assertEquals(unimons.length, 25);
    }

    /**
     * @author Michael Grundie
     *
     * Tests the the DeckBuilder creates the right total number of cards.
     */
    @Test
    public void getUnimonCardsTest()
    {
        AssetStore assetStore = new AssetStore(new FileIO(context));
        assetStore.loadAndAddTxtFile("UnimonStats.txt", "UnimonStats.txt");
        deckBuilder = new DeckBuilder(assetStore);

        for(Unimon unimon: deckBuilder.getAllUnimon())
        {
            String unimonPicFile = "img/unimonImages/" + unimon.getName()+".png";
            assetStore.loadAndAddBitmap(unimon.getName(), unimonPicFile);
        }


        Game newGame = new Game();
        newGame.onCreate(new Bundle(),assetStore);

        AssetLoading.loadBattleScreen(newGame.getAssetManager());

        CardContainer unimoncards = deckBuilder.getDeck(25,30,new BattleScreen(newGame));

        assertTrue(unimoncards.getAllCards().size() == 55);
    }

    /**
     * @author Michael Grundie.
     *
     * Test that the DeckBuilder actually creates the right amount of each type of card.
     */
    @Test
    public void rightNumberOfUnimonCardsAndSchoolCardsTest()
    {
        AssetStore assetStore = new AssetStore(new FileIO(context));
        assetStore.loadAndAddTxtFile("UnimonStats.txt", "UnimonStats.txt");
        deckBuilder = new DeckBuilder(assetStore);

        for(Unimon unimon: deckBuilder.getAllUnimon())
        {
            String unimonPicFile = "img/unimonImages/" + unimon.getName()+".png";
            assetStore.loadAndAddBitmap(unimon.getName(), unimonPicFile);
        }


        Game newGame = new Game();
        newGame.onCreate(new Bundle(),assetStore);

        AssetLoading.loadBattleScreen(newGame.getAssetManager());

        CardContainer unimoncards = deckBuilder.getDeck(25,30,new BattleScreen(newGame));

        int unimonCount = 0;
        int schoolCount = 0;
        for(Card card: unimoncards.getAllCards())
        {
            if( card.getType()== Card.TypeOfCard.SCHOOL) schoolCount++;
            if(card.getType() == Card.TypeOfCard.UNIMON) unimonCount++;
        }

        assertEquals(unimonCount,25);
        assertEquals(schoolCount,30);
    }
}
