package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;

/**
 * @author Michael Grundie.
 */
public class Unimon {
    private String name;
    private int health;
    private int resetHealth;
    private int baseAttack;
    private int attack;

    private Specials medSpecial;
    private Specials stemSpecial;
    private Specials humSpecial;

    private int tempHealth;
    private School school; //Shows the unimon's evolved type

    public Unimon(Unimon unimon){
        this.name = unimon.name;
        this.health = unimon.health;
        this.resetHealth = unimon.resetHealth;
        this.baseAttack = unimon.baseAttack;
        this.attack = unimon.attack;
        this.medSpecial = unimon.medSpecial;
        this.stemSpecial = unimon.stemSpecial;
        this.humSpecial = unimon.humSpecial;
        this.tempHealth = unimon.tempHealth;
        this.school = unimon.school;
    }

    /**
     * @author Michael Grundie.
     * @param unimonsStats
     */
    public Unimon (String[] unimonsStats){

        this.name = unimonsStats[0];
        this.health = Integer.parseInt(unimonsStats[1]);
        resetHealth = health;
        this.baseAttack = Integer.parseInt(unimonsStats[2]);
        attack = baseAttack;
        this.medSpecial = new Specials(stringToEnum(unimonsStats[3]),
                Integer.parseInt(unimonsStats[4]), Integer.parseInt(unimonsStats[5]));
        this.stemSpecial = new Specials(stringToEnum(unimonsStats[6]),
                Integer.parseInt(unimonsStats[7]), Integer.parseInt(unimonsStats[8]));
        this.humSpecial = new Specials(stringToEnum(unimonsStats[9]),
                Integer.parseInt(unimonsStats[10]), Integer.parseInt(unimonsStats[11]));
        this.school = School.BASIC;

    }


    /////////////////////////// GENERAL ACCESSORS / MUTATORS ///////////////////////////

    public String getName()
    {
        return name;
    }

    public int getHealth()
    {
        return health;
    }

    public int getAttack()
    {
        return attack;
    }

    public Specials getMedSpecial()
    {
        return medSpecial;
    }

    public Specials getStemSpecial()
    {
        return stemSpecial;
    }

    public Specials getHumSpecial()
    {
        return humSpecial;
    }

    /**
     * Returns what the current special is of the school, or null if it hasn't been evolved
     * @return what the special of the school is
     * @author Chloe McMullan
     */
    public Specials getSpecial() {
        switch(school) {
            case STEM: return stemSpecial;
            case HUMANITARIAN: return humSpecial;
            case MEDICAL: return medSpecial;
        }
        return null;
    }

    /**
     * Returns an array containing the three specials
     * @return an array of the three specials in the order: MED, STEM, HUM
     * @author Chloe McMullan
     */
    public Specials[] getAllSpecials() {
        Specials[] listOfSpecials = {medSpecial, stemSpecial, humSpecial};
        return listOfSpecials;
    }


    /**
     * Finds which of the three schools a special belongs to, or returns null otherwise
     * @param s special you're searching for the school of
     * @return the school the special belongs to, or null if no match.
     * @author Chloe McMullan
     */
    public School findSchoolOfSpecial(Specials s) {
        if (s == humSpecial) return School.HUMANITARIAN;
        else if (s == stemSpecial) return School.STEM;
        else if (s == medSpecial) return School.MEDICAL;
        return null;
    }


    /**
     * Returns the special associated with the school desired, or null if basic
     * @param searchingFor the school you're searching for the special of
     * @return the special associated with that school, or null otherwise
     * @author Chloe McMullan
     */
    public Specials getSpecial(School searchingFor) {
        switch (searchingFor) {
            case HUMANITARIAN: return getHumSpecial();
            case STEM: return getStemSpecial();
            case MEDICAL: return getMedSpecial();
            default: return null;
        }
    }

    /**
     * @author Michael Grundie.
     */
    public void resetHealth()
    {
        health = resetHealth;
    }
    public void setHealth (int health)
    {
        this.health = health;
    }

    /**
     * @author Courtney Shek
     *
     * Returns a Unimon's temporary health
     *
     * @return a Unimon's temporary health
     */
    public int getTempHealth()
    {
        return tempHealth;
    }

    /**
     * @author Courtney Shek
     *
     * Sets a Unimon's temporary health
     *
     * @param tempHealth - The Unimon's temporary health
     */
    public void setTempHealth(int tempHealth)
    {
        this.tempHealth = tempHealth;
    }

    public School getSchool()
    {
        return school;
    }

    /**
     * @author Michael Grundie
     * @param school
     */
    public void evolve(School school)
    {
        this.school = school;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// UTIL ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts a string to an enum to allow for the type of special to be found by entering a
     * string
     * @param string type of special you're looking for
     * @return the type of special if desired, or null if not found
     * @author Chloe McMullan
     */
    private Specials.TypeOfSpecial stringToEnum(String string) {
        switch(string) {
            case "ATTACK": return Specials.TypeOfSpecial.ATTACK;
            case "HEALTH": return Specials.TypeOfSpecial.HEALTH;
            case "DEFENSE": return Specials.TypeOfSpecial.DEFENSE;
            default: return null;
        }
    }
}
