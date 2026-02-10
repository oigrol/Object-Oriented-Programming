package hydraulic;

/**
 * Represents a split element, a.k.a. T element
 * 
 * During the simulation each downstream element will
 * receive a stream that is half the input stream of the split.
 */

public class Split extends Element {

	private Element[] outputs = new Element[2];

	/**
	 * Constructor
	 * @param name name of the split element
	 */
	public Split(String name) {
		super(name);
	}

	@Override
	public void connect(Element elem) {
		connect(elem, 0); //se non specifico uscita lo collego alla prima uscita
	}

	@Override
	public void connect(Element elem, int index) {
		if (index >= 0 && index < outputs.length) {
			this.outputs[index] = elem; //a uscita index connetto come ingresso elem -> 0 prima uscita / 1 seconda uscita
			super.connect(elem, index); //R6 -> imposta ingresso di elem
		} 
	}
	
	@Override
	public Element getOutput() {
        return (outputs.length > 0) ? outputs[0] : super.getOutput(); 
	}

	@Override
	public Element[] getOutputs() {
		return outputs;
	}

	@Override
	public void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {

		//check R7
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError(this.getClass().getSimpleName(), this.getName(), inputFlow, maxFlow);
		}

		double outputFlow = inputFlow / 2.0; //Per i raccordi a T la portata in ingresso viene ripartita equamente tra le due uscite.

		double[] outputFlows = new double[outputs.length];
		for (int i=0; i<outputs.length; i++) {
			outputFlows[i] = (outputs[i] != null) ? outputFlow : SimulationObserver.NO_FLOW;
		}

		observer.notifyFlow(this.getClass().getSimpleName(), this.getName(), inputFlow, outputFlows);
		//getClass().getSimpleName() restituisce il nome della classe breve (senza package)

		//propaga flow a elemento successivo
		for (int i=0; i < 2; i++) {
			if (outputs[i] != null) {
				outputs[i].propagation(outputFlows[i], observer, enableMaxFlowCheck);
			}
		}
	}
}
