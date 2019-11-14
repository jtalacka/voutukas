package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Properties")
public class Properties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(mappedBy="Properties")
    private Set<Poll> polls= new HashSet<>();

    @Column(name = "name")
    private String name;

    public Properties() {
    }

    public Properties(String name) {
        this.name = name;
    }

    public Properties(Set<Poll> pollProperties, String name) {
            this.polls = pollProperties;
        this.name = name;
    }

    public Set<Poll> getPollProperties() {
        return polls;
    }

    public void setPollProperties(Set<Poll> pollProperties) {
        this.polls = pollProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

