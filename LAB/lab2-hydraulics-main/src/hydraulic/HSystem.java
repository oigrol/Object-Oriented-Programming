package hydraulic;

/**
 * Main class that acts as a container of the elements for
 * the simulation of a hydraulics system
 * 
 */
public class HSystem {
	static final int MAX_SIZE = 100;
	private Element[] elements = new Element[MAX_SIZE];
	private int size = 0;

// R1
	/**
	 * Adds a new element to the system
	 * 
	 * @param elem the new element to be added to the system
	 */
	public void addElement(Element elem){
		if (elem == null || size >= MAX_SIZE) {
			return;
		}
		//confronto i nomi con equals per verificare che ogni elemento sia aggiunto al più una volta
		for (int i=0; i<size; i++) {
			if (elements[i].equals(elem)) { //override in Element: confronta i nomi
				return;
			}
		}

		elements[size++] = elem;
	}

	/**
	 * returns the number of elements currently present in the system
	 * 
	 * @return count of elements
	 */
	public int size() {
        return size;
    }

	/**
	 * returns the element added so far to the system
	 * 
	 * @return an array of elements whose length is equal to 
	 * 							the number of added elements
	 */
	public Element[] getElements(){
		Element[] array = new Element[size];
		if (size >= 0) {
			System.arraycopy(elements, 0, array, 0, size);
		}
		return array;
	}

// R4
	/**
	 * Starts the simulation of the system.
	 * The notifications about the simulations are sent
	 * to an observer object
	 * Before starting simulation, the parameters of the
	 * elements in the system must be defined
	 * 
	 * @param observer the observer receiving notifications
	 */
	public void simulate(SimulationObserver observer){
		simulate(observer, false);
		//modificato dopo aver letto R7
	}


// R6
	/**
	 * Deletes a previously added element 
	 * with the given name from the system
	 */
	public boolean deleteElement(String name) {
		int index = -1;
		Element elemDeleted = null;
		//trovo elemento da eliminare
		for (int i=0; i<size; i++) {
			if (name.equals(elements[i].getName())) {
				index = i;
				elemDeleted = elements[i];
				break;
			}
		}
		
		//se elemento non trovato -> false
		if (elemDeleted == null) {
			return false;
		}

		if (elemDeleted instanceof Split || elemDeleted instanceof Multisplit) {
			int numOutputConnected=0;
			Element[] outputs = elemDeleted.getOutputs();
			for (int i=0; i<outputs.length; i++) {
				if (outputs[i] != null) {
					numOutputConnected++;
				}
			}

			if (numOutputConnected > 1) {
				return false;
			}
		}

		//in tutti gli altri casi, rimuovo elemento
		Element input = elemDeleted.getInput(), output = elemDeleted.getOutput();
		//split - multisplit -> mi serve anche connectionIndex
		if (input != null) {
			if (input instanceof Split || input instanceof Multisplit) {
				Element[] outOfInputs = input.getOutputs();
				int connectionIndex=-1;
				for (int i=0; i<outOfInputs.length; i++) {
					if (outOfInputs[i] == elemDeleted) {
						connectionIndex = i;
						break;
					}
				}
				if (connectionIndex != -1) {
					input.connect(output, connectionIndex);;
				}
			} else {
				input.connect(output);
			}		
		}

		if (output != null) {
			output.setInput(input);
		}

		for (int i=index; i<size-1; i++) {
			elements[i] = elements[i+1];
		}
		elements[size-1] =  null;
		size--;

		return true;
	}

// R7
	/**
	 * Starts the simulation of the system; if {@code enableMaxFlowCheck} is {@code true},
	 * checks also the elements maximum flows against the input flow.
	 * If {@code enableMaxFlowCheck} is {@code false}  a normal simulation as
	 * the method {@link #simulate(SimulationObserver)} is performed.
	 * Before performing a checked simulation, the max flows of the elements in the
	 * system must be defined.
	 */
	public void simulate(SimulationObserver observer, boolean enableMaxFlowCheck) {
		for (int i=0; i<size; i++) {
			Element elem = elements[i];
			if (elem instanceof Source) {
				elem.propagation(SimulationObserver.NO_FLOW, observer, enableMaxFlowCheck); 
				//avvio propagazione da sorgente con ingresso NO_FLOW perchè sorgente non ha un ingresso
				//con R7 ho modificato propagation in modo che accettasse anche la portata massima
			}
		}
	}

// R8
	/**
	 * creates a new builder that can be used to create a 
	 * hydraulic system through a fluent API 
	 * 
	 * @return the builder object
	 */
    public static HBuilder build() {
		return new HBuilder();
    }
}

