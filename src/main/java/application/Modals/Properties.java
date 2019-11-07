package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Properties")
public class Properties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pollproperties",
            joinColumns = { @JoinColumn(name = "poll_id") },
            inverseJoinColumns = { @JoinColumn(name = "propertie_id") })
    private Set<Poll> pollProperties = new HashSet<>();

    @Column(name = "name")
    private String name;

    public Properties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Poll> getPollProperties() {
        return pollProperties;
    }

    public void setPollProperties(Set<Poll> pollProperties) {
        this.pollProperties = pollProperties;
    }
}
