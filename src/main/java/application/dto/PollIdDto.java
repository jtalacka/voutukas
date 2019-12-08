package application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollIdDto {

    @JsonProperty("time_stamp")
    private String timeStamp;

    @JsonProperty("channel_id")
    private String channelId;
}