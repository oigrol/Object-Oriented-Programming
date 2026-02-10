package com.weather.report.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.regex.PatternSyntaxException;

import com.weather.report.WeatherReport;
import com.weather.report.model.ThresholdType;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.model.entities.Sensor;
import com.weather.report.model.entities.Threshold;
import com.weather.report.repositories.CRUDRepository;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.repositories.NetworkRepository;

/**
 * Service responsible for importing measurements from CSV files and validating
 * them
 * against sensor thresholds, triggering notifications when needed (see README).
 */
public class DataImportingService {

  private static final NetworkRepository repo = new NetworkRepository();
  private static final MeasurementRepository repository = new MeasurementRepository();

  private DataImportingService(){
    // utility class
  }

  /**
   * Reads measurements from CSV files, persists them through repositories and
   * invokes {@link #checkMeasurement(Measurement)} after each insertion. 
   * The time window format and CSV location are defined in the README.
   *
   * @param filePath path to the CSV file to import
   */
  
  public static void storeMeasurements(String filePath) {
    // use this try-with-resources for automatic close of file in case of error
    try (BufferedReader br = new BufferedReader(new FileReader(filePath.replace("%20", " ")))) {
      String line = br.readLine(); // Read header line to skip it

      while ((line = br.readLine()) != null) {
        try {
          String[] data = line.split(",");
          /* data[0] = date        | LocalDateTime   | *
           * data[1] = networkCode | String          | *
           * data[2] = gatewayCode | String          | *
           * data[3] = sensorCode  | String          | *
           * data[4] = value       | Double          | */
          LocalDateTime timestamp = LocalDateTime.parse(data[0], WeatherReport.DATE_TIME_FORMATTER);
          String networkCode = data[1].trim();
          String gatewayCode = data[2].trim();
          String sensorCode = data[3].trim();
          Double value = Double.parseDouble(data[4]);
          // create measurement
          Measurement newMeasurement = new Measurement(networkCode, gatewayCode, sensorCode, value, timestamp);
          // save measurement
          repository.create(newMeasurement);
          // check measurement
          checkMeasurement(newMeasurement);
        } catch (DateTimeParseException | PatternSyntaxException | NumberFormatException e) {
          // in caso di errori ignoro e vado avanti
          System.err.println("Skipping invalid line: " + line);
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + filePath);
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
  }

  /**
   * Validates the saved measurement against the threshold of the corresponding
   * sensor
   * and notifies operators when the value is out of bounds. To be implemented in
   * R1.
   *
   * @param measurement newly stored measurement
   */
  private static void checkMeasurement(Measurement measurement) {
    /***********************************************************************/
    /* Do not change these lines, use currentSensor to check for possible */
    /* threshold violation, tests mocks this db interaction */
    /***********************************************************************/
    CRUDRepository<Sensor, String> sensorRepository = new CRUDRepository<>(Sensor.class);
    Sensor currentSensor = sensorRepository.read().stream()
        .filter(s -> measurement.getSensorCode().equals(s.getCode()))
        .findFirst()
        .orElse(null);
    /***********************************************************************/
    if(currentSensor != null){
      Threshold t = currentSensor.getThreshold();
      if(t != null){
        try {
          Network network = repo.checkNetwork(measurement.getNetworkCode());
          Collection<Operator> ops = network.getOperators();
          String sensorCode = currentSensor.getCode();
          double val = measurement.getValue();
          double lim = t.getValue();
          ThresholdType type = t.getType();
          switch (type) {
            case EQUAL:
              if(val==lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
            case GREATER_THAN:
              if(val>lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
            case GREATER_OR_EQUAL:
              if(val>=lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
            case LESS_THAN:
              if(val<lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
            case LESS_OR_EQUAL:
              if(val<=lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
            case NOT_EQUAL:
              if(val!=lim){
                AlertingService.notifyThresholdViolation(ops, sensorCode);
              }break;
          }
        }catch (Exception e) {
          return;
        }
      }
    }
  }

}
