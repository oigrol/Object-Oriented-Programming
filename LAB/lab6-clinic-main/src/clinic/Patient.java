package clinic;

public class Patient {
    private String firstName;
    private String lastName;
    private String ssn;
    private Doctor assignedDoc; //i pazienti sono assegnait a un dottore

    public Patient(String firstName, String lastName, String ssn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getssn() {
        return ssn;
    }
    public Doctor getAssignedDoctor() {
        return assignedDoc;
    }

    public void setDoctor(Doctor doc) {
        //Se il metodo assignPatientToDoctor() viene chiamato più volte per lo stesso paziente, viene considerato solo l'ultimo dottore assegnato.
        //devo rimuovere il paziente dalla lista del dottore
        if (this.assignedDoc != null) {
            this.assignedDoc.removePatient(ssn);
        }
        assignedDoc = doc;
        doc.addPatient(this.ssn);
    }

    @Override
    public String toString() {
        return lastName + " " + firstName + " (" + ssn +")";
    }
}
