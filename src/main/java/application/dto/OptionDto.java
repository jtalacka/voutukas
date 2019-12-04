package application.dto;

import application.domain.Poll;
import application.domain.PollID;
import application.domain.User;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDto {

    private int id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserDto> answers;

    private PollDto poll;

    private String optionText;

    public OptionDto(PollDto poll) {
        this.poll = poll;
    }

    public OptionDto(PollDto poll, String optionText) {
        this.poll = poll;
        this.optionText = optionText;
    }

    public OptionDto(Set<UserDto> answers, PollDto poll, String optionText) {
        this.answers = answers;
        this.poll = poll;
        this.optionText = optionText;
    }
}
