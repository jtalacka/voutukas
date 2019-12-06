package application.controllers;

import application.apimodels.PollResultsDataModel;
import application.apimodels.PollsByUserIdModel;
import application.dto.PollIdDto;
import application.service.OptionService;
import application.service.PollService;
import application.service.UserService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.auth.AuthTestRequest;
import com.github.seratch.jslack.api.methods.response.auth.AuthTestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity<PollResultsDataModel> getPollResultsByPollId(@RequestBody PollIdDto pollID){
        PollResultsDataModel pollResults = pollService.getPollResultsDataById(pollID.getTimeStamp(), pollID.getChannelId());
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
}
