package application.mapper;


import application.domain.Properties;
import application.dto.PropertiesDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

public class PropertiesMapper {

    ModelMapper modelMapper = new ModelMapper();

    public Properties map(PropertiesDto PropertiesDto){
        Properties Properties = modelMapper.map(PropertiesDto, Properties.class);
        return Properties;
    }

    public PropertiesDto map(Properties Properties){
        PropertiesDto PropertiesDto = modelMapper.map(Properties, PropertiesDto.class);
        return PropertiesDto;
    }

}
