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
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatDeleteResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatUpdateResponse;
import com.github.seratch.jslack.api.model.block.*;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.composition.TextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
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
        if(pollOptions.allowUsersToAddOptions==true){
            blocks.add(
                    SectionBlock.builder()
                    .text(PlainTextObject.builder().text("something").build())
                    .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("AddOption").build()).value("option").build())
                    .build()
            );}
        blocks.add(DividerBlock.builder().build());






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
            System.out.println(postResponse);
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
            propertiesSet.add(propertiesService.findPropertieByName("multivote"));
        }if(pollOptions.anonymous==true) {
            propertiesSet.add(propertiesService.findPropertieByName("anonymous"));
        }if(pollOptions.allowUsersToAddOptions==true) {
            propertiesSet.add(propertiesService.findPropertieByName("allowUsersToAddOptions"));
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
            overallNumber+=o.getAnswers().size();
        }
        int finalOverallNumber = overallNumber;
        op.forEach(answer->{
            int temp=answer.getAnswers().size();

            blocks.add(
                SectionBlock.builder()
                        .text(PlainTextObject.builder().text(answer.getOptionText()).build())
                        .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).value(String.valueOf(answer.getId())).build())
                        .build());
            blocks.add(
                    SectionBlock.builder().text(PlainTextObject.builder().text(PercentangeDisplay(temp,finalOverallNumber)).build()).build());
           if(propertyTrue("anonymous",properties)){
                blocks.add(
                        SectionBlock.builder().text(PlainTextObject.builder().text(UserBuilder(answer.getAnswers())).build()).build());

            }

        });
            if(propertyTrue("allowUsersToAddOptions",properties)){
            blocks.add(
                    SectionBlock.builder()
                            .text(PlainTextObject.builder().text("something").build())
                            .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("AddOption").build()).value("option").build())
                            .build()
            );}
        blocks.add(DividerBlock.builder().build());


        try {
            ChatUpdateResponse um = slack.methods(token).chatUpdate(req -> req.channel(currentPoll.getId().getChannelId()).ts(currentPoll.getId().getTimeStamp()).blocks(blocks));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }


    }
    public String PercentangeDisplay(int current,int overall){
        String Symbols=" ";
        if(overall!=0){
            System.out.println(20*current/overall);
        for(int i=0;i<39*current/overall;i++)
        {
            Symbols+="█";
        }}

        return Symbols;

    }
    public String UserBuilder(Set<User>users){
     String user=" ";

        for (User u : users) {

            user +="<@"+u.getName()+">";
        }


        return user;
    }



}
