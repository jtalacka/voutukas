package application.controllers;

import application.domain.PollID;
import application.dto.PollDto;
import application.service.OptionService;
import application.service.PollService;
import application.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ApiController {

    private OptionService optionService;
    private PollService pollService;
    private UserService userService;

    public ApiController(OptionService optionService, PollService pollService, UserService userService){
        this.optionService = optionService;
        this.pollService = pollService;
        this.userService = userService;
    }

    @GetMapping("/hi")
    public String hello(){
        return "Hi there";
    }

    @GetMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> getPollById(@RequestBody PollID pollID){
        PollDto poll = pollService.findPollByID(pollID.getTimeStamp(), pollID.getChannelId());
        return ResponseEntity.ok(poll);
    }
}
