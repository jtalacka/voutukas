package application.business;

import application.CompositeKeys.PollID;
import application.Modals.Option;
import application.Modals.Poll;
import application.Modals.User;
import application.Repositories.OptionRepository;
import application.Repositories.PollRepository;
import application.Repositories.UserRepository;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Array;
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
    public List<LayoutBlock> ComposeMessage(String question, List<String> answers){
        List<LayoutBlock> blocks=new ArrayList<>();

        //temporary
        OptionRepository optionRepository=SpringContext.getBean(OptionRepository.class);
        AtomicInteger counter=new AtomicInteger(optionRepository.findAll().get(optionRepository.findAll().size()-1).getId()+1);

        //temp


        LayoutBlock Question=SectionBlock.builder()
                .text(PlainTextObject.builder().text(question).build()).blockId("TEST")
                .build();
        blocks.add(Question);
        blocks.add(DividerBlock.builder().build());
        answers.forEach(answer->{blocks.add(
                SectionBlock.builder()
                        .text(PlainTextObject.builder().text(answer).build())
                        .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).value(String.valueOf(counter.get())).build())
                        .build()
        );
        counter.incrementAndGet();
        });
        blocks.add(DividerBlock.builder().build());






        return blocks;
    }
    public void PostInitialMessage(String channelId, String question, List<String> answers,String userId,String userName){
        ChatPostMessageResponse postResponse = null;
        try {
            postResponse = slack.methods(token).chatPostMessage(req -> req.channel(channelId).blocks(ComposeMessage(question,answers)));
            createPollTable(channelId,question,answers,postResponse.getTs().toString(),userId,userName);

        } catch (SlackApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void createPollTable(String channelId,String question,List<String> answers, String timeStamp,String userId,String userName){

        PollRepository pollRepository=SpringContext.getBean(PollRepository.class);
        UserRepository userRepository=SpringContext.getBean(UserRepository.class);
        OptionRepository optionRepository=SpringContext.getBean(OptionRepository.class);
        User user=new User(userId,userName);
        userRepository.save(user);
        PollID pollId=new PollID(timeStamp,channelId);
        pollRepository.save(new Poll(pollId, question,user));
        answers.forEach(
                answer->{
                    optionRepository.save(new Option(null,new Poll(pollId, question,user),answer));

                }
        );


    }
    public void OnUserVote(String payload){
        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String timestamp=pld.getContainer().getMessageTs();
        String channelId=pld.getContainer().getChannelId();
        String userId=pld.getUser().getId();
        String username=pld.getUser().getUsername();
        String voteValue=pld.getActions().get(0).getValue();

        OptionRepository optionRepository=SpringContext.getBean(OptionRepository.class);
        UserRepository usr=SpringContext.getBean(UserRepository.class);
        usr.save(new User(userId,username));

        try{
        optionRepository.findAllOptionsByPollID(new Poll(new PollID(timestamp,channelId))).forEach(
                option->{if(option.getId()==Integer.parseInt(voteValue)){
                    Set<User> answers =option.getAnswers();
                    answers.add(usr.getOne(userId));
                    option.setAnswers(answers);
                    optionRepository.save(option);
                }}
        );}catch (Exception e){


        }
        UpdateMessage(timestamp,channelId);

        //System.out.println(pld.getActions().get(0).getValue());
    }
    public void UpdateMessage(String timestamp,String channelID){
        PollRepository poll=SpringContext.getBean(PollRepository.class);
        OptionRepository options=SpringContext.getBean(OptionRepository.class);
        Poll currentPoll=poll.getOne(new PollID(timestamp,channelID));
        List<Option> op=options.findAllOptionsByPollID(new Poll(new PollID(timestamp,channelID)));

        List<LayoutBlock> blocks=new ArrayList<>();

        //temporary

        //temp

        LayoutBlock Question=SectionBlock.builder()
                .text(PlainTextObject.builder().text(currentPoll.getName()).build()).blockId("TEST")
                .build();
        blocks.add(Question);
        blocks.add(DividerBlock.builder().build());
        op.forEach(answer->{blocks.add(
                SectionBlock.builder()
                        .text(PlainTextObject.builder().text(answer.getOptionText()).build())
                        .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).value(String.valueOf(answer.getId())).build())
                        .build());
            blocks.add(
                   SectionBlock.builder().text(PlainTextObject.builder().text(UserBuilder(answer.getAnswers())).build()).build()
            );
        });
        blocks.add(DividerBlock.builder().build());


        try {
            ChatUpdateResponse um = slack.methods(token).chatUpdate(req -> req.channel(currentPoll.getId().getChannelId()).ts(currentPoll.getId().getTimeStamp()).blocks(blocks));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }


    }
    public String UserBuilder(Set<User>users){
     String user=" ";
        System.out.println("-----------------"+users.size());

        for (User u : users) {
            user += u.getName()+" ";
        }
        System.out.println("-----------------");


        return user;
    }



}
