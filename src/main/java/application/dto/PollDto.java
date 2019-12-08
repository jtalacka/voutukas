package application.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"time_stamp", "channel_id", "name", "owner", "options"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PollDto {

    private int id;

    private String initialTimeStamp;

    @JsonProperty("time_stamp")
    private String timeStamp;
    @JsonProperty("channel_id")
    private String channelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PropertiesDto> Properties; //Not set automatically

    @JsonIgnoreProperties({"answers", "owner", "poll_id"})
    private List<OptionDto> options;

    @JsonProperty("poll_question")
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
