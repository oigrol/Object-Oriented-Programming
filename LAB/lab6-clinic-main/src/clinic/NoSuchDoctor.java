package clinic;

/**
 * Exception class used to notify that a Doctor is not available
 */
public class NoSuchDoctor extends Exception {
	private static final long serialVersionUID = 1L;

	public NoSuchDoctor() {}

	public NoSuchDoctor(int id) {
		super("There is no doctor with id: " + id);
	}
}

