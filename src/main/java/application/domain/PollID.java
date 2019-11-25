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

    @Column(name = "time_stamp")
    private String timeStamp;
    
    @Column(name = "channel_id")
    private String channelId;
}
