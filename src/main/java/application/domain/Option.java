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
@Table(name = "optiontable", uniqueConstraints=
@UniqueConstraint(columnNames={"poll_id", "option_text"}))
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "answers",
            joinColumns = { @JoinColumn(name = "option_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> answers = new HashSet<>();



    @ManyToOne(fetch=FetchType.EAGER)
    private Poll poll;

    @Column(name = "option_text", length = 3000)
    @Size(max = 3000)
    private String optionText;

    public Option(Poll poll) {
        this.poll = poll;
    }

    public Option(Poll poll, String optionText) {
        this.poll = poll;
        this.optionText = optionText;
    }

    public Option(Set<User> answers, Poll poll, String optionText) {
        this.answers = answers;
        this.poll = poll;
        this.optionText = optionText;
    }

}
