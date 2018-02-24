package crazyhoneybadgerstudios.selmf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures.CardContainer;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Courtney Shek
 *
 * Testing the CardContainer class
 */

@RunWith(MockitoJUnitRunner.class)
public class CardContainerTest {

    //The container used to carry out tests
    private CardContainer cardContainer;

    /**
     * Creates a mock Card object
     */
    @Mock
    Card testCard = Mockito.mock(Card.class);

    /**
     * Creates a mock UnimonCard object
     */
    @Mock
    UnimonCard testUnimonCard = Mockito.mock(UnimonCard.class);

    /**
     * Creates a mock SchoolCard object
     */
    @Mock
    SchoolCard testSchoolCard = Mockito.mock(SchoolCard.class);

    /**
     * Before the tests run, sets up a new cardContainer object
     */
    @Before
    public void setUp() {
        cardContainer = new CardContainer(40);
    }

    /**
     * Tests that card will be added to a cardContainer successfully
     * Uses valid data
     */
    @Test
    public void addCard_TestSuccess() {
        cardContainer.addCard(testCard);
        String testCardName = "TestCard";
        when(testCard.getName()).thenReturn(testCardName);

        assertEquals(0, cardContainer.getCardIndex(testCardName));
    }

    /**
     * Tests that a cardContainer will only return unimon cards
     * Uses valid data
     */
    @Test
    public void returnOnlyUnimon_TestSuccess()
    {
        cardContainer.addCard(testUnimonCard);
        cardContainer.addCard(testSchoolCard);

        ArrayList<UnimonCard> tempArr = new ArrayList<>();
        tempArr.add(testUnimonCard);

        when(testUnimonCard.getType()).thenReturn(Card.TypeOfCard.UNIMON);
        when(testSchoolCard.getType()).thenReturn(Card.TypeOfCard.SCHOOL);

        assertEquals(tempArr, cardContainer.returnOnlyUnimon());
    }

    /**
     * Tests that a cardContainer will only return school cards
     * Uses valid data
     */
    @Test
    public void returnOnlySchool_TestSuccess()
    {
        cardContainer.addCard(testUnimonCard);
        cardContainer.addCard(testSchoolCard);

        ArrayList<SchoolCard> tempArr = new ArrayList<>();
        tempArr.add(testSchoolCard);

        when(testUnimonCard.getType()).thenReturn(Card.TypeOfCard.UNIMON);
        when(testSchoolCard.getType()).thenReturn(Card.TypeOfCard.SCHOOL);

        assertEquals(tempArr, cardContainer.returnOnlySchool());
    }

    /**
     * Tests that the cardContainer will return and remove the first card in the container
     * Uses valid data
     */
    @Test
    public void getAndRemoveTopCard_TestSuccess()
    {
        cardContainer.addCard(testCard);
        cardContainer.addCard(testUnimonCard);
        cardContainer.addCard(testSchoolCard);

        assertEquals(testCard, cardContainer.getAndRemoveTopCard());
    }

    /**
     * Tests that a card is removed from a cardContainer successfully
     * Uses valid data
     */
    @Test
    public void removeCard_TestSuccess()
    {
        cardContainer.addCard(testCard);
        cardContainer.addCard(testUnimonCard);
        cardContainer.addCard(testSchoolCard);

        String testName = "UnimonCard";
        when(testUnimonCard.getName()).thenReturn(testName);
        when(testCard.getName()).thenReturn("Test");
        when(testSchoolCard.getName()).thenReturn("Test2");
        cardContainer.removeCard(testUnimonCard);

        assertEquals(-1, cardContainer.getCardIndex(testUnimonCard.getName()));
    }

    /**
     * Tests that the cardContainer returns the right number of spaces occupied by cards
     * Uses valid data
     */
    @Test
    public void getHowManyCards_TestSuccess()
    {
        cardContainer.addCard(testUnimonCard);
        cardContainer.addCard(testSchoolCard);

        assertEquals(2, cardContainer.getHowManyCards());
    }

}
