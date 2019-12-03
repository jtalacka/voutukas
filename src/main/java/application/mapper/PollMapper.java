package application.mapper;

import application.domain.Poll;
import application.domain.PollID;
import application.dto.OptionDto;
import application.dto.PollDto;
import org.modelmapper.ModelMapper;

import java.util.List;


public class PollMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public Poll mapDtoToEntity(PollDto pollDto){
        Poll poll = modelMapper.map(pollDto, Poll.class);
        poll.setId(new PollID(pollDto.getTimeStamp(), pollDto.getChannelId()));
        return poll;
    }

    public PollDto mapEntityToDto(Poll poll){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
        pollDto.setChannelId(poll.getId().getChannelId());
        pollDto.setTimeStamp(poll.getId().getTimeStamp());
        return pollDto;
    }
    public  PollDto mapEntityToDtoWithOptions(Poll poll, List<OptionDto> optionDtoList){
        PollDto pollDto = modelMapper.map(poll, PollDto.class);
        pollDto.setChannelId(poll.getId().getChannelId());
        pollDto.setTimeStamp(poll.getId().getTimeStamp());
               pollDto.setOptions(optionDtoList);
        return pollDto;
    }

}
