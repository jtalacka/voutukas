package application.controllers;

import application.business.SlackManager;
import com.github.seratch.jslack.app_backend.views.payload.ViewSubmissionPayload;
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
    public void onInteraction(@RequestParam("payload") String jsonResponse){
        ViewSubmissionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonResponse, ViewSubmissionPayload.class);
        System.out.println(payload);

    }
}
