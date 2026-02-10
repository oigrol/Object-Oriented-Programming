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
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Parameter;
import com.weather.report.reports.GatewayReport;
import com.weather.report.reports.GatewayReportImplementation;
import com.weather.report.repositories.GatewayRepository;
import com.weather.report.repositories.MeasurementRepository;
import com.weather.report.repositories.UserRepository;
import com.weather.report.services.AlertingService;
import com.weather.report.utils.LocalDateTimeUtils;

public class GatewayOperationsImplementation implements GatewayOperations{

    private final GatewayRepository gatewayRepository;
    private final UserRepository userRepository;
    private final MeasurementRepository measurementRepository; 

    public GatewayOperationsImplementation(GatewayRepository gatewayRepository, UserRepository userRepository,
            MeasurementRepository measurementRepository) {
        this.gatewayRepository = gatewayRepository;
        this.userRepository = userRepository;
        this.measurementRepository = measurementRepository;
    }

    @Override
    public Gateway createGateway(String code, String name, String description, String username)
            throws IdAlreadyInUseException, InvalidInputDataException, UnauthorizedException {
        userRepository.checkMaintainer(username);
        gatewayRepository.gatewayExist(code);
        
        //creo nuovo Gateway
        Gateway gateway = new Gateway(code, name, description, username);
        gatewayRepository.create(gateway);
        return gateway;
    }

    @Override
    public Gateway updateGateway(String code, String name, String description, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        userRepository.checkMaintainer(username);
        Gateway gateway = gatewayRepository.checkGateway(code);

        //aggiorno campi Gateway
        gateway.setName(name);
        gateway.setDescription(description);
        gateway.setModifiedBy(username);
        gateway.setModifiedAt(LocalDateTime.now());

        gatewayRepository.update(gateway);
        return gateway;        
    }

    @Override
    public Gateway deleteGateway(String code, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        userRepository.checkMaintainer(username);
        Gateway gateway = gatewayRepository.checkGateway(code);

        // elimino Gateway esistente
        gatewayRepository.delete(code);
        AlertingService.notifyDeletion(username, code, Gateway.class);

        return gateway;
    }

    @Override
    public Collection<Gateway> getGateways(String... gatewayCodes) {
        if (gatewayCodes == null || gatewayCodes.length == 0) return gatewayRepository.read();
        return Arrays.stream(gatewayCodes).filter(Objects::nonNull).map(code->gatewayRepository.read(code)).filter(Objects::nonNull).distinct().toList();
    }

    @Override
    public Parameter createParameter(String gatewayCode, String code, String name, String description, double value,
            String username)
            throws IdAlreadyInUseException, InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        userRepository.checkMaintainer(username);
        gatewayRepository.checkParameterCodeNotNull(code);
        Gateway gateway = gatewayRepository.checkGateway(gatewayCode);

        //verifico unicità Parameter all'interno del Gateway
        if (gateway.getParameter(code) != null) {
            throw new IdAlreadyInUseException("Il parameter " + code + " associato al gateway " + gatewayCode + " esiste già");
        }

        //creo nuovo Parameter
        Parameter parameter = new Parameter(code, name, description, value);
        gateway.addParameter(parameter);
       

        gatewayRepository.update(gateway);
        return parameter;
    }

    @Override
    public Parameter updateParameter(String gatewayCode, String code, double value, String username)
            throws InvalidInputDataException, ElementNotFoundException, UnauthorizedException {
        userRepository.checkMaintainer(username);
        gatewayRepository.checkParameterCodeNotNull(code);
        Gateway gateway = gatewayRepository.checkGateway(gatewayCode);

        //verifico esistenza Parameter all'interno del Gateway
        Parameter parameter = gateway.getParameter(code);
        if (parameter == null) {
            throw new ElementNotFoundException("Il parameter " + code + " relativo al gateway " + gatewayCode + " non esiste");
        }

        //aggiorno campi Parameter
        parameter.setValue(value);
        

        gatewayRepository.update(gateway);
        return parameter;
    }

    @Override
    public GatewayReport getGatewayReport(String code, String startDate, String endDate)
            throws ElementNotFoundException, InvalidInputDataException {
        Gateway gateway = gatewayRepository.checkGateway(code);
        LocalDateTime startLocalDate = LocalDateTimeUtils.parseLocalDateTime(startDate, LocalDateTime.MIN);
        LocalDateTime endLocalDate = LocalDateTimeUtils.parseLocalDateTime(endDate, LocalDateTime.MAX);
        if (startLocalDate.isAfter(endLocalDate)) throw new InvalidInputDataException("endDate must be after startDate");

        List<Measurement> measurements = getFilteredMeasurements(code, startLocalDate, endLocalDate);
        return new GatewayReportImplementation(gateway, startDate, endDate, measurements);
    }

    /**
     * Retrieve a list of gateway measurements in the requested time interval
     * @param gatewayCode the code of the gateway
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of gateway measurements between startDate and endDate
     */
    private List<Measurement> getFilteredMeasurements(String gatewayCode, LocalDateTime startDate, LocalDateTime endDate) {
        return measurementRepository.read().stream()
                .filter(m -> m.getGatewayCode().equals(gatewayCode)) //filtra solo le misurazioni di quel gateway
                .filter(m -> !m.getTimestamp().isBefore(startDate)) //filtra solo le misurazioni avvenute a partire da [start,..
                .filter(m -> !m.getTimestamp().isAfter(endDate)) //filtra solo le misurazioni avvenute fino a ...end]
                .toList();
    }

}
