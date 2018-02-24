package crazyhoneybadgerstudios.selmf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.Player;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

/**
 * @Author Jodie Burnside (40150039) on 05/01/2017.
 * Testing the Player class
 */

@RunWith(MockitoJUnitRunner.class)
public class PlayerTest
{
    private CardContainer cardContainer;

    /**
     * Creates a mock Player object and card object (to fill player card containers)
     */
    @Mock
    private Player player = Mockito.mock(Player.class);

    @Mock
    private Card card = Mockito.mock(Card.class);

    @Mock
    private BattleScreen battleScreen = Mockito.mock(BattleScreen.class);

    @Before
    public void setUp()
    {
        player = new Player("TestPlayer", 50, battleScreen);
        cardContainer = new CardContainer(1);

        //Fill players card containers with test cards
        for (int i = 0; i < player.getPlayerDeck().getCardsSize(); i++)
        {
            player.getPlayerDeck().addCard(card);
        }
        for (int i = 0; i < player.getPlayerBench().getCardsSize(); i++)
        {
            player.getPlayerBench().addCard(card);
        }
        for (int i = 0; i < player.getPlayerActiveContainer().getCardsSize(); i++)
        {
            player.getPlayerActiveContainer().addCard(card);
        }

    }

    /**
     * Tests that the fill hand method fills the players hand correctly
     * Also tests the school card limit and move works as expected
     * Uses valid data
     */

    @Test
    public void fillHand_TestSuccess()
    {
        int schoolCardCount = 0;
        int handCardCount = 0;
        int deckCardCount = 0;
        player.fillHand(10);

            for (Card card : player.getPlayerHand().getAllCards())
            {
                if (card.getType() == Card.TypeOfCard.SCHOOL)
                {
                    schoolCardCount++;
                }
            }
        Assert.assertTrue(schoolCardCount <= 10); //Assert that the school card limit is not exceeded

             for (Card card : player.getPlayerHand().getAllCards())
             {
                      handCardCount++;
             }
        Assert.assertTrue(handCardCount == player.getPlayerHand().getCardsSize()); //Assert that the hand is fully filled, all 5 spaces filled.

             for (Card card : player.getPlayerDeck().getAllCards())
             {
                     deckCardCount++;
             }
        Assert.assertTrue(deckCardCount < player.getPlayerDeck().getCardsSize()); //Assert that cards were removed from the playerDeck successfully

    }


}
