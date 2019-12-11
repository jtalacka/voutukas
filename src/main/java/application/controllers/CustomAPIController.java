package application.controllers;

import application.apimodels.PollResultsDataModel;
import application.apimodels.PollsByUserIdModel;
import application.business.SlackManager;
import application.dto.PollCreationDto;
import application.dto.PollDto;
import application.dto.PollIdDto;
import application.service.OptionService;
import application.service.PollService;
import application.service.UserService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.auth.AuthTestRequest;
import com.github.seratch.jslack.api.methods.response.auth.AuthTestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomAPIController {

    private final PollService pollService;

    public CustomAPIController(OptionService optionService, PollService pollService, UserService userService){
        this.pollService = pollService;
    }

    @GetMapping(value = "/user/{userId}/polls")
    public ResponseEntity<PollsByUserIdModel> getAllUserPolls(@PathVariable String userId){
        PollsByUserIdModel polls = pollService.findPollsByUserId(userId);
        return ResponseEntity.ok(polls);
    }

    @GetMapping(value = "/poll/results")
    public ResponseEntity<PollResultsDataModel> getPollResultsByPollId(@RequestParam String time_stamp, @RequestParam String channel_id){
        PollResultsDataModel pollResults = pollService.getPollResultsDataById(time_stamp, channel_id);
        return ResponseEntity.ok(pollResults);
    }

    @GetMapping(value = "/auth/{authId}")
    public ResponseEntity getIfAuthorised(@PathVariable String authId)
    {
        Slack slack = new Slack();
        try {
            AuthTestResponse response = slack.methods().authTest(
              AuthTestRequest.builder().token(authId).build()
            );
            System.out.println(response);

            if(response.isOk())
            {
                return ResponseEntity.ok().build();
            }
            else
            {
                ResponseEntity.notFound().build();
            }
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(
            value = "/poll",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity createNewPoll(@RequestBody PollCreationDto newPoll){
        SlackManager slackManager = new SlackManager();

        String question = newPoll.getQuestion();
        List<String> answers =newPoll.getOptions();
        boolean InvalidAnswers = false;

        if(question.length() == 0 || question.length() == 75)
        {
            return ResponseEntity.badRequest().build();
        }
        for(String answer : answers)
        {
            if(answer.length() == 0 || answer.length() > 75) return ResponseEntity.badRequest().build();
        }

        slackManager.PostInitialMessage(newPoll.getChannelId(),newPoll.getQuestion(),newPoll.getOptions(),newPoll.getOwnerId(),newPoll.getOwnerName(),newPoll.getOwnerUserName(),newPoll.getProperties());


        return ResponseEntity.ok("Poll Created successfully");
    }
}
