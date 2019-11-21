package application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToMany(mappedBy = "answers")
    private Set<Option> answers;

    @Column(name = "name")
    private String name;

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(Set<Option> answers, String name) {
        this.answers = answers;
        this.name = name;
    }

}
