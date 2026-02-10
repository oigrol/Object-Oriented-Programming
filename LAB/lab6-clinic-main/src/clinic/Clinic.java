package clinic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * This is the facade class for the the clinic application
 * it manages patients and doctors.
 */
public class Clinic {

	private Map<String, Patient> patientsMap = new HashMap<>();
	private Map<Integer, Doctor> doctorsMap = new HashMap<>();

	/**
	 * Add a new clinic patient.
	 * 
	 * @param first first name of the patient
	 * @param last last name of the patient
	 * @param ssn SSN number of the patient
	 */
	public void addPatient(String first, String last, String ssn) {
   		if (first == null || last == null || ssn == null) throw new IllegalArgumentException("dati mancanti / non validi");
		Patient patient = new Patient(first, last, ssn);
		patientsMap.put(ssn, patient);
 	}


	/**
	 * Retrieves a patient information
	 * 
	 * @param ssn SSN of the patient
	 * @return the object representing the patient
	 * @throws NoSuchPatient in case of no patient with matching SSN
	 */
	public String getPatient(String ssn) throws NoSuchPatient {
		if (!patientsMap.containsKey(ssn)) throw new NoSuchPatient();
   		Patient patient = patientsMap.get(ssn);
		return patient.toString();
	}

	/**
	 * Add a new doctor working at the clinic
	 * 
	 * @param first first name of the doctor
	 * @param last last name of the doctor
	 * @param ssn SSN number of the doctor
	 * @param docID unique ID of the doctor
	 * @param specialization doctor's specialization
	 */
	public void addDoctor(String first, String last, String ssn, int docID, String specialization) {
		if (first == null || last == null || ssn == null || docID < 0 || specialization == null) throw new IllegalArgumentException("dati mancanti / non validi");
		Doctor doctor = new Doctor(first, last, ssn, docID, specialization);
		doctorsMap.put(docID, doctor);
	}

	/**
	 * Retrieves information about a doctor
	 * 
	 * @param docID ID of the doctor
	 * @return object with information about the doctor
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	public String getDoctor(int docID) throws NoSuchDoctor {
		if (!doctorsMap.containsKey(docID)) throw new NoSuchDoctor(docID);
   		Doctor doctor = doctorsMap.get(docID);
		return doctor.toString();
	}
	
	/**
	 * Assign a given doctor to a patient
	 * 
	 * @param ssn SSN of the patient
	 * @param docID ID of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor exists with a matching ID
	 */
	public void assignPatientToDoctor(String ssn, int docID) throws NoSuchPatient, NoSuchDoctor {
   		if (!patientsMap.containsKey(ssn)) throw new NoSuchPatient();
		if (!doctorsMap.containsKey(docID)) throw new NoSuchPatient();
		Doctor doctor = doctorsMap.get(docID);
		Patient patient = patientsMap.get(ssn);
		patient.setDoctor(doctor);
	}

	/**
	 * Retrieves the id of the doctor assigned to a given patient.
	 * 
	 * @param ssn SSN of the patient
	 * @return id of the doctor
	 * @throws NoSuchPatient in case of not patient with matching SSN
	 * @throws NoSuchDoctor in case no doctor has been assigned to the patient
	 */
	public int getAssignedDoctor(String ssn) throws NoSuchPatient, NoSuchDoctor {
   		if (!patientsMap.containsKey(ssn)) throw new NoSuchPatient();
		Patient patient = patientsMap.get(ssn);
		Doctor doctor = patient.getAssignedDoctor();
		if (doctor == null) throw new NoSuchDoctor();
		return doctor.getBadgeNumber();
	}
	
	/**
	 * Retrieves the patients assigned to a doctor
	 * 
	 * @param id ID of the doctor
	 * @return collection of patient SSNs
	 * @throws NoSuchDoctor in case the {@code id} does not match any doctor 
	 */
	public Collection<String> getAssignedPatients(int id) throws NoSuchDoctor {
   		if (!doctorsMap.containsKey(id)) throw new NoSuchDoctor();
		Doctor doctor = doctorsMap.get(id);
		return doctor.getPatients();
	}
	
	/**
	 * Loads data about doctors and patients from the given stream.
	 * <p>
	 * The text file is organized by rows, each row contains info about
	 * either a patient or a doctor.</p>
	 * <p>
	 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
	 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
	 * followed by badge ID, first name, last name, SSN, and speciality.<br>
	 * The elements on a line are separated by the {@code ';'} character possibly
	 * surrounded by spaces that should be ignored.</p>
	 * <p>
	 * In case of error in the data present on a given row, the method should be able
	 * to ignore the row and skip to the next one.<br>

	 * 
	 * @param reader reader linked to the file to be read
	 * @throws IOException in case of IO error
	 */
	public int loadData(Reader reader) throws IOException {
		return loadData(reader, null);
	}


	/**
	 * Loads data about doctors and patients from the given stream.
	 * <p>
	 * The text file is organized by rows, each row contains info about
	 * either a patient or a doctor.</p>
	 * <p>
	 * Rows containing a patient's info begin with letter {@code "P"} followed by first name,
	 * last name, and SSN. Rows containing doctor's info start with letter {@code "M"},
	 * followed by badge ID, first name, last name, SSN, and speciality.<br>
	 * The elements on a line are separated by the {@code ';'} character possibly
	 * surrounded by spaces that should be ignored.</p>
	 * <p>
	 * In case of error in the data present on a given row, the method calls the
	 * {@link ErrorListener#offending} method passing the line itself,
	 * ignores the row, and skip to the next one.<br>
	 * 
	 * @param reader reader linked to the file to be read
	 * @param listener listener used for wrong line notifications
	 * @throws IOException in case of IO error
	 */
	public int loadData(Reader reader, ErrorListener listener) throws IOException {
		/*	P;Giuseppe;Verdi;VRDGPP76F09B666I 
			M;345;Mario;Bianchi;BNCMRA44C99A320Z;Surgeon */
   		try (BufferedReader br = new BufferedReader(reader)) {
			String readString;
			String line;
			int numReadedLine = 0;
			int numLine = 1;
			while ((line = br.readLine()) != null) {
				readString = line;
				line = line.trim();
				try {
					String[] lineSplitted = line.split(";");
					String type = lineSplitted[0].trim();
					if (type.equals("M")) {
						int docID = Integer.parseInt(lineSplitted[1].trim());
						String firstName = lineSplitted[2].trim();
						String lastName = lineSplitted[3].trim();
						String ssn = lineSplitted[4].trim();
						String speciality = lineSplitted[5].trim();
						this.addDoctor(firstName, lastName, ssn, docID, speciality);
						numReadedLine++;
					} else if (type.equals("P")) {
						String firstName = lineSplitted[1].trim();
						String lastName = lineSplitted[2].trim();
						String ssn = lineSplitted[3].trim();
						this.addPatient(firstName, lastName, ssn);
						numReadedLine++;		
					} else {
						if (listener != null) listener.offending(numLine, readString);
					}
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					if (listener != null) listener.offending(numLine, readString);
				}
				numLine++;
			}
			return numReadedLine;
		}
	}
	
	/**
	 * Retrieves the collection of doctors that have no patient at all.
	 * The doctors are returned sorted in alphabetical order
	 * 
	 * @return the collection of doctors' ids
	 */
	public Collection<Integer> idleDoctors(){
		return doctorsMap.values().stream()
			.filter(d -> d.getPatients().isEmpty())
			.sorted(Comparator.comparing(Doctor::getLastName).thenComparing(Doctor::getFirstName))
			.map(Doctor::getBadgeNumber)
			.toList();
	}

	/**
	 * Retrieves the collection of doctors having a number of patients larger than the average.
	 * 
	 * @return  the collection of doctors' ids
	 */
	public Collection<Integer> busyDoctors(){
		//media pazienti di tutti i dottori
		Double avgPatient = doctorsMap.values().stream()
			.collect(Collectors.averagingInt(d -> d.getPatients().size()));
		return doctorsMap.values().stream()
			.filter(d -> d.getPatients().size() > avgPatient)
			.map(Doctor::getBadgeNumber)
			.toList();
	}

	/**
	 * Retrieves the information about doctors and relative number of assigned patients.
	 * <p>
	 * The method returns list of strings formatted as "{@code ### : ID SURNAME NAME}" where {@code ###}
	 * represent the number of patients (printed on three characters).
	 * <p>
	 * The list is sorted by decreasing number of patients.
	 * 
	 * @return the collection of strings with information about doctors and patients count
	 */
	public Collection<String> doctorsByNumPatients(){
		return doctorsMap.values().stream()
			.sorted(Comparator.comparingInt(d -> ((Doctor) d).getPatients().size()).reversed())
			.map(d -> String.format("%3d : %d %s %s", d.getPatients().size(), d.getBadgeNumber(), d.getLastName(), d.getFirstName()))
			.toList();
	}
	
	/**
	 * Retrieves the number of patients per (their doctor's)  speciality
	 * <p>
	 * The information is a collections of strings structured as {@code ### - SPECIALITY}
	 * where {@code SPECIALITY} is the name of the speciality and 
	 * {@code ###} is the number of patients cured by doctors with such speciality (printed on three characters).
	 * <p>
	 * The elements are sorted first by decreasing count and then by alphabetic speciality.
	 * 
	 * @return the collection of strings with speciality and patient count information.
	 */
	public Collection<String> countPatientsPerSpecialization(){
   		Map<String, Integer> numPatientsForSpecialization = doctorsMap.values().stream()
			.collect(Collectors.groupingBy(Doctor::getSpecialization, Collectors.summingInt(d -> d.getPatients().size())));
		return numPatientsForSpecialization.entrySet().stream()
			.filter(e -> e.getValue() > 0)
			.sorted(Comparator.comparingInt(e -> ((Map.Entry<String, Integer>) e).getValue()).reversed().thenComparing(Comparator.comparing(e -> ((Entry<String,Integer>) e).getKey())))
			.map(e -> String.format("%3d - %s", e.getValue(), e.getKey()))
			.toList();
	}

}
