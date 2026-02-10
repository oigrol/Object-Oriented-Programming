package university;
import java.util.logging.Logger;

/**
 * This class represents a university education system.
 * 
 * It manages students and courses.
 *
 */
public class University {
	private final String name; //non cambia
	private String firstNameRector;
	private String lastNameRector;

	private static final int MAX_STUD = 100;
	private int nStudent;
	private Student[] students;
	private int idStudent;

	private static final int MAX_COURSES = 50;
	private int nCourses;
	private Course[] courses;
	private int idCourse;

	private static final int MAX_STUDXCOURSE = 100;
	private Student[][] studentsForCourse;
	private int[] nStudForCourse;
	private static final int MAX_COURSESXSTUD = 25;
	private Course[][] coursesForStudent;
	private int[] nCoursesForStud;

	private int[][] grades;

// R1
	/**
	 * Constructor
	 * @param name name of the university
	 */
	public University(String name){
		// Example of logging
		// logger.info("Creating extended university object");
		this.name = name; //qui è obbligatorio mettere this, negli altri casi no perchè non c'è ambiguità nomi

		this.students = new Student[MAX_STUD];
		this.nStudent = 0;
		this.idStudent = 10000;
		
		this.courses = new Course[MAX_COURSES];
		this.nCourses = 0;
		this.idCourse = 10;

		this.studentsForCourse = new Student[MAX_COURSES][MAX_STUDXCOURSE];
		this.nStudForCourse = new int[MAX_COURSES];
		this.coursesForStudent = new Course[MAX_STUD][MAX_COURSESXSTUD];
		this.nCoursesForStud = new int[MAX_STUD];

		this.grades = new int[MAX_STUD][MAX_COURSES];
		for (int i=0; i<MAX_STUD; i++) {
			for (int j=0; j<MAX_COURSES; j++) {
				grades[i][j] = -1; //inizializzo tutti i voti a -1
			}
		}
	}
	
	/**
	 * Getter for the name of the university
	 * 
	 * @return name of university
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Defines the rector for the university
	 * 
	 * @param first first name of the rector
	 * @param last	last name of the rector
	 */
	public void setRector(String first, String last){
		firstNameRector = first;
		lastNameRector = last;
	}
	
	/**
	 * Retrieves the rector of the university with the format "First Last"
	 * 
	 * @return name of the rector
	 */
	public String getRector(){
		return firstNameRector + " " + lastNameRector;
	}
	
// R2
	/**
	 * Enrol a student in the university
	 * The university assigns ID numbers 
	 * progressively from number 10000.
	 * 
	 * @param first first name of the student
	 * @param last last name of the student
	 * 
	 * @return unique ID of the newly enrolled student
	 */
	public int enroll(String first, String last){
		if (nStudent < MAX_STUD) {
			int idS = idStudent;
			students[nStudent++] = new Student(idStudent++, first, last);

			logger.info("New student enrolled: " + idS + ", " + first + " " + last);

			return idS;
		}
		else {
			//Raggiunto limite massimo di studenti iscritti all'università. Non accettiamo altri studenti.
			return -1;
		}
	}
	
	/**
	 * Retrieves the information for a given student.
	 * The university assigns IDs progressively starting from 10000
	 * 
	 * @param id the ID of the student
	 * 
	 * @return information about the student
	 */
	public String student(int id){
		for (Student student : students) {
			if (student.getId() == id) {
				return student.toString(); //oppure return student.getId() + " " + student.getFirstName() + " " + student.getLastName(); senza Override
			}
		}
		return "Studente non trovato!"; //null
	}
	
// R3
	/**
	 * Activates a new course with the given teacher
	 * Course codes are assigned progressively starting from 10.
	 * 
	 * @param title title of the course
	 * @param teacher name of the teacher
	 * 
	 * @return the unique code assigned to the course
	 */
	public int activate(String title, String teacher){
		if (nCourses < MAX_COURSES) {
			int idC = idCourse;
			courses[nCourses++] = new Course(idCourse++, title, teacher);

			logger.info("New course activated: " + idC + ", " + title + " " + teacher);

			return idC;
		}
		else {
			//Raggiunto limite massimo di corsi che l'università può offrire. Non accettiamo altri corsi.
			return -1;
		}
	}
	
	/**
	 * Retrieve the information for a given course.
	 * 
	 * The course information is formatted as a string containing 
	 * code, title, and teacher separated by commas, 
	 * e.g., {@code "10,Object Oriented Programming,James Gosling"}.
	 * 
	 * @param code unique code of the course
	 * 
	 * @return information about the course
	 */
	public String course(int code){
		for (Course course : courses) {
			if (course.getId() == code) {
				return course.toString(); //oppure return course.getId() + "," + course.getTitle() + "," + course.getTeacher(); senza Override
			}
		}
		return "Corso non trovato!"; //null
	}
	
// R4
	/**
	 * Register a student to attend a course
	 * @param studentID id of the student
	 * @param courseCode id of the course
	 */
	public void register(int studentID, int courseCode){
		int studIndex = studentID - 10000; //recupero indice di studente (0,99) a partire dal suo id
		int courseIndex = courseCode - 10; //recupero indice di studente (0,49) a partire dal suo id

		for (Student stud : students) {
			if (stud != null && nStudForCourse[courseIndex] <  MAX_STUDXCOURSE && stud.getId() == studentID) {
				//aggiungo lo studente al corso
				studentsForCourse[courseIndex][nStudForCourse[courseIndex]++] = stud;
				break;
			}
		}

		for (Course course : courses) {
			if (course != null && nCoursesForStud[studIndex] < MAX_COURSESXSTUD && course.getId() == courseCode) {
				//aggiungo studente al corso
				coursesForStudent[studIndex][nCoursesForStud[studIndex]++] = course;
				logger.info("Student " + studentID + " signed up for course " + courseCode);
			}
		}
	}
	
	/**
	 * Retrieve a list of attendees.
	 * 
	 * The students appear one per row (rows end with `'\n'`) 
	 * and each row is formatted as describe in in method {@link #student}
	 * 
	 * @param courseCode unique id of the course
	 * @return list of attendees separated by "\n"
	 */
	public String listAttendees(int courseCode){
		int courseIndex = courseCode - 10;
		String attendees = "";

		for (int i=0; i<nStudForCourse[courseIndex]; i++) {
			attendees += studentsForCourse[courseIndex][i].toString() + "\n";
		}

		return attendees;
	}

	/**
	 * Retrieves the study plan for a student.
	 * 
	 * The study plan is reported as a string having
	 * one course per line (i.e. separated by '\n').
	 * The courses are formatted as describe in method {@link #course}
	 * 
	 * @param studentID id of the student
	 * 
	 * @return the list of courses the student is registered for
	 */
	public String studyPlan(int studentID){
		int studIndex = studentID - 10000;
		String plan = "";

		for (int i = 0; i < nCoursesForStud[studIndex]; i++) {
			plan += coursesForStudent[studIndex][i].toString() + "\n";
		}

		return plan;
	}

// R5
	/**
	 * records the grade (integer 0-30) for an exam can 
	 * 
	 * @param studentId the ID of the student
	 * @param courseID	course code 
	 * @param grade		grade ( 0-30)
	 */
	public void exam(int studentId, int courseID, int grade) {
		int studIndex = studentId - 10000;
		int courseIndex = courseID - 10;

		grades[studIndex][courseIndex] = grade;

		logger.info("Student " + studentId + " took an exam in course " + courseID + " with grade " + grade);
	}

	/**
	 * Computes the average grade for a student and formats it as a string
	 * using the following format 
	 * 
	 * {@code "Student STUDENT_ID : AVG_GRADE"}. 
	 * 
	 * If the student has no exam recorded the method
	 * returns {@code "Student STUDENT_ID hasn't taken any exams"}.
	 * 
	 * @param studentId the ID of the student
	 * @return the average grade formatted as a string.
	 */
	public String studentAvg(int studentId) {
		int studIndex = studentId - 10000, courseIndex, sum=0, count=0;
		double avg=0.0;
		
		for (int j = 0; j < nCoursesForStud[studIndex]; j++) {
			courseIndex = coursesForStudent[studIndex][j].getId() - 10;
			if (grades[studIndex][courseIndex] != -1) {
				sum += grades[studIndex][courseIndex];
				count++;
			}
		}
		if (count == 0) {
			return "Student " + studentId + " hasn't taken any exams";
		}
		avg = (double)sum/count;
		return "Student " + studentId + " : " + avg;
	}
	
	/**
	 * Computes the average grades of all students that took the exam for a given course.
	 * 
	 * The format is the following: 
	 * {@code "The average for the course COURSE_TITLE is: COURSE_AVG"}.
	 * 
	 * If no student took the exam for that course it returns {@code "No student has taken the exam in COURSE_TITLE"}.
	 * 
	 * @param courseId	course code 
	 * @return the course average formatted as a string
	 */
	public String courseAvg(int courseId) {
		int courseIndex = courseId - 10, studIndex, sum=0, count=0;
		double avg=0.0;

		for (int j = 0; j < nStudForCourse[courseIndex]; j++) {
			studIndex = studentsForCourse[courseIndex][j].getId() - 10000;
			if (grades[studIndex][courseIndex] != -1) {
				sum += grades[studIndex][courseIndex];
				count++;
			}
		}
		if (count == 0) {
			return "No student has taken the exam in " + courses[courseIndex].getTitle();
		}
		avg = (double)sum/count;
		return "The average for the course " + courses[courseIndex].getTitle() + " is: " + avg;
	}
	

// R6
	/**
	 * Retrieve information for the best students to award a price.
	 * 
	 * The students' score is evaluated as the average grade of the exams they've taken. 
	 * To take into account the number of exams taken and not only the grades, 
	 * a special bonus is assigned on top of the average grade: 
	 * the number of taken exams divided by the number of courses the student is enrolled to, multiplied by 10.
	 * The bonus is added to the exam average to compute the student score.
	 * 
	 * The method returns a string with the information about the three students with the highest score. 
	 * The students appear one per row (rows are terminated by a new-line character {@code '\n'}) 
	 * and each one of them is formatted as: {@code "STUDENT_FIRSTNAME STUDENT_LASTNAME : SCORE"}.
	 * 
	 * @return info on the best three students.
	 */
	public String topThreeStudents() {
		int studIndex, courseIndex, sum, nTakenExam;
		double avg=0.0;
		double[] studentScore;
		Student[] topStudents;


		studentScore = new double[nStudent];
		for (int i=0; i<nStudent; i++) {
			studIndex = students[i].getId() - 10000;
			sum=0;
			nTakenExam=0;
			for (int j=0; j<nCoursesForStud[studIndex]; j++) {
				courseIndex = coursesForStudent[studIndex][j].getId() - 10;
				if (grades[studIndex][courseIndex] != -1) {
					sum += grades[studIndex][courseIndex];
					nTakenExam++;
				}
			}
			if (nTakenExam == 0) {
				studentScore[studIndex] = -1; //no esami sostenuti
			} else {
				avg = (double)sum/nTakenExam;
				studentScore[studIndex] = avg + (((double)nTakenExam/nCoursesForStud[studIndex])*10.0);
			}
		}

		double[] topScores = {-1.0,-1.0,-1.0};
		topStudents = new Student[3];

		for (int i=0; i<nStudent; i++) {
			if (studentScore[i] > topScores[0]) {
				topScores[2] = topScores[1];
				topScores[1] = topScores[0];
				topScores[0] = studentScore[i];

				topStudents[2] = topStudents[1];
				topStudents[1] = topStudents[0];
				topStudents[0] = students[i];
			} else if (studentScore[i] >topScores[1]) {
				topScores[2] = topScores[1];
				topScores[1] = studentScore[i];

				topStudents[2] = topStudents[1];
				topStudents[1] = students[i];
			} else if (studentScore[i] > topScores[2]) {
				topScores[2] = studentScore[i];

				topStudents[2] = students[i];
			}
		}

		String scoreString ="";
		for (int i=0; i<3 && topScores[i] != -1; i++) {
			scoreString += topStudents[i].getFirstName() + " " + topStudents[i].getLastName() + " : " + topScores[i] + "\n";
		}

		return scoreString;
	}

// R7
    /**
     * This field points to the logger for the class that can be used
     * throughout the methods to log the activities.
     */
    public static final Logger logger = Logger.getLogger("University");

}