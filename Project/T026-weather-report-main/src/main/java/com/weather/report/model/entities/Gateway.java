package com.weather.report.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.Timestamped;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/// A _gateway_ groups multiple devices that monitor the same physical quantity.  
/// 
/// It can be configured through parameters that provide information about its state or values needed for interpreting the measurements.
@Entity
public class Gateway extends Timestamped {
  @Id
  private String code; 

  private String name;
  private String description;

  //tutti i salvataggi applicati al gateway sono applicati anche ai parametri -> cascade
  //quando carico un gateway, carica anche tutti i suoi parametri figli -> fetch
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "gateway_code") //inserisce colonna di riferimento a gateway per sapere a chi si riferiscono i parametri
  private Collection<Parameter> parameters = new ArrayList<>();

  @ManyToOne (fetch = FetchType.EAGER)
  private Network network;
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "gateway")
  private List<Sensor> sensors = new ArrayList<>();

  public Gateway() {
    // default constructor is needed by JPA
  }

  public Gateway(String code, String name, String description, String username) {
    this.code = code;
    this.name = name;
    this.description = description;
    //salvo metadati di Timestamped per tracciare creazione gateway
    this.setCreatedBy(username);
    this.setCreatedAt(LocalDateTime.now());
    this.setModifiedAt(null);
    this.setModifiedBy(null);
  }

  public Collection<Parameter> getParameters() {
    return parameters;
  }

  public Parameter getParameter(String codeParameter) {
    return this.parameters.stream()
      .filter(p -> p.getCode().equals(codeParameter))
      .findFirst().orElse(null);
  }

  public void addParameter(Parameter parameter) {
    this.parameters.add(parameter);
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Sensor> getSensors() {
    return sensors;
  }

  public void addSensor(Sensor sensor) throws InvalidInputDataException{
    if(sensor != null){
      if(sensors.stream().filter(s -> s.getCode().equals(sensor.getCode())).count() != 0){
        throw new InvalidInputDataException("Sensor already in gateway");
      }
      this.sensors.add(sensor);
      return;
    }throw new InvalidInputDataException("Invalid sensor");
  }

  public void removeSensor(Sensor sensor) throws InvalidInputDataException {
    if(sensor != null){
      Optional<Sensor> out = sensors.stream().filter(g -> g.getCode().equals(sensor.getCode())).findFirst();
      if(out.isPresent()){sensors.remove(out.get());return;}
      throw new InvalidInputDataException("Sensor not in gateway!");
    }throw new InvalidInputDataException("Invalid sensor");
  }

  public Network getNetwork() {
    return network;
  }

  public void setNetwork(Network network) {
    this.network = network;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Gateway gateway = (Gateway) o;
    return Objects.equals(code, gateway.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
  
}
