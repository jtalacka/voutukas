package application.mapper;

import application.domain.Option;
import application.dto.OptionDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class OptionMapper {

    ModelMapper modelMapper = new ModelMapper();

    public Option map(OptionDto optionDto){
        Option option = modelMapper.map(optionDto, Option.class);
        option.getPoll().getId().setTimeStamp(optionDto.getPoll().getTimeStamp());
        option.getPoll().getId().setChannelId(optionDto.getPoll().getChannelId());
        return option;
    }

    public OptionDto map(Option option){
        OptionDto optionDto = modelMapper.map(option, OptionDto.class);
        optionDto.getPoll().setTimeStamp(option.getPoll().getId().getTimeStamp());
        optionDto.getPoll().setChannelId(option.getPoll().getId().getChannelId());
        return optionDto;
    }
}
