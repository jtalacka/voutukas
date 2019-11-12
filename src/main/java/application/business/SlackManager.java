package application.business;

import application.models.CreatePollOptions;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.block.InputBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.OptionObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.PlainTextInputElement;
import com.github.seratch.jslack.api.model.block.element.StaticSelectElement;
import com.github.seratch.jslack.api.model.view.*;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.app_backend.views.response.ViewSubmissionResponse;
import com.github.seratch.jslack.common.json.GsonFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SlackManager {

    //Slack action types
    public static final String ACTION_BLOCK_ACTION = "block_actions";
    public static final String ACTION_MESSAGE_ACTIONS = "message_actions";
    public static final String ACTION_VIEW_SUBMISSION = "view_submission";
    public static final String ACTION_VIEW_CLOSED = "view_closed";
    //Block Action ID's
    private final String QUESTION_INPUT_ACTION_ID = "question_action";
    //Block identification ID's
    private final String BLOCK_ID_QUESTION_INPUT = "question_input";
    private final String BLOCK_ID_SELECT_ANONYMOUS = "select_anonymous";
    private final String BLOCK_ID_SELECT_MULTIVOTE = "select_multivote";
    private final String BLOCK_ID_SELECT_ALLOW_USERS_TO_ADD_OPTIONS = "select_allow_users_to_add";
    //Block interaction action ID's
    private final String OPTIONS_COUNT_INPUT_ACTION_ID = "updated_question_count";
    //Modal submission Callbacks
    private final String CALLBACK_MODAL_CREATE_POLL = "callback_create_poll";
    private final String CALLBACK_MODAL_SELECT_OPTIONS = "callback_select_options";
    //Constants
    private final int MAXIMUM_POLL_OPTIONS_COUNT = 10;
    private final String ENABLE_SELECT_VALUE = "Enable";
    private final String ENABLE_SELECT_TEXT = "Enable";
    private final String DISABLE_SELECT_VALUE = "Disable";
    private final String DISABLE_SELECT_TEXT = "Disable";
    private Slack slack;
    private String token;
    private List<String> InlineText;

    public SlackManager() {
        slack = Slack.getInstance();
        token = System.getenv("SLACK_API_ACCESS_TOKEN");
    }

    public void composeInitialModal(String triggerId, List<String> InlineInlineText) {
        // Question area
        InlineText = InlineInlineText;
        LayoutBlock questionBlock = InputBlock.builder()
                .label(PlainTextObject.builder().text("Question").build())
                .element(PlainTextInputElement.builder()
                        .actionId(QUESTION_INPUT_ACTION_ID)
                        .placeholder(PlainTextObject.builder().text("Your question goes here").build())
                        .build())
                .blockId(BLOCK_ID_QUESTION_INPUT)
                .build();
        if(InlineText != null)
        {
            ((InputBlock) questionBlock).setElement(PlainTextInputElement.builder().initialValue(InlineText.get(0)).build());
        }


        // Section with options count select
        List<OptionObject> questionCountSelectOptions = new LinkedList<>();
        for (int i = 2; i <= MAXIMUM_POLL_OPTIONS_COUNT; i++) {
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
                                /*.initialOption(questionCountSelectOptions.get(0))*/
                                .build())
                .build();

        List<LayoutBlock> blocks = new LinkedList<>();
        blocks.add(questionBlock);
        blocks.add(questionCountSection);
        char identifier = 'A';
        if(InlineText != null && InlineText.size() == 2)
        {
            InputBlock temp = BlockBuilder(identifier,0);
            temp.setElement(PlainTextInputElement.builder().initialValue(InlineText.get(1)).build());
            blocks.add(temp);
            blocks.add(BlockBuilder(identifier,1));
        }
        else if(InlineText != null && InlineText.size() > 2)
        {
            for(String question : InlineText)
            {
                if(InlineText.indexOf(question) == 0) continue;
                InputBlock temp = BlockBuilder(identifier,InlineText.indexOf(question) - 1);
                temp.setElement(PlainTextInputElement.builder().initialValue(question).build());
                blocks.add(temp);
            }
        }
        else
        {
            for (int i = 0; i < 2; i++) {

                blocks.add(BlockBuilder(identifier, i));
            }
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

    public String handleBlockAction(String jsonPayload) {
        BlockActionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonPayload, BlockActionPayload.class);

        //Did user change option count in modal?
        if (payload.getActions().get(0).getActionId().equals(OPTIONS_COUNT_INPUT_ACTION_ID)) {
            //Get new View layout and send view update command
            List<LayoutBlock> newBlocks = changeModalOptionInputs(payload.getView().getBlocks(), Integer.parseInt(payload.getActions().get(0).getSelectedOption().getValue()));
            sendViewUpdate(newBlocks, payload.getView());
            return "";
        }

        return "";
    }

    private List<LayoutBlock> changeModalOptionInputs(List<LayoutBlock> blocks, int inputsCount) {
        //List<LayoutBlock> presentInputs = blocks.stream().filter(c -> c instanceof InputBlock).filter(c -> ((InputBlock) c).getBlockId()!=QUESTION_INPUT_ID).collect(Collectors.toList());

        LinkedList<LayoutBlock> newBlocks = new LinkedList<>();

        newBlocks.add(blocks.remove(0));   //Question input
        newBlocks.add(blocks.remove(0));   //Questions count select

        char identifier = 'A';

        for (int i = 0; i < inputsCount; i++) {
            if (blocks.stream().filter(c -> c instanceof InputBlock).count() > 0) {
                newBlocks.add(blocks.remove(0));
                continue;
            }

            newBlocks.add(BlockBuilder(identifier, i));
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

    public String handleViewSubmission(String jsonPayload) {
        ViewSubmissionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonPayload, ViewSubmissionPayload.class);
        String callback = payload.getView().getCallbackId();

        if (callback.equals(CALLBACK_MODAL_CREATE_POLL)) {
            LayoutBlock selectAnonymous = InputBlock.builder()
                    .blockId(BLOCK_ID_SELECT_ANONYMOUS)
                    .label(PlainTextObject.builder().text("Make votes anonymous").build())
                    .element(OptionsBuilder())
                    .build();

            LayoutBlock selectMultivote = InputBlock.builder()
                    .blockId(BLOCK_ID_SELECT_MULTIVOTE)
                    .label(PlainTextObject.builder().text("Allow users to vote multiple times").build())
                    .element(OptionsBuilder())
                    .build();

            LayoutBlock selectAllowUsersToAdd = InputBlock.builder()
                    .blockId(BLOCK_ID_SELECT_ALLOW_USERS_TO_ADD_OPTIONS)
                    .label(PlainTextObject.builder().text("Allow users to add answer options").build())
                    .element(OptionsBuilder())
                    .build();

            View view = View.builder()
                    .type("modal")
                    .callbackId(CALLBACK_MODAL_SELECT_OPTIONS)
                    .title(ViewTitle.builder().type("plain_text").text("Additional options").build())
                    .submit(ViewSubmit.builder().type("plain_text").text("Post poll").build())
                    .close(ViewClose.builder().type("plain_text").text("Back").build())
                    .notifyOnClose(false)
                    .blocks(Arrays.asList(selectAnonymous, selectMultivote, selectAllowUsersToAdd))
                    .privateMetadata(GsonFactory.createSnakeCase().toJson(payload.getView().getState(), ViewState.class))
                    .build();


            ViewSubmissionResponse response = ViewSubmissionResponse.builder().responseAction("push").view(view).build();
            return GsonFactory.createSnakeCase().toJson(response, ViewSubmissionResponse.class);
        } else if (callback.equals(CALLBACK_MODAL_SELECT_OPTIONS)) {
            CreatePollOptions pollOptions = new CreatePollOptions();

            // State from previous view holding question with options
            ViewState prevState = GsonFactory.createSnakeCase().fromJson(payload.getView().getPrivateMetadata(), ViewState.class);

            // A list of all value objects
            List<Map<String, ViewState.Value>> optionObjectList = new LinkedList<>(prevState.getValues().values());

            // Question string
            String inputQuestion = optionObjectList.get(0).values().stream().findFirst().get().getValue();
            optionObjectList.remove(0);

            // Add question options to the list
            List<String> questionOptions = new LinkedList<>();
            optionObjectList.forEach(
                    item -> questionOptions.add(item.values().stream().findFirst().get().getValue())
            );

            //Current state with poll options
            List<Map<String, ViewState.Value>> currState = new LinkedList<>(payload.getView().getState().getValues().values());

            if (currState.get(0).values().stream().findFirst().get().getSelectedOption().getValue().equals(ENABLE_SELECT_VALUE))
                pollOptions.anonymous = true;
            if (currState.get(1).values().stream().findFirst().get().getSelectedOption().getValue().equals(ENABLE_SELECT_VALUE))
                pollOptions.multivote = true;
            if (currState.get(2).values().stream().findFirst().get().getSelectedOption().getValue().equals(ENABLE_SELECT_VALUE))
                pollOptions.allowUsersToAddOptions = true;

            //Close all views after getting response
            ViewSubmissionResponse response = ViewSubmissionResponse.builder().responseAction("clear").build();
            return GsonFactory.createSnakeCase().toJson(response, ViewSubmissionResponse.class);
        }

        return "";
    }

    private InputBlock BlockBuilder(char identifier, int index) {
        return
                InputBlock.builder()
                        .label(PlainTextObject.builder().text("Option " + (char) (identifier + index)).build())
                        .element(PlainTextInputElement.builder()
                                .placeholder(PlainTextObject.builder().text("Option " + (char) (identifier + index)).build())
                                .actionId("option " + index)
                                .build())
                        .build();
    }

    private StaticSelectElement OptionsBuilder() {
        return
                StaticSelectElement.builder()
                        .options(Arrays.asList(
                                OptionObject.builder().text(PlainTextObject.builder().text(ENABLE_SELECT_TEXT).build()).value(ENABLE_SELECT_VALUE).build(),
                                OptionObject.builder().text(PlainTextObject.builder().text(DISABLE_SELECT_TEXT).build()).value(DISABLE_SELECT_VALUE).build()
                        ))
                        .initialOption(OptionObject.builder().text(PlainTextObject.builder().text(DISABLE_SELECT_TEXT).build()).value(DISABLE_SELECT_VALUE).build())
                        .build();
    }

}
