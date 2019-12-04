package application.service;

import application.Repositories.OptionRepository;
import application.domain.Option;
import application.domain.Poll;
import application.domain.PollID;
import application.dto.OptionDto;
import application.mapper.OptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionService {

    private OptionRepository optionRepository;
    private OptionMapper optionMapper = new OptionMapper();

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
        this.optionMapper = optionMapper;
    }

    public OptionDto findOptionById(int id){
        return optionMapper.map(optionRepository.getOne(id));
    }

    public List<OptionDto> findAllOptions(){
        return convertToDtoList(optionRepository.findAll());
    }

    public List<OptionDto> findAllPollOptions(String timeStamp, String channelId){
        return convertToDtoList(optionRepository.findAllOptionsByPollID(new Poll(new PollID(timeStamp,channelId))));
    }

    private List<OptionDto> convertToDtoList(List<Option> optionList){
        List<OptionDto> optionDtoList = new ArrayList<>();
        optionList.forEach(option -> {
            optionDtoList.add(optionMapper.map(option));
        });
        return optionDtoList;
    }

    public OptionDto findLastOption(){
        return optionMapper.map(optionRepository.findFirstByOrderByIdDesc());
    }

    public OptionDto findOptionByPollIdAndOptionName(String timeStamp, String channelId, String optionName){
        return optionMapper.map(optionRepository.findPollOptionsByPollIdAndOptionText(
                new Poll(new PollID(timeStamp,channelId)),
                optionName
        ));
    }

    @Transactional
    public void deleteOptionById(int id){
        optionRepository.deleteById(id);
    }

    @Transactional
    public void deleteOptionsByPollID(String timestamp, String channelID){
        optionRepository.deletePollOptionsByPollID(new Poll(new PollID(timestamp,channelID)));
    }

    @Transactional
    public void deleteOptionByPollIdAndOptionName(String timeStamp, String channelID, String optionName){
        optionRepository.deletePollOptionsByPollIdAndOptionText(new Poll(new PollID(timeStamp,channelID)),optionName);
    }

    @Transactional
    public OptionDto insert(OptionDto optionDto){
        return saveOption(optionMapper.map(optionDto));
    }

    @Transactional
    public OptionDto update(OptionDto optionDto){
        return saveOption(optionMapper.map(optionDto));
    }

    private OptionDto saveOption(Option option){
        optionRepository.save(option);
        return findOptionById(option.getId());
    }

}
