package social;

import java.util.*;

import jakarta.persistence.*;

@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;
  
  @ManyToMany(fetch = FetchType.EAGER) //carica anche le entità con cui ha relazione direttamente
  private Set<Person> friendshipSet = new HashSet<>();

  //inverse side -> collegato a Group
  @ManyToMany(mappedBy = "memberSet", fetch = FetchType.EAGER)
  private Set<Group> groups = new HashSet<>();

  //inverse side -> collegato a Post
  @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
  private List<Post> postSet = new ArrayList<>();

  Person() {
    // default constructor is needed by JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {
    return code;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }

  public void addFriend(Person friend) {
    friendshipSet.add(friend);
  }

  public Collection<Person> getFriends() {
    return friendshipSet;
  }

  public Collection<Group> getGroups() {
    return groups;
  }

  public Collection<Post> getPost() {
    return postSet;
  }

  //....
  @Override
  public String toString() {
      return code + " " + name + " " + surname;
  }
}
