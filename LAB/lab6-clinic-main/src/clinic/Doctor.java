package clinic;

import java.util.ArrayList;
import java.util.Collection;

public class Doctor extends Patient{
    private int id;
    private String specialization;

    private Collection<String> assignedPatients = new ArrayList<>();

    public Doctor(String firstName, String lastName, String ssn, int id, String specialization) {
        super(firstName, lastName, ssn);
        this.id = id;
        this.specialization = specialization;
    }

    public int getBadgeNumber() {
        return id;
    }    
    public String getSpecialization() {
        return specialization;
    }
    public void addPatient(String ssn) {
        assignedPatients.add(ssn);
    }
    public void removePatient(String ssn) {
        assignedPatients.remove(ssn);
    }
    public Collection<String> getPatients() {
        return assignedPatients;
    }

    @Override
    public String toString() {
        return super.toString() + " [" + id + "]: " + specialization; 
    }
}
