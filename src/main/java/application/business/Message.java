package application.business;

import application.Repositories.OptionRepository;
import application.Repositories.PollRepository;
import application.Repositories.UserRepository;
import application.domain.Option;
import application.dto.OptionDto;
import application.dto.PollDto;
import application.dto.PropertiesDto;
import application.dto.UserDto;
import application.models.CreatePollOptions;
import application.service.OptionService;
import application.service.PollService;
import application.service.PropertiesService;
import application.service.UserService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.chat.ChatDeleteRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersInfoRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatUpdateResponse;
import com.github.seratch.jslack.api.model.block.*;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.BlockElement;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.api.model.view.View;
import com.github.seratch.jslack.api.model.view.ViewState;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

    class Message {
    private String ChannelID;
    private final Slack slack;
    private final String token;
    OptionService optionService =SpringContext.getBean(OptionService.class);
    PollService pollService = SpringContext.getBean(PollService.class);
    PropertiesService pR=SpringContext.getBean(PropertiesService.class);
    UserService userService =SpringContext.getBean(UserService.class);

    public Message(Slack slack,String token) {
        this.slack=slack;
        this.token=token;


    }

    private List<LayoutBlock> ComposeMessage(String question, List<String> answers, CreatePollOptions pollOptions){
        List<LayoutBlock> blocks=new ArrayList<>();


        //AtomicInteger counter=new AtomicInteger(optionRepository.findAll().get(optionRepository.findAll().size()-1).getId()+1);// times out

        OptionDto option = optionService.findLastOption();
        AtomicInteger counter;
        if(option != null)
        counter = new AtomicInteger(option.getId()+2);
        else {
            counter = new AtomicInteger(0);
        }
        //temp
           LayoutBlock Question = SectionBlock.builder()
                   .text(PlainTextObject.builder().text(question).build()).blockId("TEST")
                   .build();
           blocks.add(Question);
           blocks.add(DividerBlock.builder().build());
           answers.forEach(answer -> {
               blocks.add(
                       SectionBlock.builder()
                               .text(PlainTextObject.builder().text(answer).build())
                               .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).value(String.valueOf(counter.get())).build())
                               .build()
               );
               counter.incrementAndGet();
           });
        List<BlockElement> blockElements = new ArrayList<>();

        blocks.add(DividerBlock.builder().build());

        blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Delete Poll").build()).actionId("delete").style("danger").build());
        blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Renew Poll").build()).actionId("renew").build());

        if(pollOptions.allowUsersToAddOptions==true){
            blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Add choices").build()).actionId("choices").build());}

        blocks.add(ActionsBlock.builder().elements(blockElements).build());
        return blocks;
    }

    public void PostInitialMessage(String channelId, String question, List<String> answers, String userId, String slackName, String fullName, CreatePollOptions pollOptions){
        ChatPostMessageResponse postResponse = null;
        try {
            postResponse = slack.methods(token).chatPostMessage(req -> req.channel(channelId).blocks(ComposeMessage(question,answers,pollOptions)));
            userService.insert(new UserDto(userId,slackName,fullName));
            createPollTable(channelId,question,answers,postResponse.getTs(), postResponse.getTs(),userId,slackName,pollOptions);

        } catch (SlackApiException | IOException e) {
            e.printStackTrace();
        }

    }

    public void createPollTable(String channelId,String question,List<String> answers, String timeStamp, String intilialTimeStamp, String userId,String userName, CreatePollOptions pollOptions){

        Set<PropertiesDto> propertiesSet = new HashSet<>();
        if(pollOptions.multivote==true) {
            propertiesSet.add(new PropertiesDto(2));
        }if(pollOptions.anonymous==true) {
            propertiesSet.add(new PropertiesDto(3));
        }if(pollOptions.allowUsersToAddOptions==true) {
            propertiesSet.add(new PropertiesDto(1));
        }
        PollDto poll=new PollDto(timeStamp, intilialTimeStamp,channelId,propertiesSet,question, new UserDto(userId));
        List<OptionDto>option=new LinkedList<>();
        pollService.insert(poll);

        for(String a:answers){
            optionService.insert(new OptionDto(new PollDto(pollService.getPollId(timeStamp,channelId)),a));
        }
    }

    public void OnUserVote(String payload){
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();
        String slackName=pld.getUser().getUsername();

        String realName;
        try{
            realName = slack.methods(token).usersInfo(UsersInfoRequest.builder().user(pld.getUser().getId()).build()).getUser().getRealName();
        }catch (Exception e){
            System.out.println(e);
            realName = pld.getUser().getName();
        }

        String fullName=pld.getUser().getName();
        String voteValue=pld.getActions().get(0).getValue();

        PollDto currentPoll = pollService.findPollByTimeStampAnChannelID(timestamp,channelId);
        Set<PropertiesDto> properties = currentPoll.getProperties();

        userService.insert(new UserDto(userId,slackName,realName));


        try{
            List<OptionDto> optionsDtos = optionService.findAllPollOptions(timestamp,channelId);
            Boolean UserAlreadyVotedOnce=userContains(optionsDtos,userId);
            for (OptionDto o : optionsDtos){

                if(o.getId()==Integer.parseInt(voteValue)){
                    Set<UserDto> answers = o.getAnswers();
                    UserDto u = SetContainsUser(answers,userId);
                    if(u!=null){
                        //    if(!properties.contains(new Properties("multivote"))){
                        answers.remove(u);//}
                    }else {
                        if(!UserAlreadyVotedOnce||propertyTrue("multivote",properties)) {
                            answers.add(new UserDto(userId));
                        }
                    }

                    o.setAnswers(answers);
                    optionService.update(o);
                    break;
                }}
        ;}catch (Exception ignored){


            }

        UpdateMessage(timestamp,channelId);

    //    System.out.println(pld.getActions().get(0).getValue());
    }
    private UserDto SetContainsUser(Set<UserDto> answers,String userID){

        for (UserDto u : answers) {
            if(u.getId().equals(userID)) {
            return u;
            }
            }
        return null;
    }

    private Boolean userContains(List<OptionDto> options, String userID){
        for (OptionDto o : options) {
            for(UserDto u: o.getAnswers())
            if(u.getId().equals(userID)) {
                return true;
            }
        }
        return false;
    }
    public Boolean propertyTrue(String property,Set<PropertiesDto> properties){
        for(PropertiesDto p:properties){
            if(p.getName().equals(property)) {
             return true;
            }
        }
        return false;
    }

    public void UpdateMessage(String timestamp,String channelID){
        PollDto currentPoll= pollService.findPollByTimeStampAnChannelID(timestamp,channelID);

        List<OptionDto> op= optionService.findAllPollOptions(timestamp,channelID);

        List<LayoutBlock> blocks=new ArrayList<>();
        Set<PropertiesDto> properties = currentPoll.getProperties();

        //temporary

        //temp

        LayoutBlock Question=SectionBlock.builder()
                .text(PlainTextObject.builder().text(currentPoll.getName()).build()).blockId("TEST")
                .build();
        blocks.add(Question);
        blocks.add(DividerBlock.builder().build());
        int overallNumber=0;
        for(OptionDto o:op){
            if(o.getAnswers() != null)
            overallNumber+=o.getAnswers().size();
        }
        int finalOverallNumber = overallNumber;
        op.forEach(answer->{
            int temp;
            if(answer.getAnswers() != null)
            temp=answer.getAnswers().size();
            else temp = 0;

            int percentage;
            if(finalOverallNumber==0){
                percentage=0;
            }else{
                percentage=temp*100/finalOverallNumber;
            }

            blocks.add(
                SectionBlock.builder()
                        .text(PlainTextObject.builder().text(answer.getOptionText()).build())
                        .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).value(String.valueOf(answer.getId())).build())
                        .build());
            if(finalOverallNumber != 0)
            blocks.add(
                    SectionBlock.builder().text(MarkdownTextObject.builder().text(PercentangeDisplay(temp,finalOverallNumber)+"  "+String.valueOf(percentage)+"% ("+String.valueOf(temp)+")").build()).build());
            if(!propertyTrue("anonymous",properties) && answer.getAnswers() != null){
                blocks.add(
                        SectionBlock.builder().text(PlainTextObject.builder().text(UserBuilder(answer.getAnswers())).build()).build());

             }

        });
        List<BlockElement> blockElements = new ArrayList<>();


        blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Delete Poll").build()).actionId("delete").style("danger").build());
        blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Renew Poll").build()).actionId("renew").build());

        if(propertyTrue("allowUsersToAddOptions",properties)){
            blockElements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Add choices").build()).actionId("choices").build());}
        blocks.add(DividerBlock.builder().build());

        blocks.add(ActionsBlock.builder().elements(blockElements).build());

        try {
            ChatUpdateResponse um = slack.methods(token).chatUpdate(req -> req.channel(currentPoll.getChannelId()).ts(currentPoll.getTimeStamp()).blocks(blocks));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }


    }
    public String PercentangeDisplay(int current,int overall){
        String Symbols="`";
        int lineLenght=30;
        if(overall!=0){
            int calculator=lineLenght*current/overall;
        for(int i=0;i<calculator;i++)
        {
            Symbols+="█";
        }
        for(int i=calculator;i<lineLenght-3;i++){
            Symbols+="⠀";
        }

        }else{
            for(int i=0;i<lineLenght-8;i++)
            {
                Symbols+="⠀";
            }

        }

        return Symbols+"`";

    }
    public String UserBuilder(Set<UserDto>users){
     String user=" ";

        for (UserDto u : users) {

            user +="<@"+u.getSlackName()+">";
        }


        return user;
    }


    public void OnPollDelete(String payload) {
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();

        PollDto currentPoll= pollService.findPollByTimeStampAnChannelID(timestamp,channelId);
        UserDto owner = currentPoll.getOwner();

        UserDto currentUser = userService.findUserByID(userId);

        if(currentUser.getId() != owner.getId())
        {
            new SlackManager().handleMessageDeletionAction(payload,currentPoll.getOwner().getSlackName());
        }
        else
        {
            Slack slack = new Slack();
            try {
                slack.methods().chatDelete(
                        ChatDeleteRequest.builder().token(token).channel(channelId).ts(timestamp).build()
                );
            } catch (IOException | SlackApiException e) {
                e.printStackTrace();
            }
            pollService.deletePollById(currentPoll.getTimeStamp(),currentPoll.getChannelId());
        }


    }

    public void OnPolRenew(String payload) {
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();
        Boolean noAnswers = true;

        PollDto currentPoll= pollService.findPollByTimeStampAnChannelID(timestamp,channelId);

        try {
            slack.methods().chatDelete(
                    ChatDeleteRequest.builder().token(token).channel(channelId).ts(timestamp).build()
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }

        List<OptionDto> optionDtos = optionService.findAllPollOptions(currentPoll.getTimeStamp(),currentPoll.getChannelId());

        List<String> options = new ArrayList<>();
        for(OptionDto optionDto : optionDtos) {
            if (optionDto.getAnswers().size() > 0)
                noAnswers = false;
            options.add(optionDto.getOptionText());
        }

        CreatePollOptions pollProperties = new CreatePollOptions();
        currentPoll.getProperties().forEach(propertiesDto -> {
            switch (propertiesDto.getName()){
                case "anonymous":
                    pollProperties.anonymous = true;
                    break;
                case "multivote":
                    pollProperties.multivote = true;
                    break;
                case "allowUsersToAddOptions":
                    pollProperties.allowUsersToAddOptions= true;
                    break;
            }
        });

        ChatPostMessageResponse postResponse = null;
        try {
            postResponse = slack.methods(token).chatPostMessage(req -> req.channel(currentPoll.getChannelId()).blocks(ComposeMessage(currentPoll.getName(),options,pollProperties)));
            currentPoll.setTimeStamp(postResponse.getTs());
            pollService.insert(currentPoll);
        } catch (SlackApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(postResponse);
        }

        if(!noAnswers)
        UpdateMessage(currentPoll.getTimeStamp(),currentPoll.getChannelId());

    }

    public void OnOptionCreation(String payload) {
        ViewSubmissionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, ViewSubmissionPayload.class);
        View view = pld.getView();
        ViewState state = pld.getView().getState();

        List<Map<String, ViewState.Value>> stateValuesList = new LinkedList<>(state.getValues().values());
        String newOption = stateValuesList.get(0).values().stream().findFirst().get().getValue();

        String[] tsAndChannelId = view.getPrivateMetadata().split("&",2);

        optionService.insert(new OptionDto(new PollDto(pollService.getPollId(tsAndChannelId[0],tsAndChannelId[1])),newOption));
        UpdateMessage(tsAndChannelId[0],tsAndChannelId[1]);
    }

}
