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
@JsonPropertyOrder({"time_stamp", "channel_id"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PollDto {
    @JsonProperty("time_stamp")
    private String timeStamp;
    @JsonProperty("channel_id")
    private String channelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PropertiesDto> Properties;

    @JsonIgnoreProperties({"answers", "owner", "poll_id"})
    private List<OptionDto> options;

    private String name;

    private UserDto owner;
}
