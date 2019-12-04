package application.mapper;

import application.domain.Poll;
import application.domain.PollID;
import application.domain.User;
import application.dto.PollDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


public class PollMapper {

    ModelMapper modelMapper = new ModelMapper();

    public Poll map(PollDto pollDto){
        Poll poll = modelMapper.map(pollDto, Poll.class);
        poll.getId().setTimeStamp(pollDto.getTimeStamp());
        poll.getId().setChannelId(pollDto.getChannelId());
        return poll;
    }

    public PollDto map(Poll poll){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
        pollDto.setTimeStamp(poll.getId().getTimeStamp());
        pollDto.setChannelId(poll.getId().getChannelId());
        return pollDto;
    }

}
