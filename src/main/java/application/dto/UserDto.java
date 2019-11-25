package application.dto;

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

    private String name;

}
