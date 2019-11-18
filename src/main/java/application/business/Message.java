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
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.block.ActionsBlock;
import com.github.seratch.jslack.api.model.block.DividerBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.composition.TextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        AtomicInteger counter=new AtomicInteger(0);
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
        String voteValue=pld.getActions().get(0).getValue();
        //System.out.println(pld.getActions().get(0).getValue());
    }


}
