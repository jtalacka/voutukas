package application.dto;

import application.domain.Option;
import application.domain.PollID;
import lombok.*;

import java.util.HashSet;
import java.util.List;
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

//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    private Set<Option> options;

    private String name;

    private UserDto owner;

    public PollDto(String timeStamp, String channelId) {
        this.timeStamp = timeStamp;
        this.channelId = channelId;
    }

    public PollDto(String timeStamp, String channelId, String name) {
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        this.name = name;
    }

    public PollDto(String timeStamp, String channelId, String name, UserDto owner) {
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        this.name = name;
        this.owner = owner;
    }
    public PollDto(String timeStamp, String channelId, String name, UserDto owner, Set<PropertiesDto> properties) {
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        this.Properties=properties;
        this.name = name;
        this.owner = owner;
    }
}
