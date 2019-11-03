package application.controllers;

import LoggerLogic.Logger;
import application.business.SlackManager;
import com.github.seratch.jslack.common.json.GsonFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SlackController {
    private SlackManager slackManager;

    public SlackController(){
        slackManager = new SlackManager();
    }

    @RequestMapping(value = "/slack/slash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onSlashCommandAccepted(@RequestParam("trigger_id") String triggerId) {
        slackManager.composeInitialModal(triggerId);
    }

    @RequestMapping(value = "/slack/interact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onInteraction(@RequestParam("payload") String jsonResponse){
        //We check what type of response we got
        String type = GsonFactory.createSnakeCase().fromJson(jsonResponse, Map.class).get("type").toString();
        Logger.LogIncomingPayload(jsonResponse);
        //Appropriate response depending on the type of action
        switch(type){
            case SlackManager.ACTION_BLOCK_ACTION:
                //BlockActionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonResponse, BlockActionPayload.class);
                return slackManager.handleBlockAction(jsonResponse);
            case SlackManager.ACTION_VIEW_SUBMISSION:
                Logger.LogEvent("View Submitted");
                return "";
        }

        return "";



        /*if(type.equals(SlackManager.ACTION_BLOCK_ACTION)){
            BlockActionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonResponse, BlockActionPayload.class);
            Logger.LogEvent("Block Action");
        }
        else if(type.equals(SlackManager.ACTION_VIEW_SUBMISSION)){
            Logger.LogEvent("View Submitted");
        }*/

        /*ViewSubmissionPayload submissionPayload = GsonFactory.createSnakeCase().fromJson(jsonResponse, ViewSubmissionPayload.class);
        Logger.LogEvent(submissionPayload);
        View view = View.builder()
                .type("modal")
                .callbackId("create_poll_callback")
                .title(ViewTitle.builder().type("plain_text").text("Create a poll").build())
                .submit(ViewSubmit.builder().type("plain_text").text("Select question options").build())
                .close(ViewClose.builder().type("plain_text").text("Close").build())
                .notifyOnClose(false)
                .blocks(submissionPayload.getView().getBlocks())
                .build();

        ViewSubmissionResponse response = ViewSubmissionResponse.builder().responseAction("update").view(view).build();

        return GsonFactory.createSnakeCase().toJson(response, ViewSubmissionResponse.class);*/
    }
}
