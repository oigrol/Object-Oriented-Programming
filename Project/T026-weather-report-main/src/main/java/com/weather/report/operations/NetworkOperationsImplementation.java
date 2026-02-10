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
import com.weather.report.model.entities.Network;
import com.weather.report.model.entities.Operator;
import com.weather.report.model.entities.User;
import com.weather.report.model.entities.Measurement;
import com.weather.report.reports.NetworkReport;
import com.weather.report.reports.NetworkReportImplementation;
import com.weather.report.repositories.NetworkRepository;
import com.weather.report.repositories.OperatorRepository;
import com.weather.report.repositories.UserRepository;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.services.AlertingService;
import com.weather.report.utils.LocalDateTimeUtils;

public class NetworkOperationsImplementation implements NetworkOperations{

    private final NetworkRepository networkRepository;
    private final UserRepository userRepository;
    private final OperatorRepository operatorRepository;
    private final MeasurementRepository measurementRepository;

    public NetworkOperationsImplementation(NetworkRepository networkRepository, UserRepository userRepository, OperatorRepository operatorRepository, MeasurementRepository measurementRepository){
        this.networkRepository = networkRepository;
        this.userRepository = userRepository;
        this.operatorRepository = operatorRepository;
        this.measurementRepository = measurementRepository;
    }

    /**
     * Creates a network with the provided attributes.
     *
     * @param code        network unique code (mandatory, must follow
     *                    {@code NET_##})
     * @param name        network name (optional)
     * @param description network description (optional)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return created network
     * @throws IdAlreadyInUseException   when a network with the same code exists
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws UnauthorizedException     when user is missing or not authorized
     */
    @Override
    public Network createNetwork(String code, String name, String description, String username)
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
        // controlli
        userRepository.checkMaintainer(username);
        networkRepository.networkExist(code);
        // creazione della rete
        Network output = new Network(code, name, description, username);
        output.setCreatedBy(username);
        output.setCreatedAt(LocalDateTime.now());
        output.setModifiedBy(null);
        output.setModifiedAt(null);
        networkRepository.create(output);
        return output;
    }

    /**
     * Updates name/description of an existing network.
     *
     * @param code        network code (mandatory)
     * @param name        new name (optional)
     * @param description new description (optional)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return updated network
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when the network does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     */
      @Override
    public Network updateNetwork(String code, String name, String description, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        // controlli
        userRepository.checkMaintainer(username);
        Network output = networkRepository.checkNetwork(code);
        // modifica della rete
        output.setName(name);
        output.setDescription(description);
        output.setModifiedBy(username);
        output.setModifiedAt(LocalDateTime.now());
        return networkRepository.update(output);
    }

    /**
     * Deletes a network and triggers deletion notification.
     *
     * @param code     network code (mandatory)
     * @param username user performing the action (mandatory, must be a
     *                 {@code MAINTAINER})
     * @return deleted network
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when the network does not exist
     * @throws UnauthorizedException     when user is missing or not authorized
     */
    @Override
    public Network deleteNetwork(String code, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        // controlli
        userRepository.checkMaintainer(username);
        Network output = networkRepository.checkNetwork(code);
        // eliminazione della rete
        output = networkRepository.delete(code);
        AlertingService.notifyDeletion(username, code, Network.class);
        return output;
    }

    /**
     * Retrieves networks by code. When invoked with no arguments, returns all
     * networks.
     * Unknown codes are ignored.
     *
     * @param codes list of codes to fetch (optional)
     * @return collection of networks found
     */
    @Override
    public Collection<Network> getNetworks(String... codes) {
        if (codes == null || codes.length == 0) return networkRepository.read();
        return Arrays.stream(codes).filter(Objects::nonNull).map(code->networkRepository.read(code)).filter(Objects::nonNull).distinct().toList();
    }

    /**
     * Creates an operator identified by email.
     *
     * @param firstName   operator first name (mandatory)
     * @param lastName    operator last name (mandatory)
     * @param email       operator unique email (mandatory)
     * @param phoneNumber operator phone (optional)
     * @param username    user performing the action (mandatory, must be a
     *                    {@code MAINTAINER})
     * @return created operator
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws IdAlreadyInUseException   when an operator with the same email exists
     * @throws UnauthorizedException     when user is missing or not authorized
     */
    @Override
    public Operator createOperator(String firstName, String lastName, String email, String phoneNumber, String username)
            throws InvalidInputDataException, IdAlreadyInUseException, UnauthorizedException {
        // controlli
        if(firstName == null || lastName == null){ throw new InvalidInputDataException("Mandatory data missing!");}
        User user = userRepository.checkMaintainer(username);
        operatorRepository.operatorExists(email);
        // creazione operator
        Operator out = new Operator(email, firstName, lastName, phoneNumber, user);
        return operatorRepository.create(out);
    }

    /**
     * Associates an existing operator to a network.
     *
     * @param networkCode   target network code (mandatory)
     * @param operatorEmail operator email to link (mandatory)
     * @param username      user performing the action (mandatory, must be a
     *                      {@code MAINTAINER})
     * @return updated network
     * @throws ElementNotFoundException  when network or operator does not exist
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws UnauthorizedException     when user is missing or not authorized
     */
    @Override
    public Network addOperatorToNetwork(String networkCode, String operatorEmail, String username)
            throws ElementNotFoundException, InvalidInputDataException, UnauthorizedException {
        // controlli
        if(networkCode == null || operatorEmail == null || username == null){
            throw new InvalidInputDataException("Mandatory data missing!");
        }
        Network network = networkRepository.checkNetwork(networkCode);
        Operator operator = operatorRepository.checkOperator(operatorEmail);
        userRepository.checkMaintainer(username);
        // aggiunta operatore
        network.addOperatorToNetwork(operator);
        return networkRepository.update(network);
    }

    /**
     * Builds the report for a network in the given interval.
     *
     * @param code      network code (mandatory)
     * @param startDate inclusive lower bound in {@code WeatherReport.DATE_FORMAT}
     *                  (null for no bound)
     * @param endDate   inclusive upper bound in {@code WeatherReport.DATE_FORMAT}
     *                  (null for no bound)
     * @return computed network report
     * @throws InvalidInputDataException when mandatory data are invalid
     * @throws ElementNotFoundException  when the network does not exist
     */
    @Override
    public NetworkReport getNetworkReport(String code, String startDate, String endDate)
            throws InvalidInputDataException, ElementNotFoundException {
        // controlli sugli input
        Network network = networkRepository.checkNetwork(code);
        LocalDateTime checkedStartDate = LocalDateTimeUtils.parseLocalDateTime(startDate, LocalDateTime.MIN);
        LocalDateTime checkedEndDate = LocalDateTimeUtils.parseLocalDateTime(endDate, LocalDateTime.MAX);

        if (checkedStartDate.isAfter(checkedEndDate)) throw new InvalidInputDataException("endDate must be after startDate");
        
        // prendo solo misure relative a questa rete e a questo intervallo di tempo
        List<Measurement> measurementsOfNetworkInInterval = measurementRepository.read().stream()
                .filter(m -> m.getNetworkCode().equals(network.getCode()))
                .filter(m -> !m.getTimestamp().isBefore(checkedStartDate))
                .filter(m -> !m.getTimestamp().isAfter(checkedEndDate)).toList();
        return new NetworkReportImplementation(code, startDate, endDate, measurementsOfNetworkInInterval);
    }

}
