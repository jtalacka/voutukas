package application.dto;

import application.domain.Poll;
import lombok.*;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OptionDto> answers;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Poll> polls;
    
    private String name;

}
