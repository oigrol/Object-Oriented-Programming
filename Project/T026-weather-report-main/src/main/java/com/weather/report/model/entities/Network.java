package com.weather.report.model.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.Timestamped;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

/// A _monitoring network_ that represents a logical set of system elements.
/// 
/// It may have a list of _operators_ responsible for receiving notifications.
@Entity
public class Network extends Timestamped {
  @Id
  private String code; // NET_ {where _ represents a 2 digits number}
  private String name;
  private String description;
  @ManyToMany (fetch = FetchType.EAGER)
  private Collection<Operator> operators = new ArrayList<Operator>();
  @OneToMany (fetch = FetchType.EAGER, mappedBy = "network")
  private Collection<Gateway> gateways = new ArrayList<Gateway>();
  
  public Network() {}

  public Network(String code, String name, String description, String username) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.setCreatedBy(username);
    this.setCreatedAt(java.time.LocalDateTime.now());
    this.setModifiedBy(null);
    this.setModifiedAt(null);
  }

  public Collection<Operator> getOperators() {
    return operators;
  }

  public void addOperatorToNetwork(Operator operator){
    Optional<Operator> out = operators.stream().filter(g -> g.getEmail().equals(operator.getEmail())).findFirst();
    if(out.isPresent()) return;
    this.operators.add(operator);
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void addGateway(Gateway gateway) throws InvalidInputDataException{
    if(gateway != null){
      if(gateways.stream().filter(g -> g.getCode().equals(gateway.getCode())).count() != 0){
        throw new InvalidInputDataException("Gateway already in network");
      }
      this.gateways.add(gateway);
      return;
    }throw new InvalidInputDataException("Invalid gateway");
  }

  public void removeGateway(Gateway gateway) throws InvalidInputDataException{
    if(gateway != null){
      Optional<Gateway> out = gateways.stream().filter(g -> g.getCode().equals(gateway.getCode())).findFirst();
      if(out.isPresent()){gateways.remove(out.get());return;}
      throw new InvalidInputDataException("Gateway not in network!");
    }throw new InvalidInputDataException("Invalid gateway");
  }

  public Collection<Gateway> getGateways(){
    return this.gateways;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Network network = (Network) o;
    return Objects.equals(code, network.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

}
