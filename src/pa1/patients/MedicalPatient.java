package pa1.patients;

public class MedicalPatient extends Patient {
    /**
     * constructor
     */
    public MedicalPatient() {
        super();
        this.category = "Medical";
        this.deathRate = 0.1;
    }
}
