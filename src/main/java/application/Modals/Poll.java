package application.Modals;

import application.CompositeKeys.PollID;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "poll")
public class Poll {

    @EmbeddedId
    private PollID id;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Properties> Properties = new HashSet<>();

    @Column(name = "name")
    private String name;

    @ManyToOne
    private User owner;

    public Poll() {}

    public Poll(PollID id, String name, User ownerId) {
        this.id = id;
        this.name = name;
        this.owner = ownerId;
    }
    public Poll(PollID pollId){
        this.id=pollId;
    }

    public Poll(PollID id, Set<Properties> pollProperties, String name, User ownerId) {
        this.id = id;
        this.Properties = pollProperties;
        this.name = name;
        this.owner = ownerId;
    }

    public PollID getId() {
        return id;
    }

    public void setId(PollID id) {
        this.id = id;
    }

    public Set<Properties> getPollProperties() {
        return Properties;
    }

    public void setPollProperties(Set<Properties> pollProperties) {
        this.Properties = pollProperties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwnerId() {
        return owner;
    }

    public void setOwnerId(User ownerId) {
        this.owner = ownerId;
    }
}
