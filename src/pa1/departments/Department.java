package pa1.departments;

import pa1.patients.Patient;
import pa1.doctors.Doctor;

import java.util.ArrayList;
import java.util.List;

public abstract class Department {
    protected List<Doctor> doctorList = new ArrayList<>();
    protected List<Patient> patientList = new ArrayList<>();
    protected int waitingPatientCount = 0; // number of patients waiting to be accepted/admitted by this department in the current season
    protected int bedCapacity;            // bed capacity of the department
    protected int upgradeCost;            // cost for player upgrade action on the department
    protected int curedPatientCount = 0; //  cumulative cured patient count up to the current season for the department
    protected int deadPatientCount = 0;  // cumulative dead patient count up to the current season for the department
    protected int fee;
    protected String name;


    Department() {
        this.bedCapacity = 10;
        this.upgradeCost = 1000;
    }

    public String getName() {
        return name;
    }

    public int getFee() {
        return fee;
    }

    public int getCuredPatientCount() {
        return curedPatientCount;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getBedCapacity() {
        return bedCapacity;
    }

    public int getWaitingPatientCount() {
        return waitingPatientCount;
    }

    public void addWaitingPatientCount(int queueLength){waitingPatientCount=waitingPatientCount+queueLength;}//AL added

    /**
     * Upgrade the department by doubling the bed capacity
     */
    public void upgrade() {
        //  double the bedCapacity variable
        //
        bedCapacity *= 2;
    }

    /**
     * Let doctors in the department start treatment (see patients).
     * This function will be called automatically at the end of each
     * player's turn.
     *
     * @return the number of newly cured patients during the treatment
     *
     */
    public int startTreatment() {


        int oldCuredPatientCount = curedPatientCount; //provide


        for(int i=0; i<doctorList.size(); i++) {
            if (patientList.size() == 0) break;
            Doctor doctor = doctorList.get(i);
            // if (doctor.isOccupied()) continue;
            int count = 0;
            while (count < 10 && patientList.size() > 0) {
                Patient patient = patientList.get(0);
                boolean success = doctor.seePatient(patientList.get(0));
                if (success) {
                    checkoutCuredPatient(patient);
                }
                count++;
            }
        }

        // System.out.printf("after treatment patient list size %d0\n", patientList.size());
        // System.out.printf("%d %d\n", curedPatientCount, originalCuredPatientCount);

        return curedPatientCount - oldCuredPatientCount;
    }

    /**
     * Assign a doctor to the department, this function will be called
     * when the player transfer a doctor to a department.
     */
    public void doctorAssigned(Doctor doctor) {
        if(!doctorList.contains(doctor)) {
            doctorList.add(doctor);
        }
    }

    /**
     * Make a doctor leave the department, this function will be called
     * when the player transfer a doctor to a department
     */
    public void doctorLeft(Doctor doctor) {
        if(doctorList.contains(doctor)) {
            doctorList.remove(doctor);
        }
    }

    /**
     * Checkout the cured patient
     */
    public void checkoutCuredPatient(Patient patient) {
        // remove the patient from the patientList
        patientList.remove(patient);
        //increase the curedPatientCount for the department
        curedPatientCount++;
    }

    /**
     * Checkout the dead patient
     */
    public void checkoutDeadPatient(Patient patient) {
        // remove the patient from the patientList
        patientList.remove(patient);
        //  increase the deadPatientCount for the department
        deadPatientCount++;
    }

    /**
     * At the end of turn, if there are patients dead, they will be removed from the patientList (ArrayList), there
     * could be beds become empty because of this. The number of empty beds is max(0, bedCapacity - patientList.size())
     * If this empty bed number is positive, Fill the empty space by the waitingList, update the length of waiting list
     * accordingly (i.e. "update waitingPatientCount").
     * updateWaitingListAtEndOfTurn()
     *
     * @return the number of dead patients at the end of this current season
     */
    public int updateWaitingListAtEndOfTurn () {

        int oldDeathCount = deadPatientCount; //provide
        List<Patient> deadPatients = new ArrayList<>(); //provide


        for (Patient patient: patientList) {
            if (patient.isDeadAtEndOfTurn()) {
                deadPatients.add(patient);
            } else {
                patient.stayForAnotherSeason();
            }
        }

        for (Patient patient: deadPatients) {
            checkoutDeadPatient(patient);
        }

        int acceptedCount = acceptPatients(waitingPatientCount);
        waitingPatientCount -= acceptedCount;

        return deadPatientCount - oldDeathCount; // return the dead count of this season.
    }

    /**
     * clearWaitingList
     */
    public void clearWaitingList () {
        //  Clear the waiting list by setting the waitingPatientCount to 0
        waitingPatientCount = 0;
    }

    /**
     * Generate patients from the "potentialCustomers" parameter
     * for current department in the current season.
     * By default, the patients is generated as 0.04 times the potential
     * customers (remember to convert to integer).
     *
     * For FeverDepartment, the number of naturally generated patients
     * is lower than other departments. So you have to override this
     * method.
     *
     * @param potentialCustomers number of potential customers
     *
     * @return the number of patients generated
     */
    public int generatePatients(int potentialCustomers) {
        //  0.04 times potential customers, convert the value to int
        return (int) (potentialCustomers * 0.04);
    }

    /**
     * calculate the death compensation the department needs to pay upon each patient death.
     * For each death, the compensation is 5 times the "fee" variable of the department.
     *
     * @return the compensation
     */
    public int getDeathCompensation() {


        return 5 * fee;
    }

    @Override
    public String toString() {
        return String.format("Department: %s | doctors: %d | patients: %d | waiting list: %d | cured: %d | dead: %d | capacity: %d",
                name, doctorList.size(), patientList.size(), waitingPatientCount, curedPatientCount, deadPatientCount, bedCapacity);
    }

    /**
     * Given potential customers, accept new patients and add the patients
     * to the patientList
     *
     * 1. If the bed capacity allows, accept all the patients.
     * 2. Otherwise, the number of accepted patients should equal
     * to the available bed space.
     *
     * @param potentialCustomers number of potential customers
     *
     * @return the number of newly accepted patients
     */
    public abstract int acceptPatients(int potentialCustomers);
}
