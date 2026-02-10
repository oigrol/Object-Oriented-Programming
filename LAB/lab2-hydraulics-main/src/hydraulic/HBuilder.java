package hydraulic;

/**
 * Hydraulics system builder providing a fluent API
 */
public class HBuilder {
    private final HSystem system;
    private Element lastElem; //ultimo elemento aggiunto

    private int outputIndex = 0;

    //imito il comportamento dello stack
    private Element[] parent = new Element[10]; //memorizza elementi split/multisplit genitori
    private int[] index = new int[10]; //memorizza indice di uscita del genitore
    private int  top = -1; //intero che punta alla cima dello 'stack'
    private boolean splitOut = false; 
    //flag che indica se il prossimo linkto deve collegarsi all'uscita dello split(true) o meno(false)

    /*
    * metodi che imitano comportamento stack -> helper
    */
    //mette elemento in cima a stack
    private void pushParent(Element elem) {
        parent[++top] = elem;
        index[top] = outputIndex; // Salva l'indice corrente
        outputIndex = 0; // Resetta l'indice per il prossimo
    }

    //rimuove elemento in cima a stack
    private Element popParent() {
        outputIndex = index[top]; // Ripristina l'indice
        return parent[top--];
    }

    //Ritorna l'elemento in cima (split/multiplit attivo)
    private Element currentParent() {
        return parent[top];
    }

    /*
    * builder
    */
    public HBuilder() {
        this.system = new HSystem();        
    }

    /**
     * Add a source element with the given name
     * 
     * @param name name of the source element to be added
     * @return the builder itself for chaining 
     */
    public HBuilder addSource(String name) {
        Source source = new Source(name);
        system.addElement(source);
        lastElem = source;
        return this;
    }

    /**
     * returns the hydraulic system built with the previous operations
     * 
     * @return the hydraulic system
     */
    public HSystem complete() {
        return this.system;
    }

    private void linkToElement(Element elem) {
        system.addElement(elem);

        if (splitOut) { //mi collego a uscita di un parent
            currentParent().connect(elem, outputIndex);;
        } else {
            lastElem.connect(elem);
        } //collegamento sequenziale
        lastElem = elem;
        splitOut = false;
    }

    /**
     * creates a new tap and links it to the previous element
     * 
     * @param name name of the tap
     * @return the builder itself for chaining 
     */
    public HBuilder linkToTap(String name) {
        Tap tap = new Tap(name);
        linkToElement(tap);
        return this;
    }

    /**
     * creates a sink and link it to the previous element
     * @param name name of the sink
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSink(String name) {
        Sink sink = new Sink(name);
        linkToElement(sink);
        return this;
    }

    /**
     * creates a split and links it to the previous element
     * @param name of the split
     * @return the builder itself for chaining 
     */
    public HBuilder linkToSplit(String name) {
        Split split = new Split(name);
        linkToElement(split);
        return this;
    }

    /**
     * creates a multisplit and links it to the previous element
     * @param name name of the multisplit
     * @param numOutput the number of output of the multisplit
     * @return the builder itself for chaining 
     */
    public HBuilder linkToMultisplit(String name, int numOutput) {
        Multisplit multisplit = new Multisplit(name, numOutput);
        outputIndex = 0;
        linkToElement(multisplit);
        return this;
    }

    /**
     * introduces the elements connected to the first output 
     * of the latest split/multisplit.
     * The element connected to the following outputs are 
     * introduced by {@link #then()}.
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder withOutputs() {
        if (lastElem == null || !(lastElem instanceof Split)) {
            throw new RuntimeException("splitElem not find. This method must follow a split or a multiSplit.");
        }
        pushParent(lastElem);
        splitOut = true;
        //quando uno split o un multisplit viene creato, le differenti uscite
        //possono essere specificate con questo metodo che introduce tutti gli elementi in uscita:
        //poi gli elementi collgati alle uscite sono definiti con i metodi linkTo.. descritti sopra
        return this;     
    }

    /**
     * inform the builder that the next element will be
     * linked to the successive output of the previous split or multisplit.
     * The element connected to the first output is
     * introduced by {@link #withOutputs()}.
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder then() {
        if (top < 0) {
            throw new RuntimeException("This method must be called after withOutputs().");
        }
        splitOut = true; //per il prossimo linkto...
        outputIndex++;
        return this;
    }

    /**
     * completes the definition of elements connected
     * to outputs of a split/multisplit. 
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder done() {
        if (top < 0) {
            throw new RuntimeException("This method must be called after withOutputs().");
        } 
        popParent();
        return this;
    }

    /**
     * define the flow of the previous source
     * 
     * @param flow flow used in the simulation
     * @return the builder itself for chaining 
     */
    public HBuilder withFlow(double flow) {
        if (lastElem instanceof Source) {
            ((Source)lastElem).setFlow(flow);
        }
        return this;
    }

    /**
     * define the status of a tap as open,
     * it will be used in the simulation
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder open() {
        if (lastElem instanceof Tap) {
            ((Tap)lastElem).setOpen(true);
        }
        return this;
    }

    /**
     * define the status of a tap as closed,
     * it will be used in the simulation
     * 
     * @return the builder itself for chaining 
     */
    public HBuilder closed() {
        if (lastElem instanceof Tap) {
            ((Tap)lastElem).setOpen(false);
        }        
        return this;
    }

    /**
     * define the proportions of input flow distributed
     * to each output of the preceding a multisplit
     * 
     * @param props the proportions
     * @return the builder itself for chaining 
     */
    public HBuilder withPropotions(double[] props) {
        if (lastElem instanceof Multisplit) {
            ((Multisplit)lastElem).setProportions(props);
        }
        return this;
    }

    /**
     * define the maximum flow theshold for the previous element
     * 
     * @param max flow threshold
     * @return the builder itself for chaining 
     */
    public HBuilder maxFlow(double max) {
        lastElem.setMaxFlow(max);
        return this;
    }
}
