package application.dto;

import application.domain.Poll;
import application.domain.PollID;
import application.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDto {

    private int id;

    private Set<UserDto> answers;

    private PollDto pollId;

    private String optionText;

    public OptionDto(PollDto pollId) {
        this.pollId = pollId;
    }

    public OptionDto(PollDto pollId, String optionText) {
        this.pollId = pollId;
        this.optionText = optionText;
    }

    public OptionDto(Set<UserDto> answers, PollDto pollId, String optionText) {
        this.answers = answers;
        this.pollId = pollId;
        this.optionText = optionText;
    }
}
