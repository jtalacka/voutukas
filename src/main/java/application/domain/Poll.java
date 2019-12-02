package application.domain;

import lombok.*;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Properties> Properties = new HashSet<>();
    /*
    * Neturetu but OneToMany su Optionais?
    * */

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
