package social;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "Groups") //lasciare group fa andare interrogazioni al db in conflitto
class Group {
    @Id
    private String name;

    //Owner side -> proprietario della relazione -> definisco join table
    @ManyToMany(fetch = FetchType.EAGER) //carica anche lista dei membri del gruppo quando interroga il gruppo
    @JoinTable(
        name = "group_members",
        joinColumns = @JoinColumn(name = "group_name"),
        inverseJoinColumns = @JoinColumn(name = "person_code")
    )
    private Set<Person> memberSet = new HashSet<>();

    Group() {
        // default constructor is needed by JPA
    }

    Group(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void addMember(Person person) {
        memberSet.add(person); //registro per ogni gruppo, le persone che vi sono iscritte
        person.getGroups().add(this); //registro per ogni persona i gruppi a cui è iscritto
    }
    public Collection<Person> getMembers() {
        return memberSet;
    }
}
