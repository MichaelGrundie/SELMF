package crazyhoneybadgerstudios.selmf;

import org.junit.Test;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.DeckBuilder;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void deckCreated() throws Exception{

        Game game = new Game();

        DeckBuilder deckBuilder = new DeckBuilder(game);

        CardContainer cards = deckBuilder.getDeck(20,20,null);

        int test = 0;

        for(Card card: cards.getAllCards())
        {
            test++;
        }

        assertEquals(40, test);
    }
}