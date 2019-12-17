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

    /*
        Problema: gaunant DELETE, POST, PUT užklausas API fiksuoja ir vykdo užklausas du kartus.
            Pastebėjome, jog endpoint'us triggerina preflight OPTIONS užklausos.
            Įdomu tai, jog toks elgesys pastebimas ne su visomis OPTIONS užklausomis, o tik su tomis,
                kurių header'iuose yra laukas 'Access-Control-Request-Method' (pavyzdžiui lygus 'DELETE').
            Iš aplikacijos fronto dalies (arba per postman siunčiant OPTIONS užklausą,
                pvz, 'http://localhost:8080/api/poll/delete?time_stamp=1575993413.002100&channel_id=DNWQ3T8CB') bus triggerinama deletePollById funkcija.
            Taip pat radome laikiną sprendimą, kuris veikia neaišku kodėl - atkomentavus funkciją 'mockDelete', kuri net nėra kviečiama,
                OPTIONS užklausos nebetriggerina endpoint'o.
        Jokios informacijos apie tokį sistemos elgesį internete rasti nepavyko, nepadėjo ir StackOverflow klausimas:
                https://stackoverflow.com/questions/59293662/spring-boot-requests-from-axios-get-triggered-twice/59293771?noredirect=1#comment104793318_59293771

        Būtų įdomu išsiaiškinti, kodėl sistema taip elgiasi ir kaip tokio elgesio išvengti.
     */

    @DeleteMapping("/delete")
    public ResponseEntity deletePollById(@RequestParam String time_stamp, @RequestParam String channel_id)
    {
        System.out.println("hello");
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

    //Su šituo metodu bėda su preflight requestais susitvarko, nežinom kodėl :/
    @DeleteMapping(value = "/delete", consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity mockDelete(@RequestHeader("Access-Control-Request-Method") String accessControl){
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
