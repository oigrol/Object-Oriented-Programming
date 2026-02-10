package university;

public class Course {
    private String title;
    private String nameTeacher;
    private int id;

    public Course(int id, String title, String nameTeacher) {
        this.id = id;
        this.title = title;
        this.nameTeacher = nameTeacher;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTeacher() {
        return nameTeacher;
    }

    @Override
    public String toString() {
        return id + "," + title + "," + nameTeacher;
    }
}
