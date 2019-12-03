package application.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

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
