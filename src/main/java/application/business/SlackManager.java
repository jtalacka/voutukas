package application.business;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.block.InputBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.OptionObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.PlainTextInputElement;
import com.github.seratch.jslack.api.model.block.element.StaticSelectElement;
import com.github.seratch.jslack.api.model.view.View;
import com.github.seratch.jslack.api.model.view.ViewClose;
import com.github.seratch.jslack.api.model.view.ViewSubmit;
import com.github.seratch.jslack.api.model.view.ViewTitle;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SlackManager {

    private Slack slack;
    private String token;

    public static final String ACTION_BLOCK_ACTION = "block_actions";
    public static final String ACTION_MESSAGE_ACTIONS = "message_actions";
    public static final String ACTION_VIEW_SUBMISSION = "view_submission";
    public static final String ACTION_VIEW_CLOSED = "view_closed";

    private final String QUESTION_INPUT_ACTION_ID = "question_action";
    private final String QUESTION_INPUT_ID = "question_input";
    private final String OPTIONS_COUNT_INPUT_ACTION_ID = "updated_question_count";

    public SlackManager(){
        slack = Slack.getInstance();
        token = System.getenv("SLACK_API_ACCESS_TOKEN");
    }

    public void composeInitialModal(String triggerId){
        // Question area
        LayoutBlock questionBlock = InputBlock.builder()
                .label(PlainTextObject.builder().text("Question").build())
                .element(PlainTextInputElement.builder()
                        .actionId(QUESTION_INPUT_ACTION_ID)
                        .multiline(false)
                        .placeholder(PlainTextObject.builder().text("Your question goes here").build())
                        .build())
                .blockId(QUESTION_INPUT_ID)
                .build();

        LayoutBlock questionCountSection = SectionBlock.builder()
                .text(PlainTextObject.builder().text("How many options do you want?").build())
                .accessory(
                        StaticSelectElement.builder()
                                .actionId(OPTIONS_COUNT_INPUT_ACTION_ID)
                                .options(Arrays.asList(
                                        OptionObject.builder().text(PlainTextObject.builder().text("2").build()).value("2").build(),
                                        OptionObject.builder().text(PlainTextObject.builder().text("3").build()).value("3").build(),
                                        OptionObject.builder().text(PlainTextObject.builder().text("4").build()).value("4").build(),
                                        OptionObject.builder().text(PlainTextObject.builder().text("5").build()).value("5").build()))
                                .build()
                )
                .build();

        List<LayoutBlock> blocks = Arrays.asList(questionBlock, questionCountSection);

        View view = View.builder()
                .type("modal")
                .callbackId("create_poll_callback")
                .title(ViewTitle.builder().type("plain_text").text("Create a poll").build())
                .submit(ViewSubmit.builder().type("plain_text").text("Select question options").build())
                .close(ViewClose.builder().type("plain_text").text("Close").build())
                .notifyOnClose(false)
                .blocks(blocks)
                .build();

        try {
            slack.methods(token).viewsOpen(req -> req
                    .view(view)
                    .triggerId(triggerId));
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        }
    }

    public String handleBlockAction(String jsonPayload){
        BlockActionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonPayload, BlockActionPayload.class);

        //Did user change option count in modal?
        if(payload.getActions().get(0).getActionId().equals(OPTIONS_COUNT_INPUT_ACTION_ID)){
            List<LayoutBlock> newBlocks = changeModalOptionInputs(payload.getView().getBlocks(), Integer.parseInt(payload.getActions().get(0).getSelectedOption().getValue()));
            sendViewUpdate(newBlocks, payload.getView());
            return "";
        }

        return "";
    }

    private List<LayoutBlock> changeModalOptionInputs(List<LayoutBlock> blocks, int inputsCount){
        //List<LayoutBlock> presentInputs = blocks.stream().filter(c -> c instanceof InputBlock).filter(c -> ((InputBlock) c).getBlockId()!=QUESTION_INPUT_ID).collect(Collectors.toList());

        LinkedList<LayoutBlock> newBlocks = new LinkedList<>();

        newBlocks.add(blocks.get(0));   //Question input
        newBlocks.add(blocks.get(1));   //Questions count select

        for(int i = 0; i < inputsCount; i++){
            char identifier = 'A';
            newBlocks.add(
                    InputBlock.builder()
                            .label(PlainTextObject.builder().text("Option " + (char)(identifier+i)).build())
                            .element(PlainTextInputElement.builder()
                                    .placeholder(PlainTextObject.builder().text("Option " + (char)(identifier+i)).build())
                                    .actionId("option " + i)
                                    .build())
                            .build()
            );
        }

        return newBlocks;
    }

    private void sendViewUpdate(List<LayoutBlock> blocks, View oldView) {
        View view = View.builder()
                .type("modal")
                .title(oldView.getTitle())
                .submit(oldView.getSubmit())
                .callbackId(oldView.getCallbackId())
                .notifyOnClose(false)
                .blocks(blocks)
                .build();

        try {
            slack.methods(token).viewsUpdate(req -> req.viewId(oldView.getId()).view(view));
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        }
    }
}
