package application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @JsonProperty("user_id")
    private String id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OptionDto> answers;

    private String name;

}
