package hydraulic;

/**
 * Represents a tap that can interrupt the flow.
 * 
 * The status of the tap is defined by the method
 * {@link #setOpen(boolean) setOpen()}.
 */

public class Tap extends Element {

	private Element output;
	private Boolean open = true; //indica se rubinetto aperto (portata out = portata in) o chiuso (portata = 0.0) [default: open=true]

	/**
	 * Constructor
	 * @param name name of the tap element
	 */
	public Tap(String name) {
		super(name);
	}

	/**
	 * Set whether the tap is open or not. The status is used during the simulation.
	 *
	 * @param open opening status of the tap
	 */
	public void setOpen(boolean open){
		this.open = open;
	}

	@Override
	public void connect(Element elem) {
		this.output = elem;
		super.connect(elem, 0); //R6
	}

	@Override
	public Element getOutput() {
		return output;
	}
	
	@Override
	public void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {

		//check R7
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError(this.getClass().getSimpleName(), this.getName(), inputFlow, maxFlow);
		}

		double outputFlow = open ? inputFlow : 0.0; //flusso out=in se open, flusso out=0.0 se not open

		observer.notifyFlow(this.getClass().getSimpleName(), this.getName(), inputFlow, outputFlow);
		//getClass().getSimpleName() restituisce il nome della classe breve (senza package)

		//propaga flow a elemento successivo
		if (output != null) {
			output.propagation(outputFlow, observer, enableMaxFlowCheck);
		}
	}
}
