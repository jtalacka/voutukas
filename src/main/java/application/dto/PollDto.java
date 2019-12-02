package application.dto;

import application.domain.PollID;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"time_stamp", "channel_id", "name", "owner", "options"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PollDto {
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

    @JsonIgnoreProperties({"answers"})
    private UserDto owner;
}
