package application.mapper;

import application.domain.Poll;
import application.dto.PollDto;
import org.modelmapper.ModelMapper;


public class PollMapper {

    ModelMapper modelMapper = new ModelMapper();


    public Poll map(PollDto pollDto){
        Poll poll = modelMapper.map(pollDto, Poll.class);
        return poll;
    }

    public PollDto map(Poll poll){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
        return pollDto;
    }

}
