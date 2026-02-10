package social;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;

import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Null;

/**
 * Facade class for the social network system.
 * 
 */
public class Social {

  private final PersonRepository personRepository = new PersonRepository();
  private final GroupRepository groupRepository = new GroupRepository();
  private final PostRepository postRepository = new PostRepository();

  /**
   * Creates a new account for a person
   * 
   * @param code    nickname of the account
   * @param name    first name
   * @param surname last name
   * @throws PersonExistsException in case of duplicate code
   */
  public void addPerson(String code, String name, String surname) throws PersonExistsException {
    if (personRepository.findById(code).isPresent()){    // check if db already contains the code
        throw new PersonExistsException();
    }
    Person person = new Person(code, name, surname);    // create the person as a POJO
    personRepository.save(person); // save it to db -> persist -> salva oggetto completamente nuovo che non esiste nel database
  }

  /**
   * Retrieves information about the person given their account code.
   * The info consists in name and surname of the person, in order, separated by
   * blanks.
   * 
   * @param code account code
   * @return the information of the person
   * @throws NoSuchCodeException if a person with that code does not exist
   */
  public String getPerson(String code) throws NoSuchCodeException {
    //avrei potuto anche usare orElseThrow() della classe Optional
    if (personRepository.findById(code).isEmpty()) {
      throw new NoSuchCodeException(); //Se il codice passato come parametro non corrisponde a nessun account
    }
    Person person = personRepository.findById(code).get(); //con get ottengo l'oggetto effettivo da optional
    return person.toString(); 
  }

  /**
   * Define a friendship relationship between two persons given their codes.
   * <p>
   * Friendship is bidirectional: if person A is adding as friend person B, that means
   * that person B automatically adds as friend person A.
   * 
   * @param codePerson1 first person code
   * @param codePerson2 second person code
   * @throws NoSuchCodeException in case either code does not exist
   */
  public void addFriendship(String codePerson1, String codePerson2)
      throws NoSuchCodeException {
    //uso il metodo già implementato in Optional class, ma specifico il costruttore
    // / l'eccezione nella lambda expression
    Person person1 = personRepository.findById(codePerson1)
      .orElseThrow(() -> new NoSuchCodeException());
    Person person2 = personRepository.findById(codePerson2)
      .orElseThrow(() -> new NoSuchCodeException());

    //Friendship is bidirectional
    person1.addFriend(person2);
    person2.addFriend(person1);
    
    personRepository.update(person1); //merge -> oggetto esiste già in db e sto modificando i suoi dati (es: relazioni -> amici)
    personRepository.update(person2);
  }

  /**
   * Retrieve the collection of their friends given the code of a person.
   *
   * @param codePerson code of the person
   * @return the list of person codes
   * @throws NoSuchCodeException in case the code does not exist
   */
  public Collection<String> listOfFriends(String codePerson)
      throws NoSuchCodeException {
    return personRepository.findById(codePerson)
      .orElseThrow(() -> new NoSuchCodeException())
      .getFriends().stream().map(Person::getCode) //con map converto ogni oggetto person nella sua stringa code - oppure: .map(p -> p.getCode())
      .collect(Collectors.toList()); // raccolgo tutto in una lista
  }

  /**
   * Creates a new group with the given name
   * 
   * @param groupName name of the group
   * @throws GroupExistsException if a group with given name does not exist
   */
  public void addGroup(String groupName) throws GroupExistsException {
    if (groupRepository.findById(groupName).isPresent()) { // check if db already contains the name
      throw new GroupExistsException();
    }
    Group group = new Group(groupName);
    groupRepository.save(group);
  }

  /**
   * Deletes the group with the given name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if a group with given name does not exist
   */
  public void deleteGroup(String groupName) throws NoSuchCodeException {
    Group group = groupRepository.findById(groupName).orElseThrow(() -> new NoSuchCodeException());
    groupRepository.delete(group);
  }

  /**
   * Modifies the group name
   * 
   * @param groupName name of the group
   * @throws NoSuchCodeException if the original group name does not exist
   * @throws GroupExistsException if the target group name already exist
   */
  public void updateGroupName(String groupName, String newName) throws NoSuchCodeException, GroupExistsException {
    /*
    Non è possibile rinominare la chiave primaria di un'entità.
    Procedo così:
    Creo un nuovo gruppo con il nuovo nome.
    Copio i membri dal vecchio gruppo al nuovo.
    Salvo il nuovo gruppo.
    Cancello il vecchio gruppo. 
    */
    if (groupRepository.findById(newName).isPresent()) {
      throw new GroupExistsException(); //eccezione se il nuovo nome del gruppo esiste già
    }
    Group group = groupRepository.findById(groupName).orElseThrow(() -> new NoSuchCodeException()); //eccezione se il nome attuale del gruppo non esiste

    //Creo un nuovo gruppo con il nuovo nome.
    Group newGroup = new Group(newName);

    //Copio i membri dal vecchio gruppo al nuovo.
    newGroup.getMembers().addAll(group.getMembers());

    //Salvo il nuovo gruppo
    groupRepository.save(newGroup);

    //Cancello il vecchio gruppo. 
    groupRepository.delete(group);
  }

  /**
   * Retrieves the list of groups.
   * 
   * @return the collection of group names
   */
  public Collection<String> listOfGroups() {
    return groupRepository.findAll().stream().map(Group::getName)
      .collect(Collectors.toList());
  }

  /**
   * Add a person to a group
   * 
   * @param codePerson person code
   * @param groupName  name of the group
   * @throws NoSuchCodeException in case the code or group name do not exist
   */
  public void addPersonToGroup(String codePerson, String groupName) throws NoSuchCodeException {
    Person person = personRepository.findById(codePerson).orElseThrow(() -> new NoSuchCodeException());
    Group group = groupRepository.findById(groupName).orElseThrow(() -> new NoSuchCodeException());
    group.addMember(person);
    groupRepository.update(group);
  }

  /**
   * Retrieves the list of people on a group
   * 
   * @param groupName name of the group
   * @return collection of person codes
   */
  public Collection<String> listOfPeopleInGroup(String groupName) {
    Optional<Group> group = groupRepository.findById(groupName);

    if (group.isEmpty()) {
      return Collections.emptyList();
    }

    return group.get().getMembers().stream().map(Person::getCode)
      .collect(Collectors.toList());
  }

  /**
   * Retrieves the code of the person having the largest
   * group of friends
   * 
   * @return the code of the person
   */
  public String personWithLargestNumberOfFriends() {
    return personRepository.findAll() //list of people
      .stream().max(Comparator.comparingInt(p -> p.getFriends().size()))
      .map(Person::getCode).orElse("");
  }

  /**
   * Find the name of group with the largest number of members
   * 
   * @return the name of the group
   */
  public String largestGroup() {
    return groupRepository.findAll() //list of groups
      .stream().max(Comparator.comparingInt(g -> g.getMembers().size()))
      .map(Group::getName).orElse("");
  }

  /**
   * Find the code of the person that is member of
   * the largest number of groups
   * 
   * @return the code of the person
   */
  public String personInLargestNumberOfGroups() {
    return personRepository.findAll() //list of people
      .stream().max(Comparator.comparingInt(p -> p.getGroups().size()))
      .map(Person::getCode).orElse("");
  }

  // R5

  /**
   * add a new post by a given account
   * 
   * @param authorCode the id of the post author
   * @param text   the content of the post
   * @return a unique id of the post
   */
  public String post(String authorCode, String text) {
    String id = Long.toString(System.currentTimeMillis(), 36) + authorCode;
    Person person = personRepository.findById(authorCode).get();
    Post post = new Post(id, person, text);
    personRepository.findById(authorCode).get()
      .getPost().add(post);
    postRepository.save(post);
    return id;
  }

  /**
   * retrieves the content of the given post
   * 
   * @param pid    the id of the post
   * @return the content of the post
   */
  public String getPostContent(String pid) {
    /*Optional<Post> post = postRepository.findById(pid);

    if (post.isEmpty()) {
      return "";
    }*/

    return postRepository.findById(pid)
      .map(Post::getPostContent).orElse("");
  }

  /**
   * retrieves the timestamp of the given post
   * 
   * @param pid    the id of the post
   * @return the timestamp of the post
   */
  public long getTimestamp(String pid) {
    return postRepository.findById(pid)
      .map(Post::getTimestamp).orElse((long)-1);
  }

  /**
   * returns the list of post of a given author paginated
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts id
   */
  public List<String> getPaginatedUserPosts(String author, int pageNo, int pageLength) {
    Optional<Person> user = personRepository.findById(author);
    if (user.isEmpty()) {
      return Collections.emptyList();
    }
    return user.get().getPost().stream()
      .sorted(Comparator.comparingLong(Post::getTimestamp).reversed()) //ordinamento decrescente per timestamp
      .skip((long)(pageNo-1) * pageLength).limit(pageLength) //prendo pagina corretta
      .map(Post::getId).collect(Collectors.toList()); //mappo lista
  }

  /**
   * returns the paginated list of post of friends.
   * The returned list contains the author and the id of a post separated by ":"
   * 
   * @param author     author of the post
   * @param pageNo     page number (starting at 1)
   * @param pageLength page length
   * @return the list of posts key elements
   */
  public List<String> getPaginatedFriendPosts(String author, int pageNo, int pageLength) {
    Optional<Person> user = personRepository.findById(author);
    if (user.isEmpty()) {
      return Collections.emptyList();
    }
    return user.get().getFriends().stream()
      .flatMap(f -> f.getPost().stream()) //mi restituisce un'unica lista appiattita con tutti i post di tutti gli amici in ordine sparso
      .sorted(Comparator.comparingLong(Post::getTimestamp).reversed())
      .skip((long)(pageNo-1) * pageLength).limit(pageLength)
      .map(p -> p.getAuthor().getCode() + ":" +p.getId()).collect(Collectors.toList());
  }
}