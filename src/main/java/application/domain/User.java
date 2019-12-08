package application.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User")
public class User {

    @Id
    private String id;

//    @ManyToMany(mappedBy = "answers")
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<Option> answers;

    @Column(name = "slack_name")
    private String slackName;

    @Column(name = "full_name")
    private String fullName;

    public User(String id) {
        this.id = id;
    }


}
