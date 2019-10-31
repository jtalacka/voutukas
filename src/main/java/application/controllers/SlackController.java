package application.controllers;

import application.business.SlackManager;
import com.github.seratch.jslack.api.model.view.View;
import com.github.seratch.jslack.api.model.view.ViewClose;
import com.github.seratch.jslack.api.model.view.ViewSubmit;
import com.github.seratch.jslack.api.model.view.ViewTitle;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
import com.github.seratch.jslack.app_backend.views.response.ViewSubmissionResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.github.seratch.jslack.common.json.*;
import com.google.gson.Gson;

@RestController
public class SlackController {
    private SlackManager slackManager;

    public SlackController(){
        slackManager = new SlackManager();
    }

    @RequestMapping(value = "/slack/slash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onSlashCommandAccepted(@RequestParam("trigger_id") String triggerId) {
        slackManager.sendInitialModalResponse(triggerId);
    }

    @RequestMapping(value = "/slack/interact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onInteraction(@RequestParam("payload") String jsonResponse){
        ViewSubmissionPayload submissionPayload = GsonFactory.createSnakeCase().fromJson(jsonResponse, ViewSubmissionPayload.class);

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

        return GsonFactory.createSnakeCase().toJson(response, ViewSubmissionResponse.class);
    }
}
