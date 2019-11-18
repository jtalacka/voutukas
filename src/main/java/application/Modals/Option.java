package application.Modals;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    public Option() {
    }

    public Option(Set<User> answers, Poll pollId, String optionText) {
        this.answers = answers;
        this.pollId = pollId;
        this.optionText = optionText;
    }
    public Option( Poll pollId, String optionText) {
        this.pollId = pollId;
        this.optionText = optionText;
    }

    public Poll getPollId() {
        return pollId;
    }

    public void setPollId(Poll pollId) {
        this.pollId = pollId;
    }

    public int getId(){
        return id;
    }

    public Set<User> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<User> answers) {
        this.answers = answers;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
