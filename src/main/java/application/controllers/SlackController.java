package application.controllers;

import application.slackJSON.Attachment;
import application.slackJSON.SlackResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {
    @RequestMapping(value = "/slack/slash",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public SlackResponse onSlashCommandAccepted(@RequestParam("team_id") String teamId,
                                                @RequestParam("team_domain") String teamDomain,
                                                @RequestParam("channel_id") String channelId,
                                                @RequestParam("channel_name") String channelName,
                                                @RequestParam("user_id") String userId,
                                                @RequestParam("user_name") String userName,
                                                @RequestParam("command") String command,
                                                @RequestParam("text") String text,
                                                @RequestParam("response_url") String responseUrl) {
        SlackResponse response = new SlackResponse();
        response.setText("It just works");
        response.setResponseType("in_channel");

        Attachment attachment = new Attachment();
        attachment.setText(" - Todd Howard");
        attachment.setColor("#0000ff");

        response.addAttachment(attachment);

        return response;
    }
}
