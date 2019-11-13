package application.business;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        LayoutBlock Question=SectionBlock.builder()
                .text(PlainTextObject.builder().text(question).build())
                .build();
        blocks.add(Question);
        blocks.add(DividerBlock.builder().build());
        answers.forEach(answer->blocks.add(
                SectionBlock.builder()
                        .text(PlainTextObject.builder().text(answer).build())
                        .accessory(ButtonElement.builder().text(PlainTextObject.builder().text("vote").build()).build())
                        .build()
        ));
        blocks.add(DividerBlock.builder().build());






        return blocks;
    }
    public void PostInitialMessage(String channelId, String question, List<String> answers){
        
        try {
            ChatPostMessageResponse postResponse = slack.methods(token).chatPostMessage(req -> req.channel(channelId).blocks(ComposeMessage(question,answers)));
        } catch (SlackApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
