package application.dto;

import application.domain.PollID;
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
public class PollDto {

    private String timeStamp;

    private String channelId;

    private Set<PropertiesDto> Properties;

    private String name;

    private UserDto owner;

}
