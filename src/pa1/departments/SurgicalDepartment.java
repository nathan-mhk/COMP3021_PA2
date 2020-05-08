package pa1.departments;

import pa1.patients.Patient;
import pa1.patients.SurgicalPatient;

public class SurgicalDepartment extends Department {
    /**
     * constructor
     */
    public SurgicalDepartment() {
        super();
        this.fee = 500;
        this.name = "Surgical";
    }

    /**
     * Given potential customers, accept new patients and add the patients
     * to the patientList.
     *
     * @param count number of potential customers
     *
     * @return the number of newly accepted patients
     */
    @Override
    public int acceptPatients (int count) {

        int bedCount = Math.max(0, bedCapacity - patientList.size());
        int patientCount = Math.min(bedCount, count);
        for (int i=0; i<patientCount; i++) {
            Patient patient = new SurgicalPatient();
            patientList.add(patient);
        }
//        if (patientCount < count) {
//            waitingPatientCount += count - patientCount;
//        }
        return patientCount;
    }
}
