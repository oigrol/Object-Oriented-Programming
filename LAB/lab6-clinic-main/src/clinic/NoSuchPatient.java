package clinic;


/**
 * Exception class used to notify that a Patient is not available
 */
public class NoSuchPatient extends Exception {
	private static final long serialVersionUID = 1L;

	public NoSuchPatient(){}

	public NoSuchPatient(int id){
		super("There is no patient with id: " + id);
	}
}
