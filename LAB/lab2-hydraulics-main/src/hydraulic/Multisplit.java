package hydraulic;

/**
 * Represents a multisplit element, an extension of the Split that allows many outputs
 * 
 * During the simulation each downstream element will
 * receive a stream that is determined by the proportions.
 */

public class Multisplit extends Split {

	private Element[] outputs;
	private double[] proportions;

	/**
	 * Constructor
	 * @param name the name of the multi-split element
	 * @param numOutput the number of outputs
	 */
	public Multisplit(String name, int numOutput) {
		super(name);
		outputs = new Element[numOutput];
		proportions = new double[numOutput];

		//inizializzo proportions a valori di default equipartiti tali che la somma sia 1.0
		double defValue = 1.0 / (double)numOutput;
		for (int i=0; i<numOutput; i++) {
			proportions[i] = defValue;
		}
	}

		@Override
	public void connect(Element elem) {
		connect(elem, 0); //se non specifico uscita lo collego alla prima uscita
	}

	@Override
	public void connect(Element elem, int index) {
		if (index >= 0 && index < outputs.length) {
			this.outputs[index] = elem; //collego elem a uscita numero index
			super.connect(elem, index); //R6
		}
	}

	@Override
	public Element getOutput() {
        return (outputs.length > 0) ? outputs[0] : super.getOutput(); 
	}

	@Override
	public Element[] getOutputs() {
		return outputs; 
		//restituisce un array con gli elementi connessi (se no elementi connessi a una certa uscita -> null)
	}
	
	/**
	 * Define the proportion of the output flows w.r.t. the input flow.
	 * 
	 * The sum of the proportions should be 1.0 and 
	 * the number of proportions should be equals to the number of outputs.
	 * Otherwise a check would detect an error.
	 * 
	 * @param proportions the proportions of flow for each output
	 */
	public void setProportions(double... proportions) {
		//Si assuma che il numero di proporzioni passate al metodo sia pari
		//al numero di uscite e che la loro somma sia pari a 1.0.
		this.proportions = proportions;
	}
	

	@Override
	public void propagation(double inputFlow, SimulationObserver observer, boolean enableMaxFlowCheck) {

		//check R7
		if (enableMaxFlowCheck && inputFlow > maxFlow) {
			observer.notifyFlowError(this.getClass().getSimpleName(), this.getName(), inputFlow, maxFlow);
		}

		double outputFlow;

		double[] outputFlows = new double[outputs.length];
		for (int i=0; i<outputs.length; i++) {
			outputFlow = inputFlow * proportions[i];
			outputFlows[i] = (outputs[i] != null) ? outputFlow : SimulationObserver.NO_FLOW;
		}

		observer.notifyFlow(this.getClass().getSimpleName(), this.getName(), inputFlow, outputFlows);
		//getClass().getSimpleName() restituisce il nome della classe breve (senza package)

		//propaga flow a elemento successivo
		for (int i=0; i<outputs.length; i++) {
			if (outputs[i] != null) {
				outputs[i].propagation(outputFlows[i], observer, enableMaxFlowCheck);
			}
		}
	}
}
