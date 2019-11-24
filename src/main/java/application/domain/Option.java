package application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private Set<User> answers = new HashSet<>();

    @ManyToOne
    private Poll pollId;

    @Column(name = "option_text")
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
