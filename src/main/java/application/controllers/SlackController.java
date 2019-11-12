package application.controllers;

import application.business.SlackManager;
import com.github.seratch.jslack.common.json.GsonFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SlackController {
    private SlackManager slackManager;

    public SlackController(){
        slackManager = new SlackManager();
    }

    @RequestMapping(value = "/slack/slash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onSlashCommandAccepted(@RequestParam("trigger_id") String triggerId, @RequestParam("text")String text) {
        slackManager.composeInitialModal(triggerId,ListOfInputText(text));
    }

    @RequestMapping(value = "/slack/interact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onInteraction(@RequestParam("payload") String jsonResponse){
        //We check what type of response we got
        String type = GsonFactory.createSnakeCase().fromJson(jsonResponse, Map.class).get("type").toString();

        //Appropriate response depending on the type of action
        switch(type){
            case SlackManager.ACTION_BLOCK_ACTION:
                return slackManager.handleBlockAction(jsonResponse);
            case SlackManager.ACTION_VIEW_SUBMISSION:
                return slackManager.handleViewSubmission(jsonResponse);
        }

        return "";



        /*if(type.equals(SlackManager.ACTION_BLOCK_ACTION)){
            BlockActionPayload payload = GsonFactory.createSnakeCase().fromJson(jsonResponse, BlockActionPayload.class);
            System.out.println("Block Action");
        }
        else if(type.equals(SlackManager.ACTION_VIEW_SUBMISSION)){
            System.out.println("View Submitted");
        }*/

        /*ViewSubmissionPayload submissionPayload = GsonFactory.createSnakeCase().fromJson(jsonResponse, ViewSubmissionPayload.class);
        System.out.println(submissionPayload);
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

    private List<String> ListOfInputText(String text)
    {
        if(text.length() > 3000)
        {
            //throw error to user: Message text cannot exceed 3000 characters
            return null;
        }
        List<String> managedText = new ArrayList<>();
        if(text.equals(""))
        {
            return null;
        }
        else if(!text.startsWith("\""))
        {
            managedText.add(text);
            return managedText;
        }
        else
        {
            managedText = Arrays.asList(text.split("\""));
            managedText = managedText.stream().filter(item -> !item.equals("")).filter(item -> !item.equals(" ")).collect(Collectors.toList());
            return managedText;
        }
    }
}
