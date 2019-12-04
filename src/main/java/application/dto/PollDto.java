package application.dto;

import application.domain.Option;
import application.domain.PollID;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {

    private String timeStamp;

    private String channelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PropertiesDto> Properties;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Option> options;

    private String name;

    private UserDto owner;

}
