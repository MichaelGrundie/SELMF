package crazyhoneybadgerstudios.selmf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import crazyhoneybadgerstudios.game.Game_Elements.AI.AIMove;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonMove;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;

import static junit.framework.Assert.assertEquals;

/**
 * Testing of AIMove
 */

@RunWith(MockitoJUnitRunner.class)
public class AIMoveTest {

    //The objects used to carry out tests
    private ArrayList<AIMove> moves = new ArrayList<>(3);
    /**
     * Creates mock objects
     */
    @Mock
    private UnimonCard activeCard = Mockito.mock(UnimonCard.class);
    @Mock
    private UnimonCard secondCard = Mockito.mock(UnimonCard.class);
    @Mock
    private SchoolCard buffingCard = Mockito.mock(SchoolCard.class);
    @Mock
    private CardContainer cardContainer = Mockito.mock(CardContainer.class);
    @Mock
    private UnimonMove unimonAttack = Mockito.mock(UnimonMove.class);


    /**
     * Tests that the AIMoves are initialised to the correct type of move
     */
    @Test
    public void initialisingAIMoves_TestSuccess() {
        moves.add(new AIMove(activeCard)); //rerolling
        moves.add(new AIMove(unimonAttack)); //attacking
        moves.add(new AIMove(AIMove.TypeOfMove.SWAP_ACTIVE, activeCard, secondCard)); //swapping
        moves.add(new AIMove(AIMove.TypeOfMove.ATTACH, buffingCard, activeCard)); //buffing
        moves.add(new AIMove(activeCard, PlayArea.HAND, PlayArea.BENCH)); //moving

        assertEquals(moves.get(0).getTom(), AIMove.TypeOfMove.REROLL);
        assertEquals(moves.get(1).getTom(), AIMove.TypeOfMove.ATTACK);
        assertEquals(moves.get(2).getTom(), AIMove.TypeOfMove.SWAP_ACTIVE);
        assertEquals(moves.get(3).getTom(), AIMove.TypeOfMove.ATTACH);
        assertEquals(moves.get(4).getTom(), AIMove.TypeOfMove.MOVE_CARD);
    }


}
