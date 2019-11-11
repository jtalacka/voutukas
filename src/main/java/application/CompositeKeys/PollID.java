package application.CompositeKeys;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PollID implements Serializable {

    @Column(name = "time_stamp")
    private String timeStamp;

    @Column(name = "channel_id")
    private String channelId;

    public PollID() {
    }

    public PollID(String timeStamp, String channelId) {
        this.timeStamp = timeStamp;
        this.channelId = channelId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollID pollID = (PollID) o;
        return Objects.equals(timeStamp, pollID.timeStamp) &&
                Objects.equals(channelId, pollID.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, channelId);
    }
}
