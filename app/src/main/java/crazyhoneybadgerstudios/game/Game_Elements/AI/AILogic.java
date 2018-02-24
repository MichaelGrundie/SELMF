package crazyhoneybadgerstudios.game.Game_Elements.AI;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Specials;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.Game_Elements.Player;

/**
 * Created by Chloe.
 * Used to make the decisions for the AI - refactored out.
 */

public class AILogic {

    private AI ai;
    private Player humanPlayer;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// METHODS TO MAKE ACCESSING COMMON VARIABLES EASIER ////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Returns the the AI Player's active
     * @return The AI Player's active card
     */
    protected UnimonCard getAIActiveCard() {
        return (ai.getPlayerActiveContainer().getHowManyCards() == 0 ? null :
                (UnimonCard) ai.getPlayerActiveContainer().getAllCards().get(0));
    }

    /**
     * Returns AI Player's bench cards
     * @return The AI player's bench cards as an array
     */
    protected UnimonCard[] getAIBenchCards() {
        return ai.getPlayerBench().getAllCards().toArray(
                new UnimonCard[ai.getPlayerBench().getCardsSize()]);
    }

    /**
     * Returns AI Unimon Cards in hand
     * @return returns only the unimon cards in the AI player's hand as an array
     */
    protected UnimonCard[] getAIUnimonHandCards() {
        return ai.getPlayerHand().returnOnlyUnimon().toArray(
                new UnimonCard[ai.getPlayerHand().returnOnlyUnimon().size()]);
    }

    /**
     * Returns the school cards within the AI hand
     * @return the school cards within the AI hand
     */
    protected SchoolCard[] getAISchoolHandCards() {
        return ai.getPlayerHand().returnOnlySchool().toArray(
                new SchoolCard[ai.getPlayerHand().returnOnlySchool().size()]);
    }

    /**
     * Returns all the cards in the AI hand as an array
     * @return all the AI hand cards, as an array
     */
    protected Card[] getAIHandCards() {
        return ai.getPlayerHand().getAllCards().toArray(new Card[ai.getPlayerHand().getAllCards().size()]);
    }


    /**
     * Returns humanPlayer Player's bench cards
     * @return humanPlayer player bench cards as an array
     */
    protected UnimonCard[] getHumanBenchCards() {
        return humanPlayer.getPlayerBench().getAllCards().
                toArray(new UnimonCard[humanPlayer.getPlayerBench().getCardsSize()]);
    }


    /**
     *  Get the humanPlayer player's active card
     * @return returns humanPlayer player's active card, or null if there is none
     */
    protected UnimonCard getHumanPlayerActive() {
        if (humanPlayer.getPlayerActiveContainer().getHowManyCards() == 0) return null;
        return (UnimonCard)humanPlayer.getPlayerActiveContainer().getAllCards().get(0);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// CONSTRUCTOR ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the AI Logic class. Used to perform logical actions within the AI.
     * @param ai
     * @param humanPlayer
     */
    protected AILogic(AI ai, Player humanPlayer) {
        this.ai = ai;
        this.humanPlayer = humanPlayer;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// METHODS TO DETERMINE STRONGER OF 2 CARDS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Decides what the best way to determine the strongest card is.
     * @param listOfCards list of unimon cards to search through (generally AI hand/bench)
     * @return returns the strongest card
     */
    protected Card determineStrongest(UnimonCard[] listOfCards) {
        UnimonCard playerActive = getHumanPlayerActive(); UnimonCard strongestAIPick = null;

        //If player has no active, just place your strongest benched card
        if (playerActive == null) {
            strongestAIPick = determineViaHPAttack(listOfCards);
            return strongestAIPick;
        }

        //If player's active health is 30 or less, place your highest damaging card
        else if(playerActive.getHealth() <= 30) {
            strongestAIPick = determineViaAttack(listOfCards);
            return strongestAIPick;
        }

        //If player active is evolved, find the strongest counter evolution
        if (playerActive.getSchool()!= School.BASIC)
            strongestAIPick = determineStrongestViaType(listOfCards, playerActive.thisSchoolCounters());

        //If there wasn't a counter school evolution, check for same type evolution
        if (strongestAIPick != null) return strongestAIPick;
        strongestAIPick = determineStrongestViaType(listOfCards, playerActive.getSchool());

        //If all else fails, simply take the strongest from your bench
        if (strongestAIPick != null) return strongestAIPick;
        strongestAIPick = determineViaHPAttack(listOfCards);


        return strongestAIPick;
    }




    /**
     * Searches for the strongest card taking into account only attack, unless they are of equal attacks
     * in which case it takes into account healths
     * @param listOfCards list of cards to search through
     * @return Strongest unimon card
     */
    protected UnimonCard determineViaAttack(UnimonCard[] listOfCards) {
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
     * Works out what the strongest card is via HP alone. Useful for Unimon with high attacks
     * @param listOfCards list of cards to search for the strongest from
     * @return returns the strongest card
     */
    protected UnimonCard determineViaHP(UnimonCard[] listOfCards) {
        UnimonCard strongestAIPick = listOfCards[0];
        if (listOfCards.length == 1) return strongestAIPick;
        for(UnimonCard c: listOfCards) {
            if (c.getHealth() >= strongestAIPick.getHealth())
                strongestAIPick = (c.getAttack() > strongestAIPick.getAttack() ? c : strongestAIPick);
        }
        return strongestAIPick;
    }





    /**
     * Currently a simple method which simply adds the two values to discover strongest unimon
     * from HP and attack values
     * @param listOfCards cards to search through
     * @return the strongest unimon with HP + attack
     */
    protected UnimonCard determineViaHPAttack(UnimonCard[] listOfCards) {
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
     * Compares two cards via HP and attack. Attack is multiplied by three to get the two values of
     * approximate equal weighting, as average attack is 20 and average health is 60
     * @param card1 first card to compare
     * @param card2 second card to compare
     * @return If the first card is stronger. true = first stronger, false = first not stronger
     */
    protected boolean isFirstCardStrongerCardViaHPAttack(UnimonCard card1, UnimonCard card2) {
        return (card1.getUnimon().getHealth() + 3* card1.getUnimon().getAttack()) >
                (card2.getUnimon().getHealth() + 3* card2.getUnimon().getAttack());
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
    protected float determineTotalHealthDamageWithSpecial(UnimonCard card, Specials special) {
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
     * determines the stronger of two cards, taking into account a special with the specified school
     * @param card1 first card to compare
     * @param card2 second card to compare
     * @param school the particular school you wish to take into account
     * @return returns if card1 is stronger than card2
     */
    protected boolean isCardStrongerViaHPAttackSchool(UnimonCard card1, UnimonCard card2,
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
    protected UnimonCard determineStrongestViaType(UnimonCard[] listOfCards, School type) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////// METHODS BASED OFF STRENGTH OF SCHOOLS/SPECIALS /////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Returns an array of floats that contains the sum of the 'calculated strength' of each
     * type of school from the array of unimon cards provided. This allows you to determine
     * the (approximate) dominant school type in the list.
     * @param listOfCards cards to calculate the strengths of
     * @return an array of the Schools' approximate strengths - order = MED, HUM, STEM
     */
    protected float[] findStrengthOfSchools(UnimonCard[] listOfCards) {

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
     * Uses the float[] created by findStrengthOfSchools to determine what a viable and possible
     * counter evolution to these schools can be. Returning null means we have no school cards.
     * @param damages an array of the Schools' approximate strengths - order = MED, HUM, STEM
     * @return the card of the strongest possible
     */
    protected Card chooseCounterSchoolBasedOnStrengths(float[] damages) {

        // if two damages = -1, then we'll be choosing the weakest of the available schools so not worth
        if (damages[0] + damages[1] == -2f || damages[0] + damages[2] == -2f ||
                damages[1] + damages[2] == -2f) return null;

        Card schoolCard = null;

        int largest = whichIsLargest(damages);

        // if medical is strongest, choose stem
        if (largest == 0) schoolCard = checkSchoolInList(getAISchoolHandCards(), School.STEM);
            // if humanitarian is strongest, choose medical
        else if (largest == 1) schoolCard = checkSchoolInList(getAISchoolHandCards(), School.MEDICAL);
            // if stem is strongest, choose humanitarian
        else schoolCard = checkSchoolInList(getAISchoolHandCards(), School.HUMANITARIAN);

        //if a card hasn't been chosen, it was because the school card wasn't present so set that
        // damage to 0 and recall the method
        if (schoolCard == null) {
            damages[largest] = -1f;
            schoolCard = chooseCounterSchoolBasedOnStrengths(damages);
        }
        return schoolCard;
    }



    /**
     * Uses the float[] created by findStrengthOfSchools to determine what a the strongest school
     * is, using only these damages as a guideline.
     * @param damages an array of the Schools' approximate strengths - order = MED, HUM, STEM
     * @return returns a school card of the type considered strongest, otherwise returns null
     */
    protected Card chooseSchoolBasedOnStrengths(float [] damages) {
        // if two damages = -1, then we'll be choosing the weakest of the available schools so not worth
        if (damages[0] + damages[1] == -2f || damages[0] + damages[2] == -2f ||
                damages[1] + damages[2] == -2f) return null;

        int largest = whichIsLargest(damages);
        Card schoolCard = null;

        //checks if the strongest school card is available
        if (largest == 0) schoolCard = checkSchoolInList(getAISchoolHandCards(), School.MEDICAL);
        else if (largest == 1) schoolCard = checkSchoolInList(getAISchoolHandCards(), School.HUMANITARIAN);
        else schoolCard = checkSchoolInList(getAISchoolHandCards(), School.STEM);

        damages[largest] = -1f; //now sets the largest damage to be -1 so it won't be chosen again
        if (schoolCard == null) chooseSchoolBasedOnStrengths(damages); // if not chosen, retry

        return schoolCard;

    }



    /**
     * Finds the strongest school to evolved to, purely based off the health you gain and if it
     * counters the current enemy active
     * @param c card to compare the school abilities off
     * @return returns the most advantageous School in terms of health gained
     */
    protected School strongestSchoolBasedOnHealth(UnimonCard c) {
        // gets all specials in order: med, stem, hum
        Specials[] specials = c.getUnimon().getAllSpecials();

        //defaulted values
        float healthIncrease = 0f;
        School schoolToReturn = School.BASIC;

        // goes through each special, and if it's considered better, values updated accordingly
        for (Specials s: specials) {
            if (decideIfBetterInTermsOfHealth(c.getUnimon(), s, healthIncrease)) {
                healthIncrease = s.getValue() / (float)(s.getCoolDown() + 1); // health per turn value
                schoolToReturn =  c.getUnimon().findSchoolOfSpecial(s);
            }
        }

        return schoolToReturn;
    }


    /**
     * Returns if a school is better in terms of health increase compared to the current highest
     * health increase
     * @param unimon unimon to search for the specials on
     * @param special The special we're testing on this unimon
     * @param healthIncrease the current maximum health increase
     * @return true = this special is better, false = special isn't better
     */
    protected boolean decideIfBetterInTermsOfHealth(Unimon unimon, Specials special,
                                                    float healthIncrease) {
        //the special that belongs to this school
        School school = unimon.findSchoolOfSpecial(special);

        // if this special is what the human ai counters, or if there's no school cards
        // that match this special, don't choose it
        if (school == getHumanPlayerActive().thisSchoolCounters() || checkSchoolInList(
                getAIHandCards(), school) == null) return false;

        // if the special is of type health, and greater than or equal to the current health
        // per turn value
        if (special.getType() == Specials.TypeOfSpecial.HEALTH
                && special.getValue()/special.getCoolDown() >= healthIncrease) {

            // if the health per turn = the current health per turn, check which is the counter
            // to the human AI - if it is the counter return true. Else, return false
            return !(special.getValue() == healthIncrease &&
                    school != getHumanPlayerActive().thisSchoolIsWeakTo());
        }
        return false;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// METHODS TO DETERMINE THE WEAKEST CARD /////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Determines the weakest card, for reroll purposes
     * @param listOfCards cards to search through
     * @return the weakest unimon card
     */
    protected UnimonCard determineWeakestCard(UnimonCard[] listOfCards) {
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


    /**
     * Determines the school card that is of the least importance in your hand for charging
     * @return the least useful card if found, else null
     */
    protected SchoolCard determineLeastUsefulSchoolCard() {
        if (getAISchoolHandCards().length == 0) return null;

        // searches for any duplicates
        int position = checkForDoublesOfSchoolsExcludeHumanActiveCounter();
        if (position != -1) return (SchoolCard)ai.getPlayerHand().getAllCards().get(position);

        // searches for human active weakness
        Card leastUseful = checkSchoolInList(getAISchoolHandCards(),
                getHumanPlayerActive().thisSchoolCounters());
        if (leastUseful != null) return (SchoolCard)leastUseful;

        // searches for same type as human active
        leastUseful = checkSchoolInList(getAISchoolHandCards(), getHumanPlayerActive().getSchool());
        if (leastUseful != null) return (SchoolCard) leastUseful;
        return null;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// METHODS TO FIND ITEMS IN LISTS ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Finds the largest of the damages provided
     * @param damages float of damages to check for the largest in
     * @return int of where in the array the largest is
     */
    protected int whichIsLargest(float[] damages) {
        int largest = 0;
        for (int i = 0; i < damages.length; i++)
            if (damages[i] > damages[largest]) largest = i;
        return largest;
    }




    /**
     * Check if the AI has the desired school card in their hand
     * @param cards An array of cards to check for the school card
     * @param toCheck the School card we want to search for
     * @return Desired school Card if found, null if not found.
     */
    protected Card checkSchoolInList(Card[] cards, School toCheck) {
        for (Card c: cards) {
            if (c != null && c.getSchool() == toCheck) return c;
        }
        return null;
    }



    /**
     * Checks if there are doubles of any school cards. If the human active is weak to a particular
     * double, it is not returned.
     * @return the position of the first instance of the double, or -1 if not found
     */
    protected int checkForDoublesOfSchoolsExcludeHumanActiveCounter() {
        for (SchoolCard c1: ai.getPlayerHand().returnOnlySchool()) {
            for (SchoolCard c2: ai.getPlayerHand().returnOnlySchool()) {
                // Tests the two cards have the same school, and that c1 is not actually c2,
                // And that our active does not require the school cards, and that the school
                // Card is not the counter to their active
                if (c1.getSchool() == c2.getSchool() && c1 != c2 && (getHumanPlayerActive() != null
                        && getHumanPlayerActive().thisSchoolIsWeakTo() != c1.getSchool())) {
                    return ai.getPlayerBench().getCardIndex(c1.getName());
                }
            }
        }
        return -1;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// FIND LARGEST PLAYER ATTACK ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Works out the largest damage the player is able to do on this turn
     * @param unimonCard the unimoncard that is the player active
     * @return the number that is the largest damage of this unimon active card
     */
    protected int largestPlayerAttack(UnimonCard unimonCard) {
        if (unimonCard.isEvolved() && unimonCard.getCoolDown() == 0 &&
                unimonCard.getUnimon().getSpecial().getType() == Specials.TypeOfSpecial.ATTACK)
            return unimonCard.getUnimon().getSpecial().getValue();
        return unimonCard.getAttack();
    }


}
