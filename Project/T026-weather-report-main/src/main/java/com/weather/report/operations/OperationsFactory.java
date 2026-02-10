package com.weather.report.operations;

import com.weather.report.repositories.*;

/**
 * Central factory providing concrete implementations of the operations
 * interfaces.
 * {@link com.weather.report.WeatherReport} delegates to these methods to obtain
 * the correct instances for requirements R1-R4.
 */
public final class OperationsFactory {

  private static final NetworkRepository networkRepository = new NetworkRepository();
  private static final OperatorRepository operatorRepository = new OperatorRepository();
  private static final GatewayRepository gatewayRepository = new GatewayRepository();
  private static final UserRepository userRepository = new UserRepository();
  private static final MeasurementRepository measurementRepository = new MeasurementRepository();
  private static final GatewayOperationsImplementation gatewayOperationsImplementation = new GatewayOperationsImplementation(gatewayRepository, userRepository, measurementRepository);
  private static final SensorRepository sensorRepository = new SensorRepository();
  private static final ThresholdRepository thresholdRepository = new ThresholdRepository();
  private static final SensorOperations sensorOperationsImplementation = new SensorOperationsImplementation(sensorRepository,userRepository,thresholdRepository,measurementRepository);
  private static final TopologyOperations topologyOperationsImplementation = new TopologyOperationsImplementation(networkRepository, gatewayRepository, userRepository, sensorRepository);
  private static final NetworkOperationsImplementation networkOperationsImplementation = new NetworkOperationsImplementation(networkRepository, userRepository, operatorRepository, measurementRepository);
  private OperationsFactory() {
    // utility class
  }

  /**
   * @return implementation of {@link NetworkOperations} configured for R1/R4
   */
  public static NetworkOperations getNetworkOperations() {
    return networkOperationsImplementation;
  }

  /**
   * @return implementation of {@link GatewayOperations} configured for R2/R4
   */
  public static GatewayOperations getGatewayOperations() {
    return gatewayOperationsImplementation;
  }

  /**
   * @return implementation of {@link SensorOperations} configured for R3/R4
   */
  public static SensorOperations getSensorOperations() {
    return sensorOperationsImplementation;
  }

  /**
   * @return implementation of {@link TopologyOperations} configured for R4
   */
  public static TopologyOperations getTopologyOperations() {
    return topologyOperationsImplementation;
  }

}
