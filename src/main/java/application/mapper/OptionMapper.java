package application.mapper;

import application.domain.Option;
import application.dto.OptionDto;
import org.modelmapper.ModelMapper;

public class OptionMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public Option map(OptionDto optionDto){
        Option option = modelMapper.map(optionDto, Option.class);
        return option;
    }

    public OptionDto map(Option option){
        OptionDto optionDto = modelMapper.map(option, OptionDto.class);
        return optionDto;
    }
}
