package application.controllers;

import application.apimodels.PollResultsDataModel;
import application.apimodels.PollsByUserIdModel;
import application.dto.PollIdDto;
import application.service.OptionService;
import application.service.PollService;
import application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
