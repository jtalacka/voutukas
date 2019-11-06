package application.business;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.block.InputBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.OptionObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.composition.TextObject;
import com.github.seratch.jslack.api.model.block.element.PlainTextInputElement;
import com.github.seratch.jslack.api.model.block.element.StaticSelectElement;
import com.github.seratch.jslack.api.model.view.View;
import com.github.seratch.jslack.api.model.view.ViewClose;
import com.github.seratch.jslack.api.model.view.ViewSubmit;
import com.github.seratch.jslack.api.model.view.ViewTitle;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.app_backend.views.response.ViewSubmissionResponse;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SlackManager {

    private Slack slack;
    private String token;
    private ModalManager modalManager;

    public static final String ACTION_BLOCK_ACTION = "block_actions";
    public static final String ACTION_MESSAGE_ACTIONS = "message_actions";
    public static final String ACTION_VIEW_SUBMISSION = "view_submission";
    public static final String ACTION_VIEW_CLOSED = "view_closed";

    private final String QUESTION_INPUT_ACTION_ID = "question_action";
    private final String QUESTION_INPUT_ID = "question_input";

    private final String OPTIONS_COUNT_INPUT_ACTION_ID = "updated_question_count";
    private final String CALLBACK_MODAL_CREATE_POLL = "callback_create_poll";

    private final int MAXIMUM_POLL_OPTIONS_COUNT = 10;

    public SlackManager(){
        slack = Slack.getInstance();
        modalManager = new ModalManager();
        token = System.getenv("SLACK_API_ACCESS_TOKEN");
    }

    public void composeInitialModal(String triggerId){
        // Question area
        LayoutBlock questionBlock = InputBlock.builder()
                .label(PlainTextObject.builder().text("Question").build())
                .element(PlainTextInputElement.builder()
                        .actionId(QUESTION_INPUT_ACTION_ID)
                        .placeholder(PlainTextObject.builder().text("Your question goes here").build())
                        .build())
                .blockId(QUESTION_INPUT_ID)
                .build();

        // Section with options count select
        List<OptionObject> questionCountSelectOptions = new LinkedList<>();
        for(int i = 2; i <= MAXIMUM_POLL_OPTIONS_COUNT; i++){
            questionCountSelectOptions.add(
                    OptionObject.builder().text(PlainTextObject.builder().text(Integer.toString(i)).build()).value(Integer.toString(i)).build()
            );
        }

        LayoutBlock questionCountSection = SectionBlock.builder()
                .text(PlainTextObject.builder().text("How many options do you want?").build())
                .accessory(
                        StaticSelectElement.builder()
                                .actionId(OPTIONS_COUNT_INPUT_ACTION_ID)
                                .options(questionCountSelectOptions)
                                .build())
                .build();

        List<LayoutBlock> blocks = new LinkedList<>();
        blocks.add(questionBlock);
        blocks.add(questionCountSection);

        // Add 2 input options
        for(int i = 0; i < 2; i++){
            char identifier = 'A';
            blocks.add(
                    InputBlock.builder()
                            .label(PlainTextObject.builder().text("Option " + (char)(identifier+i)).build())
                            .element(PlainTextInputElement.builder()
                                    .placeholder(PlainTextObject.builder().text("Option " + (char)(identifier+i)).build())
                                    .actionId("option " + i)
                                    .build())
                            .build()
            );
        }

        View view = View.builder()
                .type("modal")
                .callbackId(CALLBACK_MODAL_CREATE_POLL)
                .title(ViewTitle.builder().type("plain_text").text("Create a poll").build())
                .submit(ViewSubmit.builder().type("plain_text").text("Select poll options").build())
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
            //Get new View layout and send view update command
            List<LayoutBlock> newBlocks = changeModalOptionInputs(payload.getView().getBlocks(), Integer.parseInt(payload.getActions().get(0).getSelectedOption().getValue()));
            sendViewUpdate(newBlocks, payload.getView());
            return "";
        }

        return "";
    }

    private List<LayoutBlock> changeModalOptionInputs(List<LayoutBlock> blocks, int inputsCount){
        //List<LayoutBlock> presentInputs = blocks.stream().filter(c -> c instanceof InputBlock).filter(c -> ((InputBlock) c).getBlockId()!=QUESTION_INPUT_ID).collect(Collectors.toList());

        LinkedList<LayoutBlock> newBlocks = new LinkedList<>();

        newBlocks.add(blocks.remove(0));   //Question input
        newBlocks.add(blocks.remove(0));   //Questions count select

        char identifier = 'A';
        for(int i = 0; i < inputsCount; i++){
            if(blocks.stream().filter(c -> c instanceof InputBlock).count() > 0){
                newBlocks.add(blocks.remove(0));
                continue;
            }

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
        //Additional formatting
        newBlocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text("\n").build()).build());

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

    public String handleViewSubmission(String jsonPayload){
        ViewSubmissionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonPayload, ViewSubmissionPayload.class);

        if(payload.getView().getCallbackId().equals(CALLBACK_MODAL_CREATE_POLL)){
            String view = "{\n" +
                    "    \"type\": \"modal\",\n" +
                    "    \"title\": {\n" +
                    "      \"type\": \"plain_text\",\n" +
                    "      \"text\": \"Updated view\"\n" +
                    "    },\n" +
                    "    \"blocks\": [\n" +
                    "      {\n" +
                    "        \"type\": \"image\",\n" +
                    "        \"image_url\": \"https://api.slack.com/img/a_very_cute_image_of_two_very_cute_cats.png\",\n" +
                    "        \"alt_text\": \"Two cats being too cute\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"type\": \"context\",\n" +
                    "        \"elements\": [\n" +
                    "          {\n" +
                    "            \"type\": \"mrkdwn\",\n" +
                    "            \"text\": \"_Two of the author's cats sit aloof from the austere challenges of modern society_\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }";
            View vv = View.builder()
                    .type("modal")
                    .callbackId("create_poll_callback")
                    .title(ViewTitle.builder().type("plain_text").text("Level 2").build())
                    .submit(ViewSubmit.builder().type("plain_text").text("Select question options").build())
                    .close(ViewClose.builder().type("plain_text").text("Close").build())
                    .notifyOnClose(false)
                    .blocks(payload.getView().getBlocks())
                    .privateMetadata("YEEEEEEEEEEEEEEEEEEEET")
                    .build();

            ViewSubmissionResponse response = ViewSubmissionResponse.builder().responseAction("push").view(vv).build();
            return GsonFactory.createSnakeCase().toJson(response, ViewSubmissionResponse.class);
        }

        return "";
    }

}
