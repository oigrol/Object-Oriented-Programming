package com.weather.report.operations;

import java.util.Collection;

import com.weather.report.exceptions.ElementNotFoundException;
import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.exceptions.UnauthorizedException;
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Sensor;
import com.weather.report.repositories.GatewayRepository;
import com.weather.report.repositories.NetworkRepository;
import com.weather.report.repositories.SensorRepository;
import com.weather.report.repositories.UserRepository;

public class TopologyOperationsImplementation implements TopologyOperations{


    private final NetworkRepository networkRepository;
    private final GatewayRepository gatewayRepository;
    private final UserRepository userRepository;
    private final SensorRepository sensorRepository;

    

    public TopologyOperationsImplementation(NetworkRepository networkRepository, GatewayRepository gatewayRepository,
            UserRepository userRepository, SensorRepository sensorRepository) {
        this.networkRepository = networkRepository;
        this.gatewayRepository = gatewayRepository;
        this.userRepository = userRepository;
        this.sensorRepository = sensorRepository;
    }

    /**
     * Returns gateways associated with a network.
     *
     * @param networkCode network code (mandatory)
     * @return collection of gateways linked to the network
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when the network does not exist
     */
    @Override
    public Collection<Gateway> getNetworkGateways(String networkCode)
            throws InvalidInputDataException, ElementNotFoundException {
        Network network = networkRepository.checkNetwork(networkCode);
        return network.getGateways();
    }

    /**
     * Associates a gateway to a network.
     *
     * @param networkCode network code (mandatory)
     * @param gatewayCode gateway code (mandatory)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return updated network
     * @throws ElementNotFoundException  when network or gateway does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     * @throws InvalidInputDataException when mandatory data are invalid
     */
    @Override
    public Network connectGateway(String networkCode, String gatewayCode, String username)
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {
        Network network = networkRepository.checkNetwork(networkCode);
        Gateway gateway = gatewayRepository.checkGateway(gatewayCode);
        userRepository.checkMaintainer(username);

        if (gateway.getNetwork() != null && !gateway.getNetwork().getCode().equals(networkCode)){
            Network nTemp = gateway.getNetwork();
            nTemp.removeGateway(gateway);
            networkRepository.update(nTemp);
        }

        network.addGateway(gateway);
        gateway.setNetwork(network);

        gatewayRepository.update(gateway);
        return networkRepository.update(network);
    }

    /**
     * Removes the association between a gateway and a network.
     *
     * @param networkCode network code (mandatory)
     * @param gatewayCode gateway code (mandatory)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return updated network
     * @throws ElementNotFoundException  when network or gateway does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     * @throws InvalidInputDataException when mandatory data are invalid
     */
    @Override
    public Network disconnectGateway(String networkCode, String gatewayCode, String username)
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {
        Network network = networkRepository.checkNetwork(networkCode);
        Gateway gateway = gatewayRepository.checkGateway(gatewayCode);
        userRepository.checkMaintainer(username);

        if (gateway.getNetwork() == null || !gateway.getNetwork().getCode().equals(networkCode)) throw new InvalidInputDataException("Il gateway non è connesso a quella rete");

        gateway.setNetwork(null);
        network.removeGateway(gateway);

        gatewayRepository.update(gateway);
        return networkRepository.update(network);
    }

    /**
     * Returns sensors associated with a gateway.
     *
     * @param gatewayCode gateway code (mandatory)
     * @return collection of sensors linked to the gateway
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when the gateway does not exist
     */
    @Override
    public Collection<Sensor> getGatewaySensors(String gatewayCode)
            throws InvalidInputDataException, ElementNotFoundException {
        
        Gateway g = gatewayRepository.checkGateway(gatewayCode);
        return g.getSensors();
    }

    /**
     * Associates a sensor to a gateway.
     *
     * @param sensorCode  sensor code (mandatory)
     * @param gatewayCode gateway code (mandatory)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return updated gateway
     * @throws ElementNotFoundException  when sensor or gateway does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     * @throws InvalidInputDataException when mandatory data are invalid
     */
    @Override
    public Gateway connectSensor(String sensorCode, String gatewayCode, String username)
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {
        Sensor s = sensorRepository.checkSensor(sensorCode);
        Gateway g = gatewayRepository.checkGateway(gatewayCode);
        userRepository.checkMaintainer(username);

        if (s.getGateway() != null && !s.getGateway().getCode().equals(gatewayCode)){
            Gateway gTemp = s.getGateway();
            gTemp.removeSensor(s);
            gatewayRepository.update(gTemp);
        }

        g.addSensor(s);
        s.setGateway(g);

        sensorRepository.update(s);
        return gatewayRepository.update(g);
    }


    /**
     * Removes the association between a sensor and a gateway.
     *
     * @param sensorCode  sensor code (mandatory)
     * @param gatewayCode gateway code (mandatory)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return updated gateway
     * @throws ElementNotFoundException  when sensor or gateway does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     * @throws InvalidInputDataException when mandatory data are invalid
     */
    @Override
    public Gateway disconnectSensor(String sensorCode, String gatewayCode, String username)
            throws ElementNotFoundException, UnauthorizedException, InvalidInputDataException {
        Gateway gateway = gatewayRepository.checkGateway(gatewayCode);
        Sensor sensor = sensorRepository.checkSensor(sensorCode);
        userRepository.checkMaintainer(username);

        if (sensor.getGateway() == null || !sensor.getGateway().getCode().equals(gatewayCode)) throw new InvalidInputDataException("Il sensore non è connesso a quel gateway");
        
        sensor.setGateway(null);
        gateway.removeSensor(sensor);

        sensorRepository.update(sensor);
        return gatewayRepository.update(gateway);
    }

}
