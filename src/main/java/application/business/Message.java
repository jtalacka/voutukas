package application.business;

import application.Repositories.OptionRepository;
import application.Repositories.PollRepository;
import application.Repositories.PropertiesRepository;
import application.Repositories.UserRepository;
import application.domain.Option;
import application.domain.Poll;
import application.domain.PollID;
import application.domain.Properties;
import application.domain.User;
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
import com.github.seratch.jslack.api.methods.response.chat.ChatDeleteResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatUpdateResponse;
import com.github.seratch.jslack.api.model.block.*;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.composition.TextObject;
import com.github.seratch.jslack.api.model.block.element.BlockElement;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.api.model.view.View;
import com.github.seratch.jslack.api.model.view.ViewState;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;

import org.hibernate.boot.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
public class Message {
    private String ChannelID;
    private Slack slack;
    private String token;
    public Message(Slack slack,String token) {
        this.slack=slack;
        this.token=token;


    }
    public List<LayoutBlock> ComposeMessage(String question, List<String> answers, CreatePollOptions pollOptions){
        List<LayoutBlock> blocks=new ArrayList<>();

        //temporary
        OptionRepository optionRepository=SpringContext.getBean(OptionRepository.class);

        //AtomicInteger counter=new AtomicInteger(optionRepository.findAll().get(optionRepository.findAll().size()-1).getId()+1);// times out
        AtomicInteger counter=new AtomicInteger(optionRepository.findFirstByOrderByIdDesc().getId()+1);// times out
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
    public void PostInitialMessage(String channelId, String question, List<String> answers, String userId, String userName, CreatePollOptions pollOptions){
        ChatPostMessageResponse postResponse = null;
        try {
            postResponse = slack.methods(token).chatPostMessage(req -> req.channel(channelId).blocks(ComposeMessage(question,answers,pollOptions)));
            createPollTable(channelId,question,answers,postResponse.getTs(),userId,userName,pollOptions);

        } catch (SlackApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createPollTable(String channelId,String question,List<String> answers, String timeStamp,String userId,String userName, CreatePollOptions pollOptions){
        System.out.println(pollOptions.allowUsersToAddOptions+" "+pollOptions.anonymous+" "+pollOptions.multivote);

        PollService pollService=SpringContext.getBean(PollService.class);
        OptionService optionService=SpringContext.getBean(OptionService.class);
        PropertiesService pR=SpringContext.getBean(PropertiesService.class);

        PropertiesService propertiesService=SpringContext.getBean(PropertiesService.class);

        Set<PropertiesDto> propertiesSet = new HashSet<>();
        if(pollOptions.multivote==true) {
            propertiesSet.add(new PropertiesDto(2));
        }if(pollOptions.anonymous==true) {
            propertiesSet.add(new PropertiesDto(3));
        }if(pollOptions.allowUsersToAddOptions==true) {
            propertiesSet.add(new PropertiesDto(1));
        }
        PollDto poll=new PollDto(timeStamp,channelId,question, new UserDto(userId),propertiesSet);
        List<OptionDto>option=new LinkedList<>();
        pollService.insert(poll);

        for(String a:answers){
            optionService.insert(new OptionDto(poll,a));
        }
     /*   User user=new User(userId,userName);
        userRepository.save(user);
        PollID pollId=new PollID(timeStamp,channelId);
        Poll tempPoll=new Poll(pollId, question,user);
        Set<Properties>p=PropertySet(pollOptions);
        tempPoll.setProperties(p);
        for (Properties u : p) {
            pR.save(u);
        }*/
        //pollRepository.save(tempPoll);




    }

    private String PropertyType(CreatePollOptions pollOptions){
        if(pollOptions.multivote==true) {
            return "multivoce";
        }if(pollOptions.anonymous==true) {
            return "anonymous";
        }if(pollOptions.allowUsersToAddOptions==true) {
            return"allowUsersToAddOptions";
        }

        return null;
    }
    public void OnUserVote(String payload){
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        System.out.println(payload);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();
        String username=pld.getUser().getUsername();
        String voteValue=pld.getActions().get(0).getValue();
        PollRepository poll=SpringContext.getBean(PollRepository.class);
        Poll currentPoll=poll.getOne(new PollID(timestamp,channelId));
        Set<Properties> properties=currentPoll.getProperties();
        System.out.println(voteValue);

        OptionRepository optionRepository=SpringContext.getBean(OptionRepository.class);
        PropertiesRepository propertiesRepository=SpringContext.getBean(PropertiesRepository.class);
        UserRepository usr=SpringContext.getBean(UserRepository.class);
       // Set<Properties> properties=
        usr.save(new User(userId,username));

        try{
            List<Option> options=optionRepository.findAllOptionsByPollID(new Poll(new PollID(timestamp,channelId)));
            Boolean UserAlreadyVotedOnce=userContains(options,userId);
            for (Option o : options){

                if(o.getId()==Integer.parseInt(voteValue)){
                    Set<User> answers =o.getAnswers();
                    User u=SetContainsUser(answers,userId);
                    if(u!=null){
                        //    if(!properties.contains(new Properties("multivote"))){
                        answers.remove(u);//}
                    }else {
                        if(!UserAlreadyVotedOnce||propertyTrue("multivote",properties)) {
                            answers.add(usr.getOne(userId));
                        }
                    }

                    o.setAnswers(answers);
                    optionRepository.save(o);
                    break;
                }}
        }catch (Exception e){


            }

        UpdateMessage(timestamp,channelId);

    //    System.out.println(pld.getActions().get(0).getValue());
    }
    private User SetContainsUser(Set<User> answers,String userID){

        for (User u : answers) {
            if(u.getId().equals(userID)) {
            return u;
            }
            }
        return null;
    }
    private Boolean userContains(List<Option> options, String userID){
        for (Option o : options) {
            for(User u: o.getAnswers())
            if(u.getId().equals(userID)) {
                return true;
            }
        }
        return false;
    }
    public Boolean propertyTrue(String property,Set<Properties> properties){
        for(Properties p:properties){
            if(p.getName().equals(property)) {
             return true;
            }
        }
        return false;
    }

    public void UpdateMessage(String timestamp,String channelID){
        PollRepository poll=SpringContext.getBean(PollRepository.class);
        OptionRepository options=SpringContext.getBean(OptionRepository.class);
        Poll currentPoll=poll.getOne(new PollID(timestamp,channelID));
        List<Option> op=options.findAllOptionsByPollID(new Poll(new PollID(timestamp,channelID)));

        List<LayoutBlock> blocks=new ArrayList<>();
        Set<Properties> properties=currentPoll.getProperties();

        //temporary

        //temp

        LayoutBlock Question=SectionBlock.builder()
                .text(PlainTextObject.builder().text(currentPoll.getName()).build()).blockId("TEST")
                .build();
        blocks.add(Question);
        blocks.add(DividerBlock.builder().build());
        int overallNumber=0;
        for(Option o:op){
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
            ChatUpdateResponse um = slack.methods(token).chatUpdate(req -> req.channel(currentPoll.getId().getChannelId()).ts(currentPoll.getId().getTimeStamp()).blocks(blocks));
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
    public String UserBuilder(Set<User>users){
     String user=" ";

        for (User u : users) {

            user +="<@"+u.getName()+">";
        }


        return user;
    }


    public void OnPollDelete(String payload) {
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();

        PollService pollService =SpringContext.getBean(PollService.class);
        PollDto currentPoll= pollService.findPollByID(timestamp,channelId);
        UserDto owner = currentPoll.getOwner();

        UserService userService = SpringContext.getBean(UserService.class);
        UserDto currentUser = userService.findUserByID(userId);

        if(currentUser.getId() != owner.getId())
        {
            new SlackManager().handleMessageDeletionAction(payload,currentPoll.getOwner().getName());
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

        PollService pollService =SpringContext.getBean(PollService.class);
        PollDto currentPoll= pollService.findPollByID(timestamp,channelId);

        try {
            slack.methods().chatDelete(
                    ChatDeleteRequest.builder().token(token).channel(channelId).ts(timestamp).build()
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }

        OptionService optionService =SpringContext.getBean(OptionService.class);
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
            optionDtos.forEach(option -> {
                optionService.insert(new OptionDto(option.getAnswers(),new PollDto(currentPoll.getTimeStamp(),currentPoll.getChannelId()),option.getOptionText()));
            });

        } catch (SlackApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(postResponse);
        }

        pollService.deletePollById(timestamp,channelId);

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

        OptionService optionService =SpringContext.getBean(OptionService.class);

        optionService.insert(new OptionDto(new PollDto(tsAndChannelId[0],tsAndChannelId[1]),newOption));
        UpdateMessage(tsAndChannelId[0],tsAndChannelId[1]);
    }
}
