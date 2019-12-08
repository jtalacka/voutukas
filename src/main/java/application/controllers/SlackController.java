package application.controllers;

import application.business.SlackManager;
import com.github.seratch.jslack.common.json.GsonFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("slack")
public class SlackController {
    private SlackManager slackManager;
    private int yes = 0;

    public SlackController(){
        slackManager = new SlackManager();
    }

    @RequestMapping(value = "slash", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void onSlashCommandAccepted(@RequestParam("trigger_id") String triggerId, @RequestParam("text")String text, @RequestParam("channel_id") String channelId) {
        slackManager.composeInitialModal(triggerId, ListOfInputText(text), channelId);
    }

    @RequestMapping(value = "interact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String onInteraction(@RequestParam("payload") String jsonResponse) throws ExecutionException, InterruptedException {
        //We check what type of response we got
        System.out.println(GsonFactory.createSnakeCase().fromJson(jsonResponse, Map.class));
        String type = GsonFactory.createSnakeCase().fromJson(jsonResponse, Map.class).get("type").toString();
        yes ++;
        System.out.println(yes);
        //Appropriate response depending on the type of action
        switch(type){
            case SlackManager.ACTION_BLOCK_ACTION:
                return slackManager.handleBlockAction(jsonResponse);
            case SlackManager.ACTION_VIEW_SUBMISSION:
                TimerTask task = new TimerTask() {
                    public void run() {
                        slackManager.handleViewSubmission(jsonResponse);
                };};
                    Timer timer = new Timer("Timer");
                    long delay  = 0;
                    timer.schedule(task, delay);
                return  "";
        }

        return "";
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
