package application.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Properties> Properties = new HashSet<>();

    @Column(name = "name", length = 3000)
    @Size(max = 3000)
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
