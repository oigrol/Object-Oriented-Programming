package com.weather.report.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import com.weather.report.model.Timestamped;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/// A _sensor_ measures a physical quantity and periodically sends the corresponding measurements.
/// 
/// A sensor may have a _threshold_ defined by the user to detect anomalous behaviours.
/// 
@Entity
public class Sensor extends Timestamped {

  @Id
  private String code;
  private String name;
  private String description;
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Threshold threshold;
  @ManyToOne(fetch = FetchType.EAGER)
  private Gateway gateway;

  
  public Sensor() {
  }


  public Sensor(String code, String name, String description, String username) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.setCreatedBy(username);
    this.setCreatedAt(LocalDateTime.now());
    this.setModifiedAt(null);
    this.setModifiedBy(null);
  }



  public Threshold getThreshold() {
    return this.threshold;
  }

  public String getCode() {
    return this.code;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setCode(String code) {
    this.code = code;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setThreshold(Threshold threshold) {
    this.threshold = threshold;
  }


  public Gateway getGateway() {
    return gateway;
  }


  public void setGateway(Gateway gateway) {
    this.gateway = gateway;
  }  

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Sensor sensor = (Sensor) o;
    return Objects.equals(code, sensor.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
  
}
