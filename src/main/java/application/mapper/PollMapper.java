package application.mapper;

import application.domain.Poll;
import application.dto.OptionDto;
import application.dto.PollDto;
import org.modelmapper.ModelMapper;

import java.util.List;


public class PollMapper {

    private final ModelMapper modelMapper = new ModelMapper();


    public Poll map(PollDto pollDto){
        Poll poll = modelMapper.map(pollDto, Poll.class);
        return poll;
    }

    public PollDto map(Poll poll){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
        return pollDto;
    }
    public  PollDto mapEntityToDtoWithOptions(Poll poll, List<OptionDto> optionDtoList){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
               pollDto.setOptions(optionDtoList);
        return pollDto;
    }

}
