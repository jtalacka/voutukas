package application.business;

import application.models.CreatePollOptions;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.model.block.*;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.OptionObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.BlockElement;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.api.model.block.element.PlainTextInputElement;
import com.github.seratch.jslack.api.model.block.element.StaticSelectElement;
import com.github.seratch.jslack.api.model.view.*;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.app_backend.views.response.ViewSubmissionResponse;
import com.github.seratch.jslack.common.json.GsonFactory;

import java.io.IOException;
import java.util.*;

public class SlackManager {

    //Slack action types
    public static final String ACTION_BLOCK_ACTION = "block_actions";
    public static final String ACTION_MESSAGE_ACTIONS = "message_actions";
    public static final String ACTION_VIEW_SUBMISSION = "view_submission";
    public static final String ACTION_VIEW_CLOSED = "view_closed";

    private final String ACTION_ID_ADD_OPTION = "add_option_action";
    private final String ACTION_ID_REMOVE_OPTION = "remove_option_action";

    private final String ACTION_ID_ALLOW_USERS_TO_ADD_OPTIONS_CHECK = "allow_to_add_options_check";
    private final String ACTION_ID_ANONYMOUS_CHECK = "anonymous_check";
    private final String ACTION_ID_MULTI_VOTE_CHECK = "multi_vote_check";
    //Block identification ID's
    private final String BLOCK_ID_QUESTION_INPUT = "question_input";
    private final String BLOCK_ID_ADD_REMOVE_ACTION_BLOCK = "add_remove_action_block";
    private final String BLOCK_ID_POLL_OPTIONS_BLOCK = "poll_options_block";

    //Modal submission Callbacks
    private final String CALLBACK_MODAL_CREATE_POLL = "callback_create_poll";
    public static final String CALLBACK_MODAL_ADD_OPTION = "callback_add_option";


    public static final String MESSAGE_ACTION_ID_ADD_OPTION = "message_add_option";

    //Constants
    private final Slack slack;
    private final String token;
    private String channelId;

    public SlackManager() {
        slack = Slack.getInstance();
        token = System.getenv("SLACK_API_ACCESS_TOKEN");
    }

    public void composeInitialModal(String triggerId, List<String> InlineInlineText,String channelId){
        this.channelId=channelId;
        // Question area
        //Block Action ID's
        String ACTION_ID_QUESTION_INPUT = "question_action";
        LayoutBlock questionBlock = inputBlockBuilder("Question", ACTION_ID_QUESTION_INPUT, "Your question goes here", BLOCK_ID_QUESTION_INPUT);

        if(InlineInlineText != null)
        {
            ((InputBlock) questionBlock).setElement(PlainTextInputElement.builder().initialValue(InlineInlineText.get(0)).build());
        }

        List<LayoutBlock> blocks = new LinkedList<>();
        blocks.add(questionBlock);

        char identifier = 'A';
        if(InlineInlineText != null && InlineInlineText.size() == 2)
        {
            InputBlock temp = inputBlockBuilder("Option "+identifier, "option 0", "Option "+identifier);
            temp.setElement(PlainTextInputElement.builder().initialValue(InlineInlineText.get(1)).build());
            blocks.add(temp);
            blocks.add(inputBlockBuilder("Option "+(char)(identifier+1), "option 1", "Option "+(char)(identifier+1)));
        }
        else if(InlineInlineText != null && InlineInlineText.size() > 2)
        {
            for(String question : InlineInlineText)
            {
                if(InlineInlineText.indexOf(question) == 0) continue;
                InputBlock temp = inputBlockBuilder("Option "+identifier, "option "+ InlineInlineText.indexOf(question), "Option "+identifier);
                temp.setElement(PlainTextInputElement.builder().initialValue(question).build());
                blocks.add(temp);
                identifier++;
            }
        }
        else
        {
            for (int i = 0; i < 2; i++) {
                blocks.add(inputBlockBuilder("Option "+identifier, "option "+i, "Option "+identifier));
                identifier++;
            }
        }

        //Add/remove option buttons

        LayoutBlock addOptionsSection = ActionsBlock.builder()
                .blockId(BLOCK_ID_ADD_REMOVE_ACTION_BLOCK)
                .elements(Collections.singletonList(
                        ButtonElement.builder().text(PlainTextObject.builder().text("Add Option").build()).actionId(ACTION_ID_ADD_OPTION).style("primary").build()
                ))
                .build();

        blocks.add(addOptionsSection);

        //Divider block
        blocks.add(DividerBlock.builder().build());
        //Heading
        blocks.add(SectionBlock.builder().text(MarkdownTextObject.builder().text("*Poll settings*").build()).build());
        //Poll options
        blocks.add(pollOptionsBuilder(new CreatePollOptions()));

        //Modal submission Callbacks
        String CALLBACK_MODAL_CREATE_POLL = "callback_create_poll";
        View view = View.builder()
                .type("modal")
                .callbackId(CALLBACK_MODAL_CREATE_POLL)
                .title(ViewTitle.builder().type("plain_text").text("Create a poll").build())
                .submit(ViewSubmit.builder().type("plain_text").text("Submit").build())
                .close(ViewClose.builder().type("plain_text").text("Close").build())
                .notifyOnClose(false)
                .blocks(blocks)
                .privateMetadata(GsonFactory.createSnakeCase().toJson(new CreatePollOptions(), CreatePollOptions.class))
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
        if(payload.getContainer().getType().equals("message")){
            String actionId = payload.getActions().get(0).getActionId();
            Message message= new Message(slack,token);
            switch (actionId) {
                case "delete":
                    message.OnPollDelete(jsonPayload);
                    return "";
                case "renew":
                    message.OnPolRenew(jsonPayload);
                    return "";
                case "choices":
                    handleMessageBlockAction(jsonPayload);
                    return "";
                default:
                    message.OnUserVote(jsonPayload);
                    return "";
            }
        }

        View payloadView = payload.getView();
        String actionId = payload.getActions().get(0).getActionId();
        CreatePollOptions options = GsonFactory.createSnakeCase().fromJson(payload.getView().getPrivateMetadata(), CreatePollOptions.class);

        List<LayoutBlock> newBlocks;

        switch(actionId){
            case ACTION_ID_ADD_OPTION:
                //Get new View layout and send view update command
                newBlocks = addModalOptionInput(payload.getView().getBlocks());
                break;
            case ACTION_ID_REMOVE_OPTION:
                newBlocks = removeModalOptionInput(payload.getView().getBlocks());
                break;
            case ACTION_ID_ALLOW_USERS_TO_ADD_OPTIONS_CHECK:
                options.allowUsersToAddOptions = !options.allowUsersToAddOptions;
                newBlocks = updateModalPollOptions(payload.getView().getBlocks(), options);
                break;
            case ACTION_ID_ANONYMOUS_CHECK:
                options.anonymous = !options.anonymous;
                newBlocks = updateModalPollOptions(payload.getView().getBlocks(), options);
                break;
            case ACTION_ID_MULTI_VOTE_CHECK:
                options.multivote = !options.multivote;
                newBlocks = updateModalPollOptions(payload.getView().getBlocks(), options);
                break;
            default:
                newBlocks = payload.getView().getBlocks();
        }
        payloadView.setPrivateMetadata(GsonFactory.createSnakeCase().toJson(options, CreatePollOptions.class));
        sendViewUpdate(newBlocks, payloadView);
        return "";
    }

    private List<LayoutBlock> updateModalPollOptions(List<LayoutBlock> blocks, CreatePollOptions options){
        blocks.set(blocks.size()-1, pollOptionsBuilder(options));
        return blocks;
    }

    private List<LayoutBlock> addModalOptionInput(List<LayoutBlock> blocks){
        int optionCount = (int)blocks.stream().filter(c -> c instanceof InputBlock && !((InputBlock) c).getBlockId().equals(BLOCK_ID_QUESTION_INPUT)).count();

        char identifier = (char)('A' + optionCount);
        LayoutBlock newOption = inputBlockBuilder("Option "+identifier, "option "+ optionCount, "Option "+identifier);

        blocks.set(optionCount + 1, ActionsBlock.builder().blockId(BLOCK_ID_ADD_REMOVE_ACTION_BLOCK)
                .elements(Arrays.asList(
                        ButtonElement.builder().text(PlainTextObject.builder().text("Add Option").build()).actionId(ACTION_ID_ADD_OPTION).style("primary").build(),
                        ButtonElement.builder().text(PlainTextObject.builder().text("Remove Option").build()).actionId(ACTION_ID_REMOVE_OPTION).build()
                )).build());
        blocks.add(optionCount + 1, newOption);

        return blocks;
    }

    private List<LayoutBlock> removeModalOptionInput(List<LayoutBlock> blocks){
        int optionCount = (int)blocks.stream().filter(c -> c instanceof InputBlock && !((InputBlock) c).getBlockId().equals(BLOCK_ID_QUESTION_INPUT)).count();
        if(optionCount <= 3){
            blocks.set(optionCount + 1, ActionsBlock.builder().blockId(BLOCK_ID_ADD_REMOVE_ACTION_BLOCK)
                    .elements(Collections.singletonList(
                            ButtonElement.builder().text(PlainTextObject.builder().text("Add Option").build()).actionId(ACTION_ID_ADD_OPTION).style("primary").build()
                    )).build());
        }

        blocks.remove(optionCount);

        return blocks;
    }

    private void sendViewUpdate(List<LayoutBlock> blocks, View oldView) {
        View view = View.builder()
                .type("modal")
                .title(oldView.getTitle())
                .submit(oldView.getSubmit())
                .callbackId(oldView.getCallbackId())
                .notifyOnClose(false)
                .blocks(blocks)
                .privateMetadata(oldView.getPrivateMetadata())
                .build();

        try {
            slack.methods(token).viewsUpdate(req -> req.viewId(oldView.getId()).view(view));
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        }
    }

    public String handleViewSubmission(String jsonPayload) {

        ViewSubmissionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonPayload, ViewSubmissionPayload.class);

        if(payload.getView().getTitle().getText().equals("Add poll option")){
            Message message = new Message(slack,token);
            message.OnOptionCreation(jsonPayload);
            return "";
        }

        String callback = payload.getView().getCallbackId();
        ViewState state = payload.getView().getState();

        //States of submitted view components
        List<Map<String, ViewState.Value>> stateValuesList = new LinkedList<>(state.getValues().values());

        String inputQuestion = stateValuesList.get(0).values().stream().findFirst().get().getValue();
        stateValuesList.remove(0);

        // A list of all option Strings
        List<String> questionOptions = new LinkedList<>();
        stateValuesList.forEach(i -> questionOptions.add(i.values().stream().findFirst().get().getValue()));

        //Current poll options
        CreatePollOptions pollOptions = GsonFactory.createSnakeCase().fromJson(payload.getView().getPrivateMetadata(), CreatePollOptions.class);
        //Display Initial Message
        Message message = new Message(slack,token);
        message.PostInitialMessage(channelId,inputQuestion,questionOptions,payload.getUser().getId(),payload.getUser().getUsername(), payload.getUser().getName(), pollOptions);
        return "";
    }


    private InputBlock inputBlockBuilder(String label, String actionId, String placeholder) {
        return InputBlock.builder()
                        .label(PlainTextObject.builder().text(label).build())
                        .element(PlainTextInputElement.builder()
                                .placeholder(PlainTextObject.builder().text(placeholder).build())
                                .actionId(actionId)
                                .build())
                        .build();
    }

    private InputBlock inputBlockBuilder(String label, String actionId, String placeholder, String block_id) {
        return InputBlock.builder()
                .label(PlainTextObject.builder().text(label).build())
                .element(PlainTextInputElement.builder()
                        .placeholder(PlainTextObject.builder().text(placeholder).build())
                        .actionId(actionId)
                        .build())
                .blockId(block_id)
                .build();
    }

    private ActionsBlock pollOptionsBuilder(CreatePollOptions options){
        String checkedPrefix = "\u2713 ";
        List<BlockElement> elements = new LinkedList<>();

        if(options.anonymous){
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text(checkedPrefix + "Make poll anonymous").build()).actionId(ACTION_ID_ANONYMOUS_CHECK).style("primary").build());
        }
        else{
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Make poll anonymous").build()).actionId(ACTION_ID_ANONYMOUS_CHECK).build());
        }
        if(options.multivote){
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text(checkedPrefix + "Allow multiple votes").build()).actionId(ACTION_ID_MULTI_VOTE_CHECK).style("primary").build());
        }
        else{
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Allow multiple votes").build()).actionId(ACTION_ID_MULTI_VOTE_CHECK).build());
        }
        if(options.allowUsersToAddOptions){
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text(checkedPrefix + "Allow users to add options").build()).actionId(ACTION_ID_ALLOW_USERS_TO_ADD_OPTIONS_CHECK).style("primary").build());
        }
        else{
            elements.add(ButtonElement.builder().text(PlainTextObject.builder().text("Allow users to add options").build()).actionId(ACTION_ID_ALLOW_USERS_TO_ADD_OPTIONS_CHECK).build());
        }

        String BLOCK_ID_POLL_OPTIONS_BLOCK = "poll_options_block";
        return ActionsBlock.builder().blockId(BLOCK_ID_POLL_OPTIONS_BLOCK).elements(elements).build();
    }

    public void handleMessageBlockAction(String payload){

        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String triggerId = pld.getTriggerId();

        List<LayoutBlock> blocks = new LinkedList<>();
        blocks.add(
                InputBlock.builder()
                        .label(PlainTextObject.builder().text("New question option").build())
                        .element(PlainTextInputElement.builder().placeholder(PlainTextObject.builder().text("New option").build()).build())
                        .build()
        );

        View view = View.builder()
                .type("modal")
                .callbackId(CALLBACK_MODAL_ADD_OPTION)
                .title(ViewTitle.builder().type("plain_text").text("Add poll option").build())
                .submit(ViewSubmit.builder().type("plain_text").text("Submit").build())
                .close(ViewClose.builder().type("plain_text").text("Cancel").build())
                .notifyOnClose(false)
                .blocks(blocks)
                .privateMetadata(pld.getContainer().getMessageTs() + "&" + pld.getContainer().getChannelId())
                .build();

        try {
            slack.methods(token).viewsOpen(req -> req
                    .view(view)
                    .triggerId(triggerId));
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        }
    }

    public void handleMessageDeletionAction(String payload, String owner){

        BlockActionPayload pld = GsonFactory.createSnakeCase().fromJson(payload, BlockActionPayload.class);
        String triggerId = pld.getTriggerId();

        List<LayoutBlock> blocks = new LinkedList<>();
        blocks.add(                    SectionBlock.builder()
                .text(PlainTextObject.builder().text("Sorry, but you cannot delete this poll").build()).build());
        blocks.add(SectionBlock.builder()
                .text(PlainTextObject.builder().text("Only <@" + owner + "> can delete this poll").build()).build());
        View view = View.builder()
                .type("modal")
                .callbackId(CALLBACK_MODAL_ADD_OPTION)
                .title(ViewTitle.builder().type("plain_text").text("Sorry").build())
                .close(ViewClose.builder().type("plain_text").text("Cancel").build())
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

    public void PostInitialMessage(String channelId, String question, List<String> answers, String userId, String slackName, String fullName, List<String> pollOptions){
        CreatePollOptions createPollOptions = new CreatePollOptions();
        pollOptions.forEach(pollOption -> {
            switch (pollOption){
                case "anonymous":
                    createPollOptions.anonymous = true;
                    break;
                case "multivote":
                    createPollOptions.multivote = true;
                    break;
                case "allowUsersToAddOptions":
                    createPollOptions.allowUsersToAddOptions= true;
                    break;
            }
        });
        Message message = new Message(slack,token);
        message.PostInitialMessage(channelId,question,answers,userId,slackName ,fullName, createPollOptions);
    }

}
