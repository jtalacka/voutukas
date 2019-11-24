package application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public Poll(PollID pollId){
        this.id=pollId;
    }

    public Poll(PollID id, String name, User ownerId) {
        this.id = id;
        this.name = name;
        this.owner = ownerId;
    }
}
