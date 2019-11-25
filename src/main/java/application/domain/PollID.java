package application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PollID implements Serializable {

    @JsonProperty("time_stamp")
    @Column(name = "time_stamp")
    private String timeStamp;

    @JsonProperty("channel_id")
    @Column(name = "channel_id")
    private String channelId;
}
