package hydraulic;

/**
 * Represents the sink, i.e. the terminal element of a system
 *
 */
public class Sink extends Element {
	
	/**
	 * Constructor
	 * @param name name of the sink element
	 */
	public Sink(String name) {
		super(name);
	}

	@Override
	public void connect(Element elem) {
		//non ha effetto su oggetto Sink
	}

	@Override
	public void connect(Element elem, int index) {
		//non ha effetto su oggetto Sink
	}


	@Override
	public void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {

		//check R7
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError(this.getClass().getSimpleName(), this.getName(), inputFlow, maxFlow);
		}

		double outputFlow = SimulationObserver.NO_FLOW; //non ha uscita -> è l'ultimo componente del sistema

		observer.notifyFlow(this.getClass().getSimpleName(), this.getName(), inputFlow, outputFlow);
		//getClass().getSimpleName() restituisce il nome della classe breve (senza package)
	}
}
