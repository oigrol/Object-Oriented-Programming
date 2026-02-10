package com.weather.report.repositories;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Gateway;

public class GatewayRepository extends CRUDRepository<Gateway, String> {

    private static final String GATEWAY_CODE_FORMAT = "GW_\\d{4}";

    public GatewayRepository() {
        super(Gateway.class);
    }
        
    /**
     * Checks: 
     *  - Check that the gateway code is not null
     *  - Check that the code is a String starting with "GW_" and followed by 4 decimal digits
     *  Retrieve gateway if it exists, otherwise throw an exception
     * @param code gateway code
     * @return checked Gateway
     * @throws InvalidInputDataException if cose is missing / if code is invalid or non-conforming
     * @throws ElementNotFoundException  if gateway is not contained in the system
     */
    public Gateway checkGateway(String code) throws InvalidInputDataException, ElementNotFoundException{
        if(code == null){ throw new InvalidInputDataException("Codice gateway mancante!");
        }else if(!code.matches(GATEWAY_CODE_FORMAT)){ throw new InvalidInputDataException("Il formato del codice del gateway (" + code + ") non è corretto!");}
        Gateway gateway = this.read(code);
        if (gateway == null) throw new ElementNotFoundException("Gateway " + code + " non trovato");
        return gateway;
    }

    /**
     * Checks:
     *  - Check that the gateway code is not null
     *  - gateway already exists
     * @param code gateway code
     * @throws InvalidInputDataException if cose is missing / if code is invalid or non-conforming
     * @throws IdAlreadyInUseException if gateway already exists with that id
     */
    public void gatewayExist(String code) throws InvalidInputDataException, IdAlreadyInUseException{
        if(code == null){ throw new InvalidInputDataException("Codice gateway mancante!");
        }else if(!code.matches(GATEWAY_CODE_FORMAT)){ throw new InvalidInputDataException("Il formato del codice del gateway (" + code + ") non è corretto!");}
        Gateway gateway = this.read(code);
        if (gateway != null) throw new IdAlreadyInUseException("Il gateway " + code + " esiste già");
    }

    /**
     * Check that the parameter code is not null
     * @param code parameter code
     * @throws InvalidInputDataException if code is missing
     */
    public void checkParameterCodeNotNull(String code) throws InvalidInputDataException {
        if (code == null) {
            throw new InvalidInputDataException("Codice parameter mancante");
        }    
    }

}
