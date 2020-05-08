package pa1.patients;

public class SurgicalPatient extends Patient {
    /**
     * constructor
     */
    public SurgicalPatient() {
        super();
        this.category = "Surgical";
        this.deathRate = 0.2;
    }
}
