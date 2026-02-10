package hydraulic;

/**
 * Represents the generic abstract element of an hydraulics system.
 * It is the base class for all elements.
 *
 * Any element can be connect to a downstream element
 * using the method {@link #connect(Element) connect()}.
 * 
 * The class is abstract since it is not intended to be instantiated,
 * though all methods are defined to make subclass implementation easier.
 */
public abstract class Element {
	private String name;
	protected double maxFlow = Double.POSITIVE_INFINITY;
	//Se non viene fornito alcun valore per il flusso massimo è da considerarsi illimitato.
	protected Element input = null; //R6 -> elemento a monte
	
	protected Element(String name) {
		this.name = name;
	}

	/**
	 * getter method for the name of the element
	 * 
	 * @return the name of the element
	 */
	public String getName() {
		return name;
	}

	/**
	 * Connects this element to a given element.
	 * The given element will be connected downstream of this element
	 * 
	 * In case of element with multiple outputs this method operates on the first
	 * one,
	 * it is equivalent to calling {@code connect(elem,0)}.
	 * 
	 * @param elem the element that will be placed downstream
	 */
	public void connect(Element elem) {
		// does nothing by default
	}

	/**
	 * Connects a specific output of this element to a given element.
	 * The given element will be connected downstream of this element
	 * 
	 * @param elem  the element that will be placed downstream
	 * @param index the output index that will be used for the connection
	 */
	public void connect(Element elem, int index) {
		// does nothing by default
		if (elem != null) {
			elem.setInput(this); //per R6 devo tenere conto che this è l'ingresso di elem
		}
	}

	/**
	 * Retrieves the single element connected downstream of this element
	 * 
	 * @return downstream element
	 */
	public Element getOutput() {
		return null;
	}

	/**
	 * Retrieves the elements connected downstream of this element
	 * 
	 * @return downstream element
	 */
	public Element[] getOutputs() {
		return null;
	}

	//per R6
	//salva ingresso dell'elemento -> elemento a monte
	public void setInput(Element elem) {
		this.input = elem;
	}
	//ritorna ingresso dell'elemento -> è l'opposto di getOutput
	public Element getInput() {
		return this.input;
	}

	/**
	 * Defines the maximum input flow acceptable for this element
	 * 
	 * @param maxFlow maximum allowed input flow
	 */
	public void setMaxFlow(double maxFlow) {
		this.maxFlow = maxFlow;
	}

	public abstract void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck);
	//sfrutta polimorfismo -> riconosce automaticamente la classe in cui viene propagata quindi rispetta il percorso source->tap/split->sink
	//se elemento A è connesso a B, al runtime la JVM verifica di che tipo è B e esegue la propagation corretta
	//con R7 aggiungo anche il parametro booleano enableMaxFlowCheck

	protected static String pad(String current, String down) {
		int n = current.length();
		final String fmt = "\n%" + n + "s";
		return current + down.replace("\n", fmt.formatted(""));
	}

	@Override
	public String toString() {
		String res = "[%s] ".formatted(getName());
		Element[] out = getOutputs();
		if (out != null) {
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < out.length; ++i) {
				if (i > 0)
					buffer.append("\n");
				if (out[i] == null)
					buffer.append("+-> *");
				else
					buffer.append(pad("+-> ", out[i].toString()));
			}
			res = pad(res, buffer.toString());
		}
		return res;
	}

	/*
	 * Assumo che oggetti con stesso nome sono lo stesso oggetto e potranno essere
	 * aggiunti una sola volta
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Element elem) {
			return this.name.equals(elem.name);
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return name.hashCode();
	}

}