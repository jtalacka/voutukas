package application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDto {
    @JsonProperty("option_id")
    private int id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<UserDto> answers;


    @JsonProperty("poll_id")
    private PollDto pollId;

    @JsonProperty("option_text")
    private String optionText;

    public OptionDto(PollDto poll) {
        this.pollId = poll;
    }

    public OptionDto(PollDto poll, String optionText) {
        this.pollId = poll;
        this.optionText = optionText;
    }

    public OptionDto(Set<UserDto> answers, PollDto poll, String optionText) {
        this.answers = answers;
        this.pollId = poll;
        this.optionText = optionText;
    }
}
