package application.dto;

import application.domain.Poll;
import lombok.*;

import javax.persistence.Column;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String id;

//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<OptionDto> answers;

//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<Poll> polls;

    private String slackName;

    private String fullName;

    public UserDto(String id) {
        this.id = id;
    }

}
