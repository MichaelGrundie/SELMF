package crazyhoneybadgerstudios.selmf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Courtney on 09/04/2017.
 * Testing the Card class
 */

@RunWith(MockitoJUnitRunner.class)
public class CardTest {

    //Test card used to carry out tests
    Card testCard = new Card(null, School.HUMANITARIAN);

    /**
     * Tests that the card will return its counter School successfully
     * Uses valid data
     */
    @Test
    public void getSchoolCounter_TestSuccess()
    {
       assertEquals(School.STEM, testCard.thisSchoolCounters());
    }

    /**
     * Tests that the card will return its School weakness sucessfully
     * Uses valid data
     */
    @Test
    public void getSchoolWeakness_TestSuccess()
    {
        assertEquals(School.MEDICAL, testCard.thisSchoolIsWeakTo());
    }

}
