package com.weather.report.repositories;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Sensor;

public class SensorRepository extends CRUDRepository<Sensor, String>{

  public SensorRepository() {
    super(Sensor.class);
  }

    /**
     * Checks:
     *  - sensorCode not null
     *  - sensor already exists
     * @param sensorCode  sensor unique code (mandatory, must follow {@code S_######})
     * @throws InvalidInputDataException    if sensorCode is invalid
     * @throws IdAlreadyInUseException      if another sensor with the same code exists
     */
    public void sensorExist(String sensorCode) throws IdAlreadyInUseException, InvalidInputDataException{
        if (sensorCode == null) throw new InvalidInputDataException("Mandatory data are missing");
        if (!sensorCode.matches("S_\\d{6}")) throw new InvalidInputDataException("Invalid code format. Must be S_xxxxxx");
        Sensor s = this.read(sensorCode);
        if(s!=null) throw new IdAlreadyInUseException("sensor code already exists");     
    }

    /**
     * Checks: 
     *  - sensorCode not null
     *  - sensorCode is a String starting with "s_" followed by 6
     *  - sensor in repository
     * Retrieve sensor if it exists, otherwise throw an exception
     * @param sensorCode  sensor unique code (mandatory, must follow
     *                    {@code S_######})
     * @return checked Network
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when network does not exist
     */
    public Sensor checkSensor(String sensorCode) throws ElementNotFoundException, InvalidInputDataException{
        if (sensorCode == null) throw new InvalidInputDataException("Mandatory data are missing");
        if (!sensorCode.matches("S_\\d{6}")) throw new InvalidInputDataException("Invalid code format. Must be S_xxxxxx");
        Sensor s = this.read(sensorCode);
        if(s==null) throw new ElementNotFoundException("sensor does not exist");
        return s;
    }
}
