package pa1;

import pa2.Main;
import pa1.departments.Department;
import pa1.departments.FeverDepartment;
import pa1.departments.MedicalDepartment;
import pa1.departments.SurgicalDepartment;
import pa1.doctors.Doctor;
import pa1.doctors.MinisterDoctor;
import pa1.exceptions.DeficitException;

import java.util.ArrayList;
import java.util.List;
public class Player {
    // Attributes
    private List<Department> departments = new ArrayList<>(); // list of departments of this player
    private List<Doctor> doctors = new ArrayList<>(); //list of doctors of this player
    private final String name;         // Player name (read from players.txt)
    private final String hospitalName; // hospital name (read from players.txt)
    private int money; //this variable is holding the money the player has
    private int deficitTimeSpan = 0; // for how many seasons the money instance variable has been negative
    private int curedPatientCount = 0;    // cumulative curedPatientCount up to the current season
    private int potentialCustomers = 100; // an artificial number for calculating the new patients/customers each season

    //constructor of the player
    public Player(String name, String hospitalName, int money) {
        this.name = name;
        this.hospitalName = hospitalName;
        this.money = money;
        Doctor doctor = new MinisterDoctor(name);
        doctors.add(doctor);

        Department feverDept = new FeverDepartment();
        Department medicalDept = new MedicalDepartment();
        Department surgicalDept = new SurgicalDepartment();
        departments.add(feverDept);
        departments.add(medicalDept);
        departments.add(surgicalDept);

        // at the beginning of the game, the player has at least one doctor,
        // who is a MinisterDoctor and carries the name of the player himself/herself
        // we put this doctor to the medicalDept instead of the other two possible departments
        // (feverDept/surgicalDept)
        doctor.transferToDepartment(medicalDept);
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public String getName() {
        return name;
    }

    public int getHospitalScale()
    {
        int capacity = 0;
        for(Department department: this.getDepartments())
        {
            capacity += department.getBedCapacity();
        }
        if(capacity < 50)
            return 1;
        else if(capacity < 80)
                return 2;
        else
            return 3;

    }

    public int getMoney() {
        return money;
    }

    public int getDeficitTimeSpan() {
        return deficitTimeSpan;
    }

    public int getCuredPatientCount() {
        return curedPatientCount;
    }

    public void updateCuredPatientCount() {
        int count = 0;
        for (Department dept: departments) {
            count += dept.getCuredPatientCount();
        }
        curedPatientCount = count; // accumulate all the cured patients by the player up to the current season
    }

    public int payOperationCost() { // operational cost of the player in hiring all his/her doctors
        int opCost = 0;
        for (Doctor doctor: doctors) {
            opCost += doctor.getSalary();
        }
        spendMoney(opCost);
        return opCost;
    }

    public void checkBalanceAtEndOfTurn() { // check to see if the player has ran into deficit
        if (money < 0) {                    // update deficitTimeSpan accordingly
            deficitTimeSpan++;
        } else {
            deficitTimeSpan = 0;
        }
    }

    public void processAtStartOfTurn() {
        //System.out.printf("### Start automatic pre processing for player %s\n", name);
        //System.out.printf("### Refresh doctor status... \n");
        String result = "### Start automatic pre processing for player " + name + "\n";
        Main.printResult.add(result);
        result = "### Refresh doctor status... \n";
        Main.printResult.add(result);
        doctors.forEach(Doctor::beginTurn); //lambda expression to run the beginTurn() method for each doctor in the doctors ArrayList
        for (Department dept: departments) {
            int count = dept.generatePatients(potentialCustomers);
            int acceptedCount = dept.acceptPatients(count);
            dept.addWaitingPatientCount(Math.max(0,count-acceptedCount));//AL added
            //System.out.printf("### %d new patients come to %s Department, %d accepted, %d enter waiting list \n",
                    //count, dept.getName(), acceptedCount, count - acceptedCount);
            result = "###" + count +"  new patients come to " + dept.getName() + "  Department, " + acceptedCount + "  accepted, " + (count - acceptedCount) + "  enter waiting list \n";
            Main.printResult.add(result);
        }
    }

    public void processAtEndOfTurn() throws DeficitException {
        //System.out.printf("### Start automatic post processing for player %s\n", name);
        String result = "### Start automatic post processing for player"  + name + "\n";
        Main.printResult.add(result);

        int income = 0;
        int curedPatients = 0;
        for (Department dept: departments) {// traverse all the departments of the player
            int departCured=0;
            departCured= dept.startTreatment();
            curedPatients += departCured; // calculate cured patients in this season for a department
            income += departCured * dept.getFee();//earning of the department from cured patients
        }
        collectMoney(income); // accumulate income into the money variable of the player object, this variable is holding the money the player has
        //System.out.printf("### Start treatment for each departments, cured %d, earned %d\n", curedPatients, income);
        result = "### Start treatment for each departments, cured " + curedPatients + ", earned " + income + "\n";
        Main.printResult.add(result);
        updateCuredPatientCount(); // update the cumulative cured patient number of this player from all his/her departments
        int opCost = payOperationCost();
        result = "### Pay operation cost for each departments, spend " + opCost + "\n";
        //System.out.printf("### Pay operation cost for each departments, spend %d\n", opCost);
        Main.printResult.add(result);
        result = "### Check empty beds and dead patients...\n";
        Main.printResult.add(result);
        //System.out.println("### Check empty beds and dead patients...");
        int deathCount = 0;
        boolean waitingListBoom = false;
        int compensation = 0;
        for (Department dept: departments) {
            deathCount += dept.updateWaitingListAtEndOfTurn(); //this updateWaitingListAtEndOfTurn()
            if (dept.getWaitingPatientCount() > dept.getBedCapacity()) {
                waitingListBoom = true;
                dept.clearWaitingList();
            }
            compensation += deathCount * dept.getDeathCompensation();
        }

        spendMoney(compensation);
        result = "### Pay compensation cost for death incidents, spend " + compensation + "\n";
        Main.printResult.add(result);
        //System.out.printf("### Pay compensation cost for death incidents, spend %d\n", compensation);

        checkBalanceAtEndOfTurn();
        result = "### check deficit, # of deficit turns is " + deficitTimeSpan + "\n";
        Main.printResult.add(result);
        //System.out.printf("### check deficit, # of deficit turns is %d\n", deficitTimeSpan);

        result = "### Calculate potential customers...\n";
        Main.printResult.add(result);
        //System.out.println("### Calculate potential customers...");
        if (waitingListBoom) {
            int change = potentialCustomerShrink();
            result = "### Bad news! Waiting list is boomed, potential customers shrink by " + -change + "\n";
            Main.printResult.add(result);
            //System.out.printf("### Bad news! Waiting list is boomed, potential customers shrink by %d\n", -change);
        } else if (deathCount == 0) {
            int change = potentialCustomerGrow();
            result = "### Good news! No death in this turn, potential customers grow by " + change + "\n";
            Main.printResult.add(result);
            //System.out.printf("### Good news! No death in this turn, potential customers grow by %d\n", change);
        }

        try{
            checkDeficitStatus();
        } catch(DeficitException e) {
            throw e;
        }
    }

    @Override
    public String toString() {
        return String.format("Player: %s | money: %d | doctors: %d | deficit turns: %d | cured patients: %d | potential customers: %d",
                name, money, getDoctors().size(), deficitTimeSpan, curedPatientCount, potentialCustomers);
    }

    public void addNewlyRecruitedDoctor(Doctor doctor) {
        doctor.endTurn();
        doctors.add(doctor);

        //System.out.printf("A new doctor is recruited %s\n", doctor);
    }

    private int potentialCustomerShrink() {
        int originalCount = potentialCustomers;
        potentialCustomers = (int) (potentialCustomers / 2);
        return potentialCustomers - originalCount;
    }

    private int potentialCustomerGrow() {
        int originalCount = potentialCustomers;
        potentialCustomers = (int) ((1 + 0.2) * potentialCustomers);
        return potentialCustomers - originalCount;
    }

    public void spendMoney(int amount) {
        money -= amount;
    }

    public void collectMoney(int amount) {
        if (amount < 0) {
            spendMoney(-amount);
        } else {
            money += amount;
        }
    }

    /**
     * checkDeficitStatus
     */
    public void checkDeficitStatus() throws DeficitException {
        //if (money < 0) throw new DeficitException(this);
    }

    /**
     * hasBankrupted
     *
     * @return if the player has bankrupted.
     */
    public boolean hasBankrupted() {
        return deficitTimeSpan > 2;
    }

    public boolean allDoctorOccupied() {
        for (Doctor doctor: getDoctors()) {
            if (!doctor.isOccupied()) return false;
        }
        return true;
    }
}
