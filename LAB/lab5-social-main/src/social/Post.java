package social;

import jakarta.persistence.*;

@Entity
class Post {
    @Id
    private String id;
    private String text;
    private Long timestamp;

    //Owner side
    @ManyToOne
    @JoinColumn(
        name = "person_code",
        nullable = false
    )
    private Person author;

    Post() {
        // default constructor is needed by JPA
    }

    Post(String id, Person author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPostContent() {
        return text;
    }

    public Person getAuthor() {
        return author;
    }
}
