package application.mapper;

import application.domain.Option;
import application.dto.OptionDto;
import org.modelmapper.ModelMapper;

public class OptionMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final PollMapper pollMapper = new PollMapper();

    public Option map(OptionDto optionDto){
        Option option;
        if (optionDto != null) {
            option = modelMapper.map(optionDto, Option.class);
            option.setPoll(pollMapper.map(optionDto.getPollId()));
            return option;
        }
        return null;
    }

    public OptionDto map(Option option){
        OptionDto optionDto;
        if(option != null) {
            optionDto = modelMapper.map(option, OptionDto.class);
            optionDto.setPollId(pollMapper.map(option.getPoll()));
            return optionDto;
        }
        return null;
    }
}
