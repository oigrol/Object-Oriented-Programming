package com.weather.report.operations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.IdAlreadyInUseException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.ThresholdType;
import com.weather.report.model.entities.*;

import com.weather.report.reports.SensorReport;
import com.weather.report.reports.SensorReportImplementation;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.repositories.SensorRepository;
import com.weather.report.repositories.ThresholdRepository;
import com.weather.report.repositories.UserRepository;
import com.weather.report.services.AlertingService;
import com.weather.report.utils.LocalDateTimeUtils;

public class SensorOperationsImplementation implements SensorOperations{

    private final SensorRepository sensorRepository;
    private final UserRepository userRepository;
    private final ThresholdRepository thresholdRepository;
    private final MeasurementRepository measurementRepository;
    

    public SensorOperationsImplementation(SensorRepository sensorRepository, UserRepository userRepository,
            ThresholdRepository thresholdRepository, MeasurementRepository measurementRepository) {
        this.sensorRepository = sensorRepository;
        this.userRepository = userRepository;
        this.thresholdRepository = thresholdRepository;
        this.measurementRepository = measurementRepository;
    }
    

    @Override
    public Sensor createSensor(String code, String name, String description, String username)
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
        
        sensorRepository.sensorExist(code);
        userRepository.checkMaintainer(username);
        if (sensorRepository.read(code) != null) throw new IdAlreadyInUseException("A sensor with the same code exists");
    
        Sensor s = new Sensor(code, name, description, username);
        
        sensorRepository.create(s);

        return s;
    }


    @Override
    public Sensor updateSensor(String code, String name, String description, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
       
        Sensor s = sensorRepository.checkSensor(code);
        userRepository.checkMaintainer(username);
        s.setModifiedAt(LocalDateTime.now());
        s.setModifiedBy(username); 
        s.setDescription(description);
        s.setName(name);

        sensorRepository.update(s);
        return s;
    }

    @Override
    public Sensor deleteSensor(String code, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {

        sensorRepository.checkSensor(code);
        userRepository.checkMaintainer(username);
        Sensor s = sensorRepository.delete(code);
        AlertingService.notifyDeletion(username, code, Sensor.class);
        return s;
    }

    @Override
    public Collection<Sensor> getSensors(String... sensorCodes) {
        if (sensorCodes == null || sensorCodes.length == 0) return sensorRepository.read();
        return Arrays.stream(sensorCodes).filter(Objects::nonNull).map(code->sensorRepository.read(code)).filter(Objects::nonNull).distinct().toList();
    }
    /**
   * Creates a threshold for a sensor.
   *
   * @param sensorCode target sensor code (mandatory)
   * @param type       comparison type (mandatory)
   * @param value      threshold numeric value
   * @param username   user performing the action (mandatory, must be a
   *                   {@code MAINTAINER})
   * @return created threshold
   * @throws InvalidInputDataException when mandatory data are invalid
   * @throws ElementNotFoundException  when the sensor does not exist
   * @throws IdAlreadyInUseException   when a threshold already exists for the
   *                                   sensor
   * @throws UnauthorizedException     when user is missing or not authorized
   */

   @Override
    public Threshold createThreshold(String sensorCode, ThresholdType type, double value, String username)
        throws InvalidInputDataException, ElementNotFoundException, IdAlreadyInUseException, UnauthorizedException {

        if (type == null)  throw new InvalidInputDataException("Threshold type cannot be null");
        Sensor s = sensorRepository.checkSensor(sensorCode);
        userRepository.checkMaintainer(username);
        if (s.getThreshold() != null) throw new IdAlreadyInUseException("This sensor already has a threshold defined");
        Threshold t = new Threshold(value, type);

        thresholdRepository.create(t);
        s.setThreshold(t);
        sensorRepository.update(s);

        return t;
}

    @Override
    public Threshold updateThreshold(String sensorCode, ThresholdType type, double value, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        Sensor s = sensorRepository.checkSensor(sensorCode);
        userRepository.checkMaintainer(username);
        Threshold t = s.getThreshold();
        if(t==null) throw new ElementNotFoundException("Sensor not found");
        if(type!=null) t.setType(type);
        t.setValue(value);
        thresholdRepository.update(t);
        return t;
    }

    @Override
    public SensorReport getSensorReport(String code, String startDate, String endDate)
            throws InvalidInputDataException, ElementNotFoundException {
                
        Sensor s = sensorRepository.checkSensor(code);     
        List<Measurement> sMeasurements = (List<Measurement>) filterMeasurement(startDate, endDate, s);

        return new SensorReportImplementation(code, startDate, endDate, sMeasurements);
    }

    private Collection<Measurement> filterMeasurement(String startDate, String endDate, Sensor s) throws InvalidInputDataException{
        LocalDateTime startLocalDate = LocalDateTimeUtils.parseLocalDateTime(startDate, LocalDateTime.MIN);
        LocalDateTime endLocalDate = LocalDateTimeUtils.parseLocalDateTime(endDate, LocalDateTime.MAX);
        if (startLocalDate.isAfter(endLocalDate)) throw new InvalidInputDataException("endDate must be after startDate");

        return measurementRepository.read().stream()
                .filter(m -> m.getSensorCode().equals(s.getCode()))
                .filter(m -> !m.getTimestamp().isBefore(startLocalDate)) 
                .filter(m -> !m.getTimestamp().isAfter(endLocalDate)) 
                .toList();
    }

}
