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
@Table(name = "poll", uniqueConstraints=
@UniqueConstraint(columnNames={"channel_id","time_stamp"}))
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "initial_time_stamp")
    private String InitialTimeStamp;

    @Column(name = "time_stamp")
    private String timeStamp;

    @Column(name = "channel_id")
    private String channelId;

    @ManyToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Properties> Properties = new HashSet<>();

    @Column(name = "name", length = 3000)
    @Size(max = 3000)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    public Poll(int id) {
        this.id = id;
    }

    public Poll(String timeStamp, String channelID, @Size(max = 3000) String name) {
        this.InitialTimeStamp = timeStamp;
        this.timeStamp = timeStamp;
        this.channelId = channelID;
        this.name = name;
    }

    public Poll(String timeStamp, String channelID, User owner) {
        this.InitialTimeStamp = timeStamp;
        this.timeStamp = timeStamp;
        this.channelId = channelID;
        this.owner = owner;
    }

    public Poll(String timeStamp, String channelID, @Size(max = 3000) String name, User owner) {
        this.InitialTimeStamp = timeStamp;
        this.timeStamp = timeStamp;
        this.channelId= channelID;
        this.name = name;
        this.owner = owner;
    }
}
