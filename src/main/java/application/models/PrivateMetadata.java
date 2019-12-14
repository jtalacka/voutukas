package application.models;

public class PrivateMetadata {
    public CreatePollOptions options;
    public String channelId;

    public PrivateMetadata(CreatePollOptions options,String channelId){
        this.options=options;
        this.channelId=channelId;
    }
}

