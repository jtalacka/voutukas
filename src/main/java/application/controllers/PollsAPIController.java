package application.controllers;

import application.dto.PollDto;
import application.dto.PollIdDto;
import application.service.PollService;
import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.chat.ChatDeleteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/poll")
public class PollsAPIController {
    private final PollService pollService;
    public PollsAPIController(PollService pollService)
    {
        this.pollService = pollService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<PollDto>> getAllPolls()
    {
        return ResponseEntity.ok(pollService.findAllPolls());
    }

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> getPollById(@RequestBody PollIdDto pollIdDto)
    {
        PollDto pollDto = pollService.findPollByTimeStampAnChannelID(pollIdDto.getTimeStamp(),pollIdDto.getChannelId());
        if(pollDto == null)
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            pollService.insert(pollDto);
            return ResponseEntity.ok().body(pollDto);
        }
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity deletePollById(@RequestParam String time_stamp, @RequestParam String channel_id)
    {
        pollService.deletePollById(time_stamp, channel_id);
        Slack slack = new Slack();
        try {
            slack.methods(System.getenv("SLACK_API_ACCESS_TOKEN")).chatDelete(ChatDeleteRequest.builder()
            .channel(channel_id)
            .ts(time_stamp)
            .build());
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @PutMapping(value = "/poll", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollDto> PutPoll(@RequestBody PollDto pollDto)
    {
        if(pollService.findPollByTimeStampAnChannelID(pollDto.getTimeStamp(),pollDto.getChannelId()) != null)
        {
            pollService.update(pollDto);
            return ResponseEntity.ok(pollDto);
        }
        else
        {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
