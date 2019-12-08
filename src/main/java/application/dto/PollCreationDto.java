package application.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollCreationDto {

    private int id;

    private String initialTimeStamp;

    private String timeStamp;

    private String channelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PropertiesDto> Properties;

//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<Option> options;

    private String name;

    private UserDto owner;

}
