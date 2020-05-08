package pa1.patients;

public class FeverPatient extends Patient {
    /**
     * constructor
     */
    public FeverPatient() {
        super();
        this.category = "Fever";
        this.deathRate = 0.04;
    }
}
