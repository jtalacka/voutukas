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

    @Column(name = "name",unique=true)
    private String name;

    public Properties(String name) {
        this.name = name;
    }
}

