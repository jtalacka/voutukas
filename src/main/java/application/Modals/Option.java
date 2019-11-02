package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "optiontable")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "answers",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "option_id") })
    private Set<User> answers = new HashSet<>();

    @Column(name = "poll_id")
    private int pollId;
    @Column(name = "option_text")
    private String optionText;

    public Option() {
    }

    public Option(int pollId, String optionText) {
        this.pollId = pollId;
        this.optionText = optionText;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Set<User> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<User> answers) {
        this.answers = answers;
    }
}
