package crazyhoneybadgerstudios.game.Game_Elements.AI;

import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Specials;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Unimon;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;
import crazyhoneybadgerstudios.game.Game_Elements.Player;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.School;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.SchoolCard;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonMove;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.UnimonCard;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

/**
 * Controls the battle AI - uses AILogic to make most decisions
 * @author Chloe McMullan
 */


public class AI extends Player {

    private final String TAG = this.getClass().getSimpleName();


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// INSTANCE VARIABLES //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int difficulty; // later to be used to alter intelligence of AI
    private Player humanPlayer;
    private ArrayList<AIMove> moves = new ArrayList<AIMove>(); // moves the AI has planned.
    private AILogic aiLogic;

    // these ensure certain things don't happen twice in a row
    private boolean usedDefensiveLastTurn;
    private boolean rerolledLastTurn;



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// CONSTRUCTOR ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Constructor for the AI Player
     * @param name The name of the AI
     * @param hp The Wit points of the AI player
     * @param difficulty The difficulty of the AI Player, int ranging from 0 - 3
     * @param humanPlayer The human player, for accessing their board cards
     */
    public AI(String name, int hp, int difficulty, Player humanPlayer, BattleScreen battleScreen) {
        super(name, hp, battleScreen);
        this.difficulty = difficulty;
        this.humanPlayer = humanPlayer;
        usedDefensiveLastTurn = false;
        rerolledLastTurn = false;
        this.aiLogic = new AILogic(this, humanPlayer);


    }


    /**
     * Constructor for the AI player. Used for testing purposes.
     * @param name - Name of the AI
     * @param hp - The number of WitPoints the AI has
     */
    public AI(String name, int hp)
    {
        super(name, hp);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// GENERAL GETTERS/SETTERS //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        if (difficulty < 0 || difficulty > 100) return;
        this.difficulty = difficulty;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// PUBLIC METHODS TO USE THE AI CLASS //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The method to be called on the first turn of the AI, as the first turn has different rules
     * to the other turns.
     */
    public ArrayList<AIMove> firstTurn() {
        placeUnimonOnBench();

        //determines strongest from your bench unimon, and places it on the active
        moveActions(aiLogic.determineStrongest(aiLogic.getAIBenchCards()), PlayArea.BENCH, PlayArea.ACTIVE);

        //evolves the active to strongest evolution
        evolveActiveToStrongestEvolutionSchool(aiLogic.getAIActiveCard());
        placeUnimonOnBench(); //ensures bench is as full as possible
        return moves;
    }


    /**
     * Method to be called each time it is the AI's turn. It checks if there is an AI active card,
     * evolves the card if it is best, places unimon on the bench if required, checks for cards
     * to reroll. These are all put into the arraylist moves, of type AIMove
     * @return the moves the AI has planned for this turn
     */
    public ArrayList<AIMove> aiTurn() {
        moves.clear();

        //If there is no active unimon, we want to add an active unimon
        if (aiLogic.getAIActiveCard() == null) {

            moveActions(aiLogic.determineStrongest(aiLogic.getAIBenchCards()), PlayArea.BENCH, PlayArea.ACTIVE);

            if (aiLogic.getAIActiveCard() == null) return moves; //ends here as we can no longer play
        }

        //checkIfShouldSwapActive(); //couldn't test enough to ensure bug free

        //If the Unimon card isn't evolved, we check if it's efficient and possible to evolve it
        //And if it is, we evolve it.
        if (aiLogic.getAIActiveCard().getSchool() == School.BASIC)
            evolveActiveToStrongestEvolutionSchool(aiLogic.getAIActiveCard());
        // we can only evolve one card, so if the AI hasn't been evolved we should evolve the bench
        else {
            if (getPlayerBench().getHowManyCards() > 0 &&
                    aiLogic.determineStrongest(aiLogic.getAIBenchCards()).getSchool() == School.BASIC) {
                evolveActiveToStrongestEvolutionSchool(
                        (UnimonCard)aiLogic.determineStrongest(aiLogic.getAIBenchCards()));
            }
        }

        // Possibly places unimon on bench, if needed/optimal. Done after Active assignment
        // in case spaces freed
        placeUnimonOnBench();

        checkForCardToReroll();

        attack();

        return moves;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// METHOD TO SWAP ACTIVE //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the active should be swapped.
     */
    private void checkIfShouldSwapActive() {
        Log.d(TAG, "checkIfShouldSwapActive: In here 1");
        // if either is null, return
        if (aiLogic.getAIActiveCard() == null || aiLogic.getHumanPlayerActive() == null ||
                aiLogic.getAIBenchCards().length == 0) return;

        UnimonCard strongestBench = aiLogic.determineViaHPAttack((aiLogic.getAIBenchCards()));

        Log.d(TAG, "checkIfShouldSwapActive: in here 2");
        Log.d(TAG, "checkIfShouldSwapActive: health smaller? " +
                (aiLogic.getAIActiveCard().getHealth() <= aiLogic.largestPlayerAttack(aiLogic.getHumanPlayerActive())));
        Log.d(TAG, "checkIfShouldSwapActive: active weaker? " +
                aiLogic.isFirstCardStrongerCardViaHPAttack(aiLogic.getAIActiveCard(), strongestBench));

        // if the active will die this turn, and the active is stronger than the strongest bench,
        // swap with the strongest bench
        if (aiLogic.getAIActiveCard().getHealth() <= aiLogic.largestPlayerAttack(aiLogic.getHumanPlayerActive()) &&
                aiLogic.isFirstCardStrongerCardViaHPAttack(aiLogic.getAIActiveCard(), strongestBench)) {
            Log.d(TAG, "checkIfShouldSwapActive: here");

            swapActions(strongestBench, aiLogic.getAIActiveCard());


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// METHOD TO EVOLVE TO THE SITUATIONALLY BEST CARD ///////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Checks if a card should be evolved, and evolves it if
     * it can be evolved to something stronger
     */
    private void evolveActiveToStrongestEvolutionSchool(UnimonCard cardToEvolve) {
        if (aiLogic.getAISchoolHandCards().length == 0) return;
        Card schoolCard;

        // chooses independently of the human active, will always cause a 'return' as if there's no
        // human active the game should technically be over. This allows for use of the 'end turn'
        // button
        if (aiLogic.getHumanPlayerActive() == null || aiLogic.getHumanPlayerActive().getSchool() == School.BASIC) {
            // chooses the strongest school based on the strengths of each, and if a school card
            // is available
            schoolCard = aiLogic.chooseSchoolBasedOnStrengths(
                    aiLogic.findStrengthOfSchools(new UnimonCard[]{aiLogic.getAIActiveCard()}));
            if (schoolCard != null) attachActions(schoolCard, cardToEvolve, false);
            return;
        }

        // can we kill human without evolving? if yes, should we evolve to counter the strongest
        // strongest bench school instead?
        if (aiLogic.getAIActiveCard().getAttack() >= aiLogic.getHumanPlayerActive().getHealth() &&
                humanPlayer.getPlayerBench().getHowManyCards() > 0) {

            schoolCard = aiLogic.chooseCounterSchoolBasedOnStrengths( aiLogic.findStrengthOfSchools(aiLogic.getHumanBenchCards()));
            if (attachActions(schoolCard, cardToEvolve, true)) return;
        }

        //prioritise the Ability of extra health if the card we're evolving is the active and
        // it will be killed in one turn -
        if (cardToEvolve == aiLogic.getAIActiveCard() &&
                aiLogic.getAIActiveCard().getHealth() <= aiLogic.getHumanPlayerActive().getAttack()) {
            schoolCard = aiLogic.checkSchoolInList(aiLogic.getAISchoolHandCards(),
                    aiLogic.strongestSchoolBasedOnHealth(aiLogic.getAIActiveCard()));
            if (attachActions(schoolCard, cardToEvolve, true)) return;
        }

        // checks for the counter to the human active card
        schoolCard = aiLogic.checkSchoolInList(aiLogic.getAISchoolHandCards(),
                aiLogic.getHumanPlayerActive().thisSchoolIsWeakTo());
        if (attachActions(schoolCard, cardToEvolve, true)) return;

        //checks for the same type as the human active card, at this point it's not worth evolving
        // a card that isn't the ai active
        if (cardToEvolve != aiLogic.getAIActiveCard()) return;
        schoolCard = aiLogic.checkSchoolInList(aiLogic.getAISchoolHandCards(),
                aiLogic.getHumanPlayerActive().getSchool());
        if (schoolCard != null) attachActions(schoolCard, cardToEvolve, false);
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// METHOD TO SITUATIONALLY PLACE BEST CARD ON BENCH ////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if we should place unimon on the bench. Aims to fill the bench.
     */
    private void placeUnimonOnBench() {

        //If we have less than 1 unimon, and have unimon in hand, we try to add a unimon onto bench
        while (this.getPlayerBench().getHowManyCards() < 3 && aiLogic.getAIUnimonHandCards().length > 0) {
            UnimonCard cardToAdd = aiLogic.determineViaHPAttack(aiLogic.getAIUnimonHandCards());
            moveActions(cardToAdd, PlayArea.HAND, PlayArea.BENCH);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////// METHODS FOR SEARCHING FOR THE MOST EFFICIENT CARD TO REROLL, IF ANY //////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if there are cards to reroll. Reroll criteria is if you have 2 or more of the same
     * school card, which isn't the type of school card the AI active needs, or the weakest unimon if
     * there are 2 or more (as at this point, we will have already placed unimon to the bench)
     */
    private void checkForCardToReroll() {
        //rerolls only occur when the deck is 50% or more full
        if ((float)getPlayerDeck().getHowManyCards() /
                (float)getPlayerDeck().getCardsSize() < 0.5
                || rerolledLastTurn) {
            rerolledLastTurn = false;
            return;
        }

        // If there are no unimon in the hand, and the bench hasn't been filled,
        // it would probably be most efficient to reroll a school card
        if (aiLogic.getAIUnimonHandCards().length < 1 && getPlayerBench().getHowManyCards() < 3) {
            int indexToReroll = aiLogic.checkForDoublesOfSchoolsExcludeHumanActiveCounter(); //-1 is a flag for no doubles
            if (indexToReroll != -1) {
                rerollActions(getPlayerHand().getAllCards().get(indexToReroll));
            }
        }

        //reroll the weakest unimon if there are more than 2 unimon in hand, or if we require a
        // school card that we don't have when our AI hasn't been evolve
        else if (getPlayerHand().returnOnlyUnimon().size() >  2 ||
                ( aiLogic.getHumanPlayerActive() != null && aiLogic.getAIActiveCard().getSchool() != School.BASIC &&
                        (aiLogic.checkSchoolInList(aiLogic.getAIHandCards(),
                                aiLogic.getHumanPlayerActive().thisSchoolIsWeakTo()) == null))){
            rerollActions(aiLogic.determineWeakestCard(aiLogic.getAIUnimonHandCards()));
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// METHODS FOR ATTACKING ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to add an attack to moves. Will decide what move is the most efficient to use
     */
    private void attack() {
        if (aiLogic.getHumanPlayerActive() == null) return;
        School s = aiLogic.getAIActiveCard().getSchool();

        boolean charging = chargeACard(aiLogic.getAIActiveCard());

        //if our cooldown is greater than 0 on our special attack, or we're avoiding the best turn,
        // or we haven't charged and are not charging, we'll just use the basic attack
        if ((!aiLogic.getAIActiveCard().isCharged() && !charging) ||
                aiLogic.getAIActiveCard().getCoolDown() != 0 || avoidTheBestTurn()) s = School.BASIC;

        // if the active isn't evolved, or the cooldown isn't 0 on the special, or it's not
        // worth using a special, just use the basic
        else if (!aiLogic.getAIActiveCard().isEvolved() || aiLogic.getAIActiveCard().getCoolDown() != 0 ||
                !decideIfWorthUsingSpecial(aiLogic.getAIActiveCard().getUnimon(),
                        aiLogic.getAIActiveCard().getUnimon().getSpecial())) s = School.BASIC;

        moves.add(new AIMove(new UnimonMove(this, humanPlayer, s,
                battleScreen)));

    }


    /**
     * Charges a card if possible
     * @param toCharge the card to charge
     * @return true if the card has been charged, false otherwise
     */
    private boolean chargeACard(UnimonCard toCharge) {
        SchoolCard schoolCard;

        // ensures that if we're evolving the card this turn, we're taking into account that it has been evolved

        if (toCharge.isEvolved() && !toCharge.isCharged()) {
            schoolCard = aiLogic.determineLeastUsefulSchoolCard();
            if (schoolCard != null) {
                moves.add(new AIMove(AIMove.TypeOfMove.ATTACH, schoolCard, aiLogic.getAIActiveCard()));
                getPlayerHand().removeCard(schoolCard);
                return true;
            }
        }
        return false;
    }




    /**
     * Indicates if it's worth using a special ability over your basic attack
     * @param unimon the unimon the special will be used on
     * @param specials what the special is
     * @return true = use special, false = use basic attack
     */
    private boolean decideIfWorthUsingSpecial(Unimon unimon, Specials specials) {
        // this is only called when the Human active Unimon exists so no need to check non-null
        switch(specials.getType()) {
            case ATTACK: if (unimon.getAttack() > aiLogic.getHumanPlayerActive().getHealth()) return false;
                break;
            // temp health and permanent health currently have the same equation:
            // if temp health + defence < player attack, it will be a waste of a school card
            default: if (specials.getValue() < aiLogic.largestPlayerAttack(aiLogic.getHumanPlayerActive()) ||
                    unimon.getHealth() + specials.getValue() > 150 || usedDefensiveLastTurn) {
                usedDefensiveLastTurn = false;
                return false;
            }
            usedDefensiveLastTurn = true;
                break;
        }
        return true;
    }







    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// CARD MOVEMENT UTIL ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Performs the actions for a card to be swapped, if both cards are not null
     * @param benchCard bench card to swap into active
     * @param activeCard active card to swap into bench
     */
    private void swapActions(UnimonCard benchCard, UnimonCard activeCard) {
        if (benchCard == null || activeCard == null) return;

        getPlayerActiveContainer().removeCard(activeCard);
        getPlayerBench().removeCard(benchCard);
        getPlayerActiveContainer().addCard(benchCard);
        getPlayerBench().addCard(activeCard);

        moves.add(new AIMove(AIMove.TypeOfMove.SWAP_ACTIVE, benchCard, activeCard));
    }

    /**
     * Performs all the possible actions required before moving a card
     * @param moving card that will be moved
     * @param movingFromHere play area we're moving from
     * @param movingToHere play area we're moving to
     */
    private void moveActions(Card moving, PlayArea movingFromHere, PlayArea movingToHere) {

        moves.add(new AIMove(moving, movingFromHere, movingToHere));

        //only 2 possibilities are hand -> bench and bench -> active
        switch (movingFromHere) {
            // can only move to bench from hand
            case HAND: moveCardToDifferentContainer(getPlayerHand(), getPlayerBench(), moving);
                break;
            // can only move to active
            case BENCH: moveCardToDifferentContainer(getPlayerBench(), getPlayerActiveContainer(), moving);
                break;
        }
    }



    /**
     * Actions that occur when we want to attach a card. Adds the move if schoolcard isn't null,
     * and if we're not trying to avoid the best turn
     * @param schoolCard school card to attach to a unimon card
     * @param attachTo unimon card to attach to
     * @param avoidBestTurn if we want to test to avoid the best turn
     * @return true if an attach has occured, false otherwise
     */
    private boolean attachActions(Card schoolCard, UnimonCard attachTo, boolean avoidBestTurn) {
        if (avoidBestTurn && avoidTheBestTurn()) return false;

        if (schoolCard != null) {
            moves.add(new AIMove(AIMove.TypeOfMove.ATTACH, schoolCard, attachTo));
            getPlayerHand().removeCard(schoolCard);
            return true;
        }
        return false;
    }



    /**
     * Takes in a card we wish to reroll. If the card isn't null, it will reroll it
     * @param cardToReroll the card we wish to reroll.
     */
    private void rerollActions(Card cardToReroll) {
        if (cardToReroll == null) return;
        moves.add(new AIMove(cardToReroll));
        getPlayerHand().removeCard(cardToReroll);
        rerolledLastTurn = true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// TAKING DIFFICULTY INTO ACCOUNT ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Allows you to decide if you should avoid taking a turn due to your difficulty
     * @return if true, avoid that turn, if false take the best turn
     */
    private boolean avoidTheBestTurn() {
        return Math.random() * 100 > difficulty;
    }

}