package pa1.doctors;

import pa1.Player;
import pa1.patients.Patient;

import java.util.Random;

public class MedicalDoctor extends Doctor {
    /**
     * constructor
     *
     * @param name
     */
    public MedicalDoctor (String name) {
        super(name);
        this.specialty = "Medical";
        this.salary = 200;
    }

    /**
     * recruitDoctor
     *
     * @param name
     */
    @Override
    public void recruitDoctor(Player player, String name) {
        Doctor doctor = new MedicalDoctor(name);
        doctor.transferToDepartment(affiliation);
        player.addNewlyRecruitedDoctor(doctor);
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
