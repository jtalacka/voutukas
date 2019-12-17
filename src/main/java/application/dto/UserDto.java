package application.dto;

import application.domain.Poll;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @JsonProperty("user_id")
    private String id;

    private String slackName;

    private String fullName;

    public UserDto(String id) {
        this.id = id;
    }

}
