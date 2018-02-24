package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;

import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.engine.animation.IAnimation;
import crazyhoneybadgerstudios.game.Game_Elements.Player;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.game.GameSpecificAnimations.PopOutFromCentreOfGameObjectAnimation;
import crazyhoneybadgerstudios.game.GameSpecificAnimations.moveUpOrDownGameObjectAnimation;

/**
 * @author v.1 Courtney Shek - Created basic attack class
 * @author v.2 Michael Grundie - Updated class to work with game logic
 *
 * Handles all types of Unimon moves
 */

public class UnimonMove {

    /**
     * Constructor variables
     */
    private final String TAG = this.getClass().getSimpleName();
    private UnimonCard unimonMakingMove;
    private Specials.TypeOfSpecial special;
    private School moveType;
    private Player offensive, defensive;
    private int moveValue;
    private int moveCoolDown;
    BattleScreen battleScreen;
    private ArrayList<IAnimation> animations = new ArrayList<>(5);
    School enemySchool;

    /**
     * @author Courtney Shek
     *
     * Creates a UnimonMove belonging to a specific unimon
     *
     * @param unimonMakingMove - The unimon making the move
     * @param moveType - The school the unimonMove belongs to
     */
    public UnimonMove(UnimonCard unimonMakingMove, School moveType)
    {
        this.unimonMakingMove = unimonMakingMove;
        this.moveType = moveType;
        getMoveInfo();
    }

    /**
     * @author Michael Grundie.
     *
     * @param offensive
     * @param defensive
     * @param moveType
     * @param battleScreen
     */
    public UnimonMove(Player offensive, Player defensive, School moveType, BattleScreen battleScreen)
    {
        this.battleScreen = battleScreen;
        this.offensive = offensive;
        this.defensive = defensive;
        unimonMakingMove = (UnimonCard) offensive.getPlayerActiveContainer().getAllCards().get(0);
        this.moveType = moveType;
        getMoveInfo();
    }

    /**
     * @author Michael Grundie
     */
    public void performMove()
    {

        switch (special)
        {
            case ATTACK:attack();
                break;
            case HEALTH: increaseHealth();
                break;
            default:defenseIncrease();
                break;
        }

        if(moveType != School.BASIC) unimonMakingMove.setCoolDown(moveCoolDown);
    }

    /**
     * @author Courtney Shek
     * Provides information about a Unimon's Move based on the School type of the move
     */
    private void getMoveInfo()
    {
        Unimon unimon = unimonMakingMove.getUnimon();

            switch (moveType) {
                case BASIC:
                    special = Specials.TypeOfSpecial.ATTACK;
                    moveValue = unimonMakingMove.getAttack();
                    moveCoolDown = 0;
                    break;
                case MEDICAL:
                    moveValue = unimon.getMedSpecial().getValue();
                    moveCoolDown = unimon.getMedSpecial().getCoolDown();
                    special = unimon.getMedSpecial().getType();
                    break;
                case STEM:
                    moveValue = unimon.getStemSpecial().getValue();
                    moveCoolDown = unimon.getStemSpecial().getCoolDown();
                    special = unimon.getStemSpecial().getType();
                    break;
                case HUMANITARIAN:
                    moveValue = unimon.getHumSpecial().getValue();
                    moveCoolDown = unimon.getHumSpecial().getCoolDown();
                    special = unimon.getHumSpecial().getType();
                    break;
            }
    }

    /**
     * @author Michael Grundie.
     *
     * Allows the unimon to attack an enemy unimon
     * Checks what school the enemy belongs to, if the unimon has evolved to a particular school
     * the attack may cause more or less damage
     *
     */
    public void attack()
    {
        UnimonCard recievingAttack =(UnimonCard) defensive.getPlayerActiveContainer().getAllCards().get(0);

        enemySchool = recievingAttack.getSchool();
        School strongToSchool = unimonMakingMove.thisSchoolCounters();

        if(enemySchool == strongToSchool)
        {
            //If the enemy school is strong to your school,
            //they get 10 school defense against your attacks
            reduceHealth(recievingAttack, 10);
            battleScreen.addAnimation(new PopOutFromCentreOfGameObjectAnimation(recievingAttack, enemySchool.name(),battleScreen));
            battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("ATTACKSOUND"));
            battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("USEDEFENSE"));
        }
        else reduceHealth(recievingAttack, 0);

    }

    /**
     * @author Michael Grundie.
     */
    private void defenseIncrease()
    {
        //Gives the unimon a defense.
        unimonMakingMove.setDefense(moveValue);
        battleScreen.addAnimation(new moveUpOrDownGameObjectAnimation(unimonMakingMove, special.name(), moveUpOrDownGameObjectAnimation.Direction.UP, battleScreen));
        battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("DEFENSEUP"));
        Log.d(TAG, "defenseIncrease: unimon's health increased");
    }

    /**
     * @author Michael Grundie.
     *
     * Reduces the health of the enemy recieving the attack. Considers school defense, defense, and health
     * then updates the enemy unimon's stats.
     *
     * Adds appropriate sounds/animations for the move/attack.
     *
     * @param recievingAttack -
     *                        The unimon recieving the attack
     * @param healthDefenseBalance -
     *                             School defence is passed in to start the balance.
     */
    private void reduceHealth(UnimonCard recievingAttack, int healthDefenseBalance)
    {
        //School strength minus the attack;
        Log.d(TAG, "reduceHealth: " + healthDefenseBalance + "School defense considered");
        healthDefenseBalance -= moveValue;


        //Check if there is damage left to inflict on the opposition.
        if(healthDefenseBalance < 0)
        {
            //Take leftover damage from the unimon's defense.
            if(recievingAttack.getDefense()>0)
            {
                healthDefenseBalance += recievingAttack.getDefense();
                Log.d(TAG, "reduceHealth: Unimon defense considered");
                battleScreen.addAnimation(new PopOutFromCentreOfGameObjectAnimation(recievingAttack, "DEFENSE",battleScreen));
                battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("USEDEFENSE"));

                //Left over defense is given to the opposition
                if(healthDefenseBalance >= 0)
                {
                    recievingAttack.setDefense(healthDefenseBalance);
                    Log.d(TAG, "reduceHealth: remaining defense returned");
                }
                else recievingAttack.setDefense(0);
            }

            //Take left over damage from unimon's health.
            if(healthDefenseBalance < 0)
            {

                //Take any remaining attack damage from the oppositions health
                healthDefenseBalance += recievingAttack.getHealth();
                Log.d(TAG, "reduceHealth: unimon health considered");
                battleScreen.addAnimation(new moveUpOrDownGameObjectAnimation(recievingAttack, "HEALTH",
                        moveUpOrDownGameObjectAnimation.Direction.DOWN, battleScreen));
                battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("HEALTHDOWN"));

                //Left over health is given to the opposition
                if(healthDefenseBalance >0)
                {
                    recievingAttack.setHealth(healthDefenseBalance);
                    Log.d(TAG, "reduceHealth: remaining health returned");


                }
                //If no left over health
                else
                {
                    //Opposition unimon gets zero health.
                    recievingAttack.setHealth(0);
                    battleScreen.addAnimation(new PopOutFromCentreOfGameObjectAnimation(recievingAttack,"KO",battleScreen));
                    Log.d(TAG, "reduceHealth: no health left");
                }
            }
        }
        else
        {
            //Nothing is done if the school strength was enough to sustain the attack.
            Log.d(TAG, "reduceHealth: School defense sustained the attack");
        }

    }

    /**
     * @author Courtney Shek.
     *
     * Sets the amount of damage done to the enemy unimon based on the school the enemy belongs
     * to as well as the attacking unimon. Unimon's attack damage cannot be more than 50
     * A unimon's attack will only change if their type of special is attack
     */
    private void setAttackDamage()
    {
        if(enemySchool == School.MEDICAL)
        {
            if(moveType == School.HUMANITARIAN)
            {
                moveValue += 20;
            }
            else if (moveType == School.STEM)
            {
                moveValue += 10;
            }
        }
        else if(enemySchool == School.STEM)
        {
            if(moveType == School.HUMANITARIAN)
            {
                moveValue += 10;
            }
            else if (moveType == School.MEDICAL)
            {
                moveValue += 20;
            }
        }
        else if(enemySchool == School.HUMANITARIAN)
        {
            if (moveType == School.STEM)
            {
                moveValue += 20;
            }
            else if (moveType == School.MEDICAL)
            {
                moveValue += 10;
            }
        }

        if(moveValue > 50)
        {
            moveValue = 50;
        }
    }

    /**
     * @author Courtney Shek.
     * @author Michael Grundie.
     *
     * Increases the Unimon's health, a unimon's health cannot be over 150
     */
    public void increaseHealth()
    {

            int newHealth = unimonMakingMove.getHealth() + moveValue;
            if(newHealth > 150)
            {
                unimonMakingMove.setHealth(150);
                Log.d(TAG, "increaseHealth: unimon max health increase");
            }
            else
            {
                unimonMakingMove.setHealth(newHealth);
                Log.d(TAG, "increaseHealth: unimon health increased");
            }

        battleScreen.addAnimation(new moveUpOrDownGameObjectAnimation(unimonMakingMove, special.name(), moveUpOrDownGameObjectAnimation.Direction.UP, battleScreen));
        battleScreen.addSound(battleScreen.getGame().getAssetManager().getSound("HEALTHUP"));

    }



    /**
     * Sets the type of move
     *
     * @param moveType - The type of move
     */
    public void setMoveType(School moveType)
    {
        this.moveType = moveType;
    }

    /**
     * Sets the type of move being used
     *
     * @param special - The type of move being used
     */
    public void setSpecial(Specials.TypeOfSpecial special)
    {
        this.special = special;
    }

    public School getMoveType() {
        return moveType;
    }

    public UnimonCard getUnimonMakingMove() {
        return unimonMakingMove;
    }
}
