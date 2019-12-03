package application.controllers;

import application.apimodels.PollResultsDataModel;
import application.apimodels.PollsByUserIdModel;
import application.domain.Option;
import application.domain.User;
import application.dto.OptionDto;
import application.dto.PollDto;
import application.dto.PollIdDto;
import application.dto.UserDto;
import application.service.OptionService;
import application.service.PollService;
import application.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private OptionService optionService;
    private PollService pollService;
    private UserService userService;

    public ApiController(OptionService optionService, PollService pollService, UserService userService){
        this.optionService = optionService;
        this.pollService = pollService;
        this.userService = userService;
    }

    @GetMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> getPollById(@RequestBody PollIdDto pollID){
        PollDto poll = pollService.findPollByID(pollID.getTimeStamp(), pollID.getChannelId());
        return ResponseEntity.ok(poll);
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

    @DeleteMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deletePollById(@RequestBody PollIdDto pollID)
    {
        pollService.deletePollById(pollID.getTimeStamp(),pollID.getChannelId());
    }

    @DeleteMapping(value = "/user{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePollById(@PathVariable String userId)
    {
        userService.deleteUserByID(userId);
    }

    @DeleteMapping(value = "/answer/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePollById(@PathVariable int id)
    {
        optionService.deleteOptionById(id);
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> PostUser(@RequestBody UserDto userDto)
    {
        if(userService.findUserByID(userDto.getId()) != null)
        {
            return ResponseEntity.badRequest().body(null);
        }
        else
        {
            userService.insert(userDto);
            return ResponseEntity.ok(userDto);
        }
    }

    @PostMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> PostPoll(@RequestBody PollDto pollDto)
    {
        if(pollService.findPollByID(pollDto.getTimeStamp(),pollDto.getChannelId()) != null)
        {
            return ResponseEntity.badRequest().body(null);
        }
        else
        {
            pollService.insert(pollDto);
            return ResponseEntity.ok(pollDto);
        }
    }

    @PostMapping(value = "/answer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OptionDto> PostAnswer(@RequestBody OptionDto optionDto)
    {
        if(optionService.findOptionById(optionDto.getId()) != null)
        {
            return ResponseEntity.badRequest().body(null);
        }
        else
        {
            optionService.insert(optionDto);
            return ResponseEntity.ok(optionDto);
        }
    }

    @PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> PutUser(@RequestBody UserDto userDto)
    {
        if(userService.findUserByID(userDto.getId()) != null)
        {
            userService.update(userDto);
            return ResponseEntity.ok(userDto);
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> PutPoll(@RequestBody PollDto pollDto)
    {
        if(pollService.findPollByID(pollDto.getTimeStamp(),pollDto.getChannelId()) != null)
        {
            pollService.update(pollDto);
            return ResponseEntity.ok(pollDto);
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/answer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OptionDto> PutAnswer(@RequestBody OptionDto optionDto)
    {
        if(optionService.findOptionById(optionDto.getId()) != null)
        {
            optionService.update(optionDto);
            return ResponseEntity.ok(optionDto);
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
