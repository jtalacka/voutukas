package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {

    @Id
    private String id;

    @ManyToMany(mappedBy = "answers")
    private Set<Option> answers;

    @Column(name = "name")
    private String name;

    public User() {
    }
    public User(String id,String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    public User(Set<Option> answers, String name) {
        this.answers = answers;
        this.name = name;
    }

    public Set<Option> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Option> answers) {
        this.answers = answers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
