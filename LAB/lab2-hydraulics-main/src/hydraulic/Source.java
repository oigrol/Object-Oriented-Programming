package hydraulic;

/**
 * Represents a source of water, i.e. the initial element for the simulation.
 *
 * Lo status of the source is defined through the method
 * {@link #setFlow(double) setFlow()}.
 */
public class Source extends Element {

	private Element output;
	private double flow; //portata per una sorgente

	/**
	 * constructor
	 * @param name name of the source element
	 */
	public Source(String name) {
		super(name);
	}

	/**
	 * Define the flow of the source to be used during the simulation
	 *
	 * @param flow flow of the source (in cubic meters per hour)
	 */
	public void setFlow(double flow){
		this.flow = flow;
	}

	@Override 
	public void connect(Element elem) {
		this.output = elem; //connette l'uscita di this a ingresso di elem
		super.connect(elem, 0); //R6
	}

	@Override 
	public Element getOutput() {
		return output;
	}

	@Override
	public void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {
		//source non ha bisogno di fare check del flusso in ingresso

		double outputFlow = this.flow;

		observer.notifyFlow(this.getClass().getSimpleName(), this.getName(), inputFlow, outputFlow);
		//getClass().getSimpleName() restituisce il nome della classe breve (senza package)

		//propaga flow a elemento successivo
		if (output != null) {
			output.propagation(outputFlow, observer, enableMaxFlowCheck);
		}
	}

	@Override
	public void setMaxFlow(double maxFlow) {
		//source non ha ingressi -> no effetto
	}
}
