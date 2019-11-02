package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "answers",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "option_id") })
    private Set<Option> answers = new HashSet<>();

    @Column(name = "name")
    private String name;


    public User() {
    }

    public User(String name) {
        name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public Set<Option> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Option> answers) {
        this.answers = answers;
    }
}
