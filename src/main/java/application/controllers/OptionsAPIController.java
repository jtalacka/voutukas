package application.controllers;

import application.dto.OptionDto;
import application.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answer")
public class OptionsAPIController {
    private final OptionService optionService;
    public OptionsAPIController(OptionService optionService)
    {
        this.optionService = optionService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<OptionDto>> getAllOptions()
    {
        return ResponseEntity.ok(optionService.findAllOptions());
    }

    @GetMapping(value = "/{answerId}")
    public ResponseEntity<OptionDto> getAnswerById(@PathVariable int answerId)
    {
        OptionDto optionDto = optionService.findOptionById(answerId);
        if(optionDto == null)
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            optionService.insert(optionDto);
            return ResponseEntity.ok().body(optionDto);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOptionById(@PathVariable int id)
    {
        optionService.deleteOptionById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
