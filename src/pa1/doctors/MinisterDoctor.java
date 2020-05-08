package pa1.doctors;

import pa1.Player;
import pa1.patients.Patient;

import java.util.Random;

public class MinisterDoctor extends Doctor {
    /**
     * Constructor
     *
     * @param name
     */
    public MinisterDoctor (String name) {
        super(name);
        this.specialty = "Minister";
        this.salary = 500;
    }

    /**
     * recruitDoctor
     *
     * @param name
     */
    @Override
    public void recruitDoctor(Player player, String name) {
        Doctor doctor = null;
        Random rand = new Random();
        int magicNumber = rand.nextInt(4);
        switch (magicNumber) {
            case 0:
                doctor = new FeverDoctor(name);
                break;
            case 1:
                doctor = new MedicalDoctor(name);
                break;
            case 2:
                doctor = new SurgicalDoctor(name);
                break;
            default:
                doctor = new MinisterDoctor(name);
                break;
        }
        player.addNewlyRecruitedDoctor(doctor);
    }

    /**
     * Minister doctor is better at raising fund than other types of doctor.
     *
     * @return 1000 times (1 + 0.1 times the skillLevel)
     */
    @Override
    public int raiseFund() {
        //  the amount the minister doctor can raise is 1000 times (1 + 0.1 times the skillLevel), remember to return an int
        return (int)(1000 * (1 + 0.1 * specialSkillLevel));
    }

    /**
     * The minister doctor is good at getting discount when upgrading the hospital.
     *
     * @return 1.0 - 0.1 times the specialSkillLevel
     */
    @Override
    public double getUpgradeDiscountRate() {
        //  the discount rate  is 1.0 - 0.1 times the specialSkillLevel
        return 1 - 0.1 * specialSkillLevel;
    }

    /**
     * seePatient
     *
     * @param patient
     *
     * @return true if the patient is cured, false otherwise.
     */
    @Override
    public boolean seePatient(Patient patient) {
        if (patient.getCategory().equals(getSpecialty())) {
            return true;
        } else {
            Random rand = new Random();
            int magic = rand.nextInt(100);
            return magic > 50;
        }
    }
}
