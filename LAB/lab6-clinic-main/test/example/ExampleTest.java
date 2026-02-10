package example;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import clinic.Clinic;
import clinic.NoSuchDoctor;
import clinic.NoSuchPatient;

class ExampleTest {

	@Test
	void testR1() throws NoSuchPatient {
		Clinic clinic = new Clinic();

		clinic.addPatient("Alice", "Green", "ALCGRN");
		String alice = clinic.getPatient("ALCGRN");
		assertNotNull(alice, "Missing patient");
		assertTrue(alice.matches("Green\\s+Alice\\s+\\(\\s*ALCGRN\\s*\\)"), "Wrong patient format for ALCGRN.");
	}

	@Test
	void testR2() throws NoSuchDoctor {
		Clinic clinic = new Clinic();

		clinic.addDoctor("Kate", "Love", "LVOKTA",86,"Physician");

		String kate = clinic.getDoctor(86);
		assertNotNull(kate, "Missing doctor");
		assertTrue(kate.contains("86"), "Missing doctor's id");
		assertTrue(kate.contains("Physician"), "Missing doctor's specialization");
	}

	@Test
	void testR3() throws NoSuchPatient, NoSuchDoctor {
		Clinic clinic = new Clinic();

		clinic.addPatient("Alice", "Green", "ALCGRN");
		clinic.addPatient("Robert", "Smith", "RBTSMT");
		clinic.addPatient("Steve", "Moore", "STVMRE");
		clinic.addPatient("Susan", "Madison", "SNSMDS");
		
		
		clinic.addDoctor("George", "Sun","SNUGRG", 14,"Physician");
		clinic.addDoctor("Kate", "Love", "LVOKTA",86,"Physician");
		
		clinic.assignPatientToDoctor("SNSMDS", 86);
		clinic.assignPatientToDoctor("ALCGRN", 14);
		clinic.assignPatientToDoctor("RBTSMT", 14);
		clinic.assignPatientToDoctor("STVMRE", 14);
		
		int susanDoc = clinic.getAssignedDoctor("SNSMDS");
		assertEquals(86, susanDoc, "Wrong doctor for Susan");

		Collection<String> patients = clinic.getAssignedPatients(14);

		assertNotNull(patients, "Missing George's patients");
		
		assertThrows(NoSuchDoctor.class,
			    ()-> clinic.getAssignedPatients(-1),
			    "Invalid doctor id should raise an exception");

	}
	
	@Test
	void testR4() throws NoSuchPatient, NoSuchDoctor, IOException {
		Clinic clinic = new Clinic();

		int n = clinic.loadData(new FileReader(Path.of("data","data.txt").toString()));
		assertEquals(3, n, "Wrong number of lines");
		
		String gio = clinic.getPatient("RSSGNN33B30F316I");
		assertNotNull(gio, "Patient not read from file");

		String mario = clinic.getDoctor(345);
		assertNotNull(mario, "Doctor not read from file");
	}


	@Test
	void testR5() throws NoSuchPatient, NoSuchDoctor {
		Clinic clinic = new Clinic();

		clinic.addPatient("Alice", "Green", "ALCGRN");
		clinic.addPatient("Robert", "Smith", "RBTSMT");
		clinic.addPatient("Steve", "Moore", "STVMRE");
		clinic.addPatient("Susan", "Madison", "SSNMDS");
		
		clinic.addDoctor("George", "Sun","SNUGRG", 14,"Physician");
		clinic.addDoctor("Kate", "Love", "LVOKTA",86,"Dentist");
		clinic.addDoctor("Marie", "Curie", "MRICRU",88,"Chemist");
		
		clinic.assignPatientToDoctor("SSNMDS", 86);
		clinic.assignPatientToDoctor("ALCGRN", 14);
		clinic.assignPatientToDoctor("RBTSMT", 14);
		clinic.assignPatientToDoctor("STVMRE", 14);

		Collection<Integer> busy = clinic.busyDoctors();
		
		assertNotNull(busy, "Missing busy doctors");
		assertEquals(1, busy.size(), "Too many busy doctors detected");
		assertTrue(busy.contains(14), "Missing busy doctor");
		
		Collection<String> dbp = clinic.doctorsByNumPatients();
		assertNotNull(dbp, "Missing doctors by num patients");
		assertEquals(3, dbp.size(), "Wrong number of doctors per num of patients");

		Collection<String> pps = clinic.countPatientsPerSpecialization();
		assertNotNull(pps, "Missing doctors by num patients");
		assertEquals(2, pps.size(), "Wrong specializations in count patients");
	}
}
