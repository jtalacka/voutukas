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

    @Column(name = "name")
    private String name;

    public User(String id) {
        this.id = id;
    }


}
