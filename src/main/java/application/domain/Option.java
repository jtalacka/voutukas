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
@Table(name = "optiontable")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "answers",
            joinColumns = { @JoinColumn(name = "option_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> answers = new HashSet<>();

    @ManyToOne
    private Poll pollId;

    @Column(name = "option_text", length = 3000)
    @Size(max = 3000)
    private String optionText;

    public Option(Poll pollId) {
        this.pollId = pollId;
    }

    public Option(Poll pollId, String optionText) {
        this.pollId = pollId;
        this.optionText = optionText;
    }

    public Option(Set<User> answers, Poll pollId, String optionText) {
        this.answers = answers;
        this.pollId = pollId;
        this.optionText = optionText;
    }

}
