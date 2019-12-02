package application.service;

import application.Repositories.PropertiesRepository;
import application.domain.Properties;
import application.dto.PropertiesDto;
import application.mapper.PropertiesMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertiesService {

    private PropertiesRepository propertiesRepository;
    private PropertiesMapper propertiesMapper = new PropertiesMapper();

    public PropertiesDto findPropertieByName(String name){
        return propertiesMapper.map(propertiesRepository.findProperty(name));
    }

    public List<PropertiesDto> findAllProperties(){
         List<PropertiesDto> propertiesDtos = new ArrayList<>();
        for(Properties property : propertiesRepository.findAll()){
            propertiesDtos.add(propertiesMapper.map(property));
        }
        return propertiesDtos;
    }
}
