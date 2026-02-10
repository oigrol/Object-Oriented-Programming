package com.weather.report.model.entities;


import com.weather.report.model.ThresholdType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/// A _threshold_ defines an acceptable limit for the values measured by a sensor.
/// 
/// It **always** consists of a numeric value and a 
/// [ThresholdType][com.weather.report.model.ThresholdType] that the system must apply to decide whether a measurement is anomalous.
@Entity
public class Threshold {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "threshold_val")
  private double value;
  @Enumerated(EnumType.STRING)
  private ThresholdType type;

  public Threshold() {
  }

  public Threshold(double value, ThresholdType type) {
    this.value = value;
    this.type = type;
  }

  public double getValue() {
    return this.value;
  }

  public ThresholdType getType() {
    return this.type;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public void setType(ThresholdType type) {
    this.type = type;
  }

  
}