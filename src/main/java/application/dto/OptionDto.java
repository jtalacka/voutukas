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
