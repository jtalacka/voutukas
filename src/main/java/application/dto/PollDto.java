package application.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {

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

    public PollDto(int id) {
        this.id = id;
    }

    public PollDto(int id, String timeStamp, String channelId) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.channelId = channelId;
    }


    public PollDto(int id, String timeStamp, String channelId, String name) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        this.name = name;
    }

    public PollDto(String timeStamp, String initialTimeStamp, String channelId, Set<PropertiesDto> properties, String name, UserDto owner) {
        this.initialTimeStamp = initialTimeStamp;
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        Properties = properties;
        this.name = name;
        this.owner = owner;
    }

    public PollDto(int id, String timeStamp, String channelId, String name, UserDto owner) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.channelId = channelId;
        this.name = name;
        this.owner = owner;
    }
}
