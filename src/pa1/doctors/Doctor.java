package pa1.doctors;

import pa1.Player;
import pa1.departments.Department;
import pa1.patients.Patient;

public abstract class Doctor {
    protected int specialSkillLevel;
    protected String name;
    protected String specialty;
    protected Department affiliation;
    protected boolean occupied;
    protected int salary;

    /**
     * Initializes the attributes of a doctor
     * initially doctor has skillLevel 0 and occupied is set to false
     *
     * @param name the doctor's name
     */
    protected Doctor(String name) {
        this.name = name;
        this.occupied = false;
        this.specialSkillLevel = 0;
        this.affiliation = null;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getSalary() {
        return salary;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public String get_affiliation(){
        if(affiliation == null)
            return "          ";
        else
            return affiliation.getName();}

    public int getSpecialSkillLevel(){return specialSkillLevel;}

    /**
     * By default, the upgrade cost for doctor is 1000 times (level + 1).
     *
     * @return the amount of cost to increase the skillLevel
     */
    public int getTrainingCost() {
        return 1000 * (specialSkillLevel + 1);
    }

    /**
     * At start of each turn, set occupied to false so as to refresh
     * doctor's status
     */
    public void beginTurn() {
        occupied = false;
    }

    /**
     * When a doctor finishes the action, end his/her turn by setting occupied
     * to true.
     */
    public void endTurn() {
        occupied = true;
    }

    /**
     * Raise money by the doctor
     *
     * @return the amount of money raised
     */
    public int raiseFund() {
        return 1000;
    }

    /**
     * A doctor can go training to get his/her specialSkillLevel promoted.
     */
    public void goTraining(Player player) {
        int cost = getTrainingCost();
        player.spendMoney(cost);
        specialSkillLevel++;
    }

    /**
     * Transfer a doctor from one department to the new Department "dept"
     *
     *
     * @param dept the department the doctor will transfer to
     */
    public void transferToDepartment(Department dept) {
        if (dept == null) return;

        if (affiliation != null) {
            affiliation.doctorLeft(this);
        }

        affiliation = dept;
        dept.doctorAssigned(this);
    }

    /**
     * A doctor can upgrade the department.
     *
     * @param player
     * @param dept
     */
    public void upgradeDepartment(Player player, Department dept) {
        int cost = (int) (dept.getUpgradeCost() * dept.getBedCapacity() * getUpgradeDiscountRate());
        player.spendMoney(cost);
        dept.upgrade();
    }

    /**
     * By default, doctor has no discount when doing hospital upgrading.
     *
     * @return 1.0
     */
    public double getUpgradeDiscountRate() {
        return 1.0;
    }

    /**
     * According to the patient's category and the doctor's specialty,
     * decide if the doctor can successfully cure the patient
     *
     * @param patient
     *
     * @return true if the patient is cured
     */
    public abstract boolean seePatient(Patient patient); // abstract method, you need to provide definition
                                                         // in FeverDoctor,MedicalDoctor,MinisterDoctor,
                                                         // and SurgicalDoctor classes


    /**
     * According to the doctor's specialty, decide which type of new
     * doctor is recruited, and add the doctor to the player's doctorList
     *
     * @param player the current player
     * @param name the new doctor's name
     */
    public abstract void recruitDoctor(Player player, String name); // abstract method, you need to provide definition
                                                                    // in FeverDoctor,MedicalDoctor,MinisterDoctor,
                                                                    // and SurgicalDoctor classes


    @Override
    public String toString() {
        return String.format("Doctor %s, assigned to %s department, level %d specialized in %s (%s)",
                name, affiliation == null ? "no" : affiliation.getName(), specialSkillLevel, specialty, isOccupied() ? "occupied" : "ready");
    }
}
