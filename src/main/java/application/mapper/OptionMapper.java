package application.mapper;

import application.domain.Option;
import application.dto.OptionDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class OptionMapper {

    ModelMapper modelMapper = new ModelMapper();

    public Option map(OptionDto optionDto){
        Option option;
        if (optionDto != null) {
            option = modelMapper.map(optionDto, Option.class);
            return option;
        }
        return null;
    }

    public OptionDto map(Option option){
        OptionDto optionDto;
        if(option != null) {
            optionDto = modelMapper.map(option, OptionDto.class);
            return optionDto;
        }
        return null;
    }
}
