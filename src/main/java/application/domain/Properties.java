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
@Table(name = "Properties")
public class Properties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy="Properties")
    private Set<Poll> polls= new HashSet<>();

    @Column(name = "name")
    private String name;

    public Properties(String name) {
        this.name = name;
    }

    public Properties(Set<Poll> pollProperties, String name) {
            this.polls = pollProperties;
        this.name = name;
    }
}

