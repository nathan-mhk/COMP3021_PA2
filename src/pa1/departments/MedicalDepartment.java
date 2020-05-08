package pa1.departments;

import pa1.patients.MedicalPatient;
import pa1.patients.Patient;

public class MedicalDepartment extends Department {
    /**
     * constructor
     */
    public MedicalDepartment() {
        super();
        this.fee = 200;
        this.name = "Medical";
    }

    /**
     * request to admit/accept "count" number of patients, letting each one of them having a bed
     *
     *
     *
     * @param count number of patients are requesting to get admitted/accepted
     *
     * @return the number of actually accepted patients
     */
    @Override
    public int acceptPatients (int count) {

        int bedCount = Math.max(0, bedCapacity - patientList.size());
        int patientCount = Math.min(bedCount, count);
        for (int i=0; i<patientCount; i++) {
            Patient patient = new MedicalPatient();
            patientList.add(patient);
        }
        return patientCount;
    }
}
