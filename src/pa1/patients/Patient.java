package pa1.patients;

import java.util.Random;

public abstract class Patient {
    protected String category;
    protected int timeSpan = 0;
    protected int maxAllowedTimeSpan = 3;
    protected double deathRate;

    /**
     * return the category of the patient
     *
     * @return the category of the patient
     */
    public String getCategory () {
        return category;
    }

    /**
     *
     * The patient has not been treated, so he/she
     * have to wait for another turn to see the doctor.
     *
     * Here we increase the timespan by 1 indicating the patient
     * has waited for another season
     */
    public void stayForAnotherSeason() {

        timeSpan++;
    }

    /**
     * Check if the patient is dead at the end of the turn
     *
     * There are two occasions that a patient might die
     * 1. if the maxAllowedTimeSpan is reached, the patient will
     * definitely die.
     *
     * 2. Otherwise, the patient die with a probability equals
     * to the deathRate.
     *
     * Use java.util.Random to model the probability behavior.
     *
     * @return if the patient is dead
     *
     */
    public boolean isDeadAtEndOfTurn () {
        if (timeSpan > maxAllowedTimeSpan) return true;
        Random rand = new Random();
        float magic = rand.nextFloat();
        return magic < deathRate;
    }
}
