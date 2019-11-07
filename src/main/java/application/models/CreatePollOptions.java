package application.models;

public class CreatePollOptions {

    public  CreatePollOptions(){
        anonymous = multivote = allowUsersToAddOptions = false;
    }

    public boolean anonymous;
    public boolean multivote;
    public boolean allowUsersToAddOptions;
}
