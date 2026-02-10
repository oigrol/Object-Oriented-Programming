package com.weather.report.model.entities;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

/// An _operator_ is an entity that receives notifications when a threshold violation is detected.  
@Entity
public class Operator{
  @Id
  private String email;
  private String name;
  private String lastname;
  @Column (nullable = true)
  private String phoneNumber;
  @ManyToMany (fetch = FetchType.EAGER, mappedBy = "operators")
  private Collection<Network> networks = new ArrayList<Network>();
  @OneToOne (fetch = FetchType.LAZY)
  private User user;

  public Operator() {}

  public Operator(String email, String name, String lastname, String phoneNumber, User user) {
    this.user = user;
    this.email = email;
    this.name = name;
    this.lastname = lastname;
    this.phoneNumber = phoneNumber;
  }

  public String getFirstName() {
    return name;
  }

  public String getLastName() {
    return lastname;
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public User getUser() {
    return user;
  }

}
