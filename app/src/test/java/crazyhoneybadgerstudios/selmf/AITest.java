package crazyhoneybadgerstudios.selmf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import crazyhoneybadgerstudios.game.Game_Elements.AI.AI;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Specials;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Chloe.
 * Testing the Board class
 */

@RunWith(MockitoJUnitRunner.class)
public class AITest {

    //The objects used to carry out tests
    private UnimonCard u1;
    private UnimonCard u2;
    private SchoolCard hum;
    private SchoolCard stem;
    private SchoolCard med;


    /**
     * Creates a mock objects
     */
    @Mock
    private UnimonCard activeCard = Mockito.mock(UnimonCard.class);
    @Mock
    private BattleScreen bs = Mockito.mock(BattleScreen.class);

    /**
     * Sets up objects used for tests
     */
    @Before
    public void setUp()
    {
        u1 = new UnimonCard(new Unimon
                (new String[]{"u1", "100", "10", "ATTACK", "20", "0", "DEFENSE", "10", "0",
                        "HEALTH", "30", "2"}), bs, School.BASIC);

        u2 = new UnimonCard(new Unimon
                (new String[]{"u2", "80", "5", "ATTACK", "10", "0", "DEFENSE", "5", "0",
                        "HEALTH", "10", "2"}), bs, School.BASIC);

        hum = new SchoolCard(bs, School.HUMANITARIAN);
        stem = new SchoolCard(bs, School.STEM);
        med = new SchoolCard(bs, School.MEDICAL);

    }

    /**
     * Tests that my algorithm is returning what I expected it to return.
     * Through this test, I realised that I had an inaccuracy of int/int when I wanted a float
     * answer!
     */
    @Test
    public void determineTotalHealthWithSpecialTest() {
        assertEquals(100 + (20*3),
                determineTotalHealthDamageWithSpecial(u1,
                        u1.getUnimon().getMedSpecial()), 0);
        assertEquals(100 + (3*10) + 10,
                determineTotalHealthDamageWithSpecial(u1,
                        u1.getUnimon().getStemSpecial()), 0);
        assertEquals(100 + (3*10) + 2*(30/3),
                determineTotalHealthDamageWithSpecial(u1,
                        u1.getUnimon().getHumSpecial()), 0);
    }


    /**
     * Returns an array containing the strength of schools using the determineTotalHealthWithSpecial
     */
    @Test
    public void testFindStrengthOfSchools()
    {
        UnimonCard[] listOfCards = {u1};

        float[] damages = findStrengthOfSchools(listOfCards);
        float[] expected = { (100 + (20*3)), (100 + (3*10) + 2*(30/3)), (100 + (3*10) + 10)};
        assertArrayEquals(expected, damages, 0);

    }


    /**
     * Tests a simple search function to ensure it's returning when a school is present
     */
    @Test
    public void testSchoolInList() {
        Card[] list1 = {hum, stem, med};
        Card[] list2 = {stem, med};

        assertEquals(checkSchoolInList(list1, School.HUMANITARIAN), hum);
        assertEquals(checkSchoolInList(list2, School.HUMANITARIAN), null);

    }


    /**
     * Tests that the largest player attack is accurate of the unimon provided when both
     * evolved and basic.
     */
    @Test
    public void testLargestPlayerAttack() {
        u1.evolveCard(School.MEDICAL);

        assertEquals(largestPlayerAttack(u1), 20);
        assertEquals(largestPlayerAttack(u2), 5);
    }


    /**
     * Simple tet to determine if a card is stronger via hp attack
     */
    @Test
    public void testIsFirstCardStrongerViaHPAttack() {
        assertEquals(isFirstCardStrongerCardViaHPAttack(u1, u2), true);
        assertEquals(isFirstCardStrongerCardViaHPAttack(u2, u1), false);
    }

    /**
     * Simple test for if a card is stronger via only attack
     */
    @Test
    public void testDetermineViaAttack() {
        assertEquals(determineViaAttack(new UnimonCard[]{u1, u2}), u1);
        assertEquals(determineViaAttack(new UnimonCard[]{u2, u1}), u1);
    }


    /**
     * Tests a simple search function which looks for the largest float in an array
     */
    @Test
    public void testWhichIsLargest() {
        float[] float1 = {1, 2, 3};
        float[] float2 = {6, 6, 15};

        assertEquals(whichIsLargest(float1), 2);
        assertEquals(whichIsLargest(float2), 2);
    }

    /**
     * Tests an assortment of arrays to ensure that the strongest card via hp attack is returned
     */
    @Test
    public void testDetermineViaHPAttack() {
        UnimonCard[] card1 = {};
        UnimonCard[] card2 = {u1};
        UnimonCard[] card3 = {u1, u2};
        UnimonCard[] card4 = {u2, u1};
        assertEquals(determineViaHPAttack(card1), null);
        assertEquals(determineViaHPAttack(card2), u1);
        assertEquals(determineViaHPAttack(card3), u1);
        assertEquals(determineViaHPAttack(card4), u1);

    }


    /**
     * Tests if a card is stronger via hp attack school. This method would never be given
     * 0 or 1 card, so no need to test for these.
     */
    @Test
    public void testIsCardStrongerViaHPAttackSchool() {
        assertEquals(isCardStrongerViaHPAttackSchool(u1, u2, School.BASIC), true);
        assertEquals(isCardStrongerViaHPAttackSchool(u1, u2, School.HUMANITARIAN), true);
        assertEquals(isCardStrongerViaHPAttackSchool(u1, u2, School.MEDICAL), true);
        assertEquals(isCardStrongerViaHPAttackSchool(u1, u2, School.STEM), true);
    }


    /**
     * Tests with more than 1 card and 1 card for both evolved and unevolved. Fixed a bug where
     * I was checking for if the card was evolved when I shouldn't have!
     */
    @Test
    public void testDetermineStrongestViaType() {
        UnimonCard[] list = {u1};
        UnimonCard[] list2 = {u1, u2};

        assertEquals(determineStrongestViaType(list, School.BASIC), u1);
        assertEquals(determineStrongestViaType(list, School.HUMANITARIAN), u1);
        assertEquals(determineStrongestViaType(list2, School.HUMANITARIAN), u1);
        assertEquals(determineStrongestViaType(list2, School.BASIC), u1);
    }


    /**
     * Tests determining the weakest card with 0, 1 and 2 cards.
     */
    @Test
    public void testDetermineWeakestCard() {
        UnimonCard[] list = {u1};
        UnimonCard[] list2 = {};
        UnimonCard[] list3 = {u1, u2};

        assertEquals(determineWeakestCard(list), u1);
        assertEquals(determineWeakestCard(list2), null);
        assertEquals(determineWeakestCard(list3), u2);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////// private methods we're moving to here for testing purposes ////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an array of floats that contains the sum of the 'calculated strength' of each
     * type of school from the array of unimon cards provided. This allows you to determine
     * the (approximate) dominant school type in the list.
     * @param listOfCards cards to calculate the strengths of
     * @return an array of the Schools' approximate strengths - order = MED, HUM, STEM
     */
    private float[] findStrengthOfSchools(UnimonCard[] listOfCards) {

        float[] damages = {0, 0, 0}; //Med, Humanitarian, Stem

        for (UnimonCard c: listOfCards) {
            if (c == null) continue;
            damages[0] += determineTotalHealthDamageWithSpecial(c, c.getUnimon().getMedSpecial());
            damages[1] += determineTotalHealthDamageWithSpecial(c, c.getUnimon().getHumSpecial());
            damages[2] += determineTotalHealthDamageWithSpecial(c, c.getUnimon().getStemSpecial());
        }

        return damages;
    }



    /**
     * Determines what the approximate 'strength' is of a card. Maths is for determining a
     * 'strength' is as follows:
     * health usually = 60. To get damage (approx 20 on average) of equal weighting,
     * multiply attack by 3.
     * All skills work out: health + damage per turn (made to be of equal weighting).
     * @param card card to work out the strength of
     * @param special the special to include when working out the strength
     * @return returns the approximate strength
     */
    private float determineTotalHealthDamageWithSpecial(UnimonCard card, Specials special) {
        switch(special.getType()) {
            // attack eqn: card health + 3 * (average damage per turn)
            // average damage per turn =
            // (special damage / how often it can be used) + (basic attack / remainder of turns)
            case ATTACK: return (card.getHealth() + 3 * (card.getAttack() * (1 -
                    1/(special.getCoolDown() + 1)))
                    + 3*(special.getValue() * ((float)1 / (float)(special.getCoolDown() + 1))));

            // ability eqn: card health + 3 * basic attack + (temp health / how often it can be used)
            case DEFENSE: return (card.getHealth() + 3 * (card.getAttack()) +
                    (special.getValue() * ((float)1 / (float)(special.getCoolDown() + 1))));

            // health_increase eqn: card health + bonus health + 3 * basic attack
            case HEALTH: return ( (card.getHealth()) +
                    ( 3 * card.getAttack() ) +
                    2*(special.getValue() * ((float)1 / (float)(special.getCoolDown() + 1 ))));
        }
        //should never reach here
        return -1f;
    }


    /**
     * Check if the AI has the desired school card in their hand
     * @param cards An array of cards to check for the school card
     * @param toCheck the School card we want to search for
     * @return Desired school Card if found, null if not found.
     */
    private Card checkSchoolInList(Card[] cards, School toCheck) {
        for (Card c: cards) {
            if (c != null && c.getSchool() == toCheck) return c;
        }
        return null;
    }


    /**
     * Works out the largest damage the player is able to do on this turn
     * @param unimonCard the unimoncard that is the player active
     * @return the number that is the largest damage of this unimon active card
     */
    private int largestPlayerAttack(UnimonCard unimonCard) {
        if (unimonCard.isEvolved() && unimonCard.getCoolDown() == 0 &&
                unimonCard.getUnimon().getSpecial().getType() == Specials.TypeOfSpecial.ATTACK)
            return unimonCard.getUnimon().getSpecial().getValue();
        return unimonCard.getAttack();
    }


    /**
     * Searches for the strongest card taking into account only attack, unless they are of equal attacks
     * in which case it takes into account healths
     * @param listOfCards list of cards to search through
     * @return Strongest unimon card
     */
    private UnimonCard determineViaAttack(UnimonCard[] listOfCards) {
        // if the length of cards is 0, or the first card is just a null card, return null
        if (listOfCards.length == 0 || listOfCards[0] == null) return null;

        UnimonCard strongestAIPick = listOfCards[0];
        if (listOfCards.length == 1) return strongestAIPick; //return this if its the only card

        for(UnimonCard c: listOfCards) {
            if (c != null && largestPlayerAttack(c) >= largestPlayerAttack(strongestAIPick))
                if (largestPlayerAttack(c) > largestPlayerAttack(strongestAIPick)) strongestAIPick = c;
                else if (isFirstCardStrongerCardViaHPAttack(c, strongestAIPick)) strongestAIPick = c;
        }
        return strongestAIPick;
    }


    /**
     * Compares two cards via HP and attack. Attack is multiplied by three to get the two values of
     * approximate equal weighting, as average attack is 20 and average health is 60
     * @param card1 first card to compare
     * @param card2 second card to compare
     * @return If the first card is stronger. true = first stronger, false = first not stronger
     */
    private boolean isFirstCardStrongerCardViaHPAttack(UnimonCard card1, UnimonCard card2) {
        return (card1.getHealth() + 3* card1.getAttack()) > (card2.getHealth() + 3* card2.getAttack());
    }


    /**
     * Finds the largest of the damages provided
     * @param damages
     * @return
     */
    private int whichIsLargest(float[] damages) {
        int largest = 0;
        for (int i = 0; i < damages.length; i++)
            if (damages[i] > damages[largest]) largest = i;
        return largest;
    }


    /**
     * Currently a simple method which simply adds the two values to discover strongest unimon
     * from HP and attack values
     * @param listOfCards cards to search through
     * @return the strongest unimon with HP + attack
     */
    private UnimonCard determineViaHPAttack(UnimonCard[] listOfCards) {
        if (listOfCards.length == 0) return null;
        UnimonCard strongestAIPick = null;
        for(UnimonCard c : listOfCards) {
            if (strongestAIPick == null && c != null) {
                strongestAIPick = c;
                continue;
            }
            if (c!= null) strongestAIPick =
                    isFirstCardStrongerCardViaHPAttack(strongestAIPick, c) ? strongestAIPick : c;
        }
        return strongestAIPick;
    }

    /**
     * determines the stronger of two cards, taking into account a special with the specified school
     * @param card1 first card to compare
     * @param card2 second card to compare
     * @param school the particular school you wish to take into account
     * @return returns if card1 is stronger than card2
     */
    private boolean isCardStrongerViaHPAttackSchool(UnimonCard card1, UnimonCard card2,
                                                    School school) {

        // if the school is basic, just work out the stronger via normal hpattack
        if (school == School.BASIC) return isFirstCardStrongerCardViaHPAttack(card1, card2);

        else {
            return determineTotalHealthDamageWithSpecial(card1, card1.getUnimon().getSpecial(school)) >
                    determineTotalHealthDamageWithSpecial(card2, card2.getUnimon().getSpecial(school));
        }

    }

    /**
     * Determines the strongest based on the types of school
     * @param listOfCards cards to search through
     * @param type school type to search for
     * @return the strongest card of that type found
     */
    private UnimonCard determineStrongestViaType(UnimonCard[] listOfCards, School type) {
        UnimonCard strongestAIPick = null;

        for(UnimonCard c : listOfCards) {
            // if the card is null or not the right school, continue
            if (c == null) continue;
            // if strongestAI hasn't been assigned yet, or c is stronger, c = strongestai
            if (strongestAIPick == null || !isCardStrongerViaHPAttackSchool(strongestAIPick, c, type))
                strongestAIPick = c;
        }
        return strongestAIPick;
    }


    /**
     * Determines the weakest card, for reroll purposes
     * @param listOfCards cards to search through
     * @return the weakest unimon card
     */
    private UnimonCard determineWeakestCard(UnimonCard[] listOfCards) {
        //if there are no cards, return null
        if (listOfCards.length == 0) return null;

        UnimonCard weakestAIPick = listOfCards[0];
        if (listOfCards.length == 1) return weakestAIPick;

        // if c isn't null and weakestcard is stronger than c, weakest card is c
        for(UnimonCard c : listOfCards) {
            if (c != null && isFirstCardStrongerCardViaHPAttack(weakestAIPick, c))
                weakestAIPick = c;
        }

        return weakestAIPick;
    }

}
