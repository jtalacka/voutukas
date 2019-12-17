package application.models;

public class CreatePollOptions {

    public  CreatePollOptions(){
        anonymous = allowUsersToAddOptions = false;
        multivote = true;
    }

    public boolean anonymous;
    public boolean multivote;
    public boolean allowUsersToAddOptions;
}
