package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Poll")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pollproperties",
            joinColumns = { @JoinColumn(name = "poll_id") },
            inverseJoinColumns = { @JoinColumn(name = "propertie_id") })
    private Set<Properties> pollProperties = new HashSet<>();

    @Column(name = "name")
    private String name;
    @Column(name = "owner_id")
    private int ownerId;
    @Column(name = "slack_string_id")
    private String slackStringId;

    public Poll() {}

    public Poll (String name, int owenerId, String slackStringId) {
    this.name = name;
    this.ownerId = owenerId;
    this.slackStringId = slackStringId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getSlackStringId() {
        return slackStringId;
    }

    public void setSlackStringId(String slackStringId) {
        this.slackStringId = slackStringId;
    }

    public Set<Properties> getPollProperties() {
        return pollProperties;
    }

    public void setPollProperties(Set<Properties> pollProperties) {
        this.pollProperties = pollProperties;
    }
}
