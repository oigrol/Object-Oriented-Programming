package com.weather.report.repositories;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Network;

public class NetworkRepository extends CRUDRepository<Network, String>{

    public NetworkRepository() {
        super(Network.class);
    }
    

    /**
     * Checks: 
     *  - netcode not null
     *  - netcode is a String starting with "NET_" followed by 2 decimal digits
     *  - network in repository
     * Retrieve network if it exists, otherwise throw an exception
     * @param code        network unique code (mandatory, must follow {@code NET_##})
     * @return checked Network
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when network does not exist
     */
    public Network checkNetwork(String netCode) throws InvalidInputDataException, ElementNotFoundException{
        if(netCode == null){ throw new InvalidInputDataException("Invalid network code!");
        }else if(!netCode.matches("NET_\\d{2}")){ throw new InvalidInputDataException("Invalid network code format!");}
        Network out = this.read(netCode);
        if(out == null){ throw new ElementNotFoundException("Network "+netCode+" not found!");}
        return out;
    }

    /**
     * Checks:
     *  - code not null
     *  - network already exists
     * @param code        network unique code (mandatory, must follow {@code NET_##})
     * @throws InvalidInputDataException    if network code is invalid
     * @throws IdAlreadyInUseException      if another network with the same code exists
     */
    public void networkExist(String code) throws InvalidInputDataException, IdAlreadyInUseException{
        if(code == null){
            throw new InvalidInputDataException("Network code is null!");
        }else if(!code.matches("NET_\\d{2}")){ 
            throw new InvalidInputDataException("Invalid network code format!");
        }else if(this.read(code) != null){
            throw new IdAlreadyInUseException("Network "+code+" already exists!");
        }else return;
    }

}
