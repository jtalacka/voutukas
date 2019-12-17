package application.service;

import application.Repositories.OptionRepository;
import application.Repositories.PollRepository;
import application.domain.Option;
import application.domain.Poll;
import application.dto.OptionDto;
import application.mapper.OptionMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionService {

    private OptionRepository optionRepository;
    private OptionMapper optionMapper = new OptionMapper();
    private PollRepository pollRepository;

    public OptionService(OptionRepository optionRepository, PollRepository pollRepository) {
        this.optionRepository = optionRepository;
        this.optionMapper = optionMapper;
        this.pollRepository = pollRepository;
    }

    public OptionDto findOptionById(int id){
        return optionMapper.map(optionRepository.getOne(id));
    }

    public List<OptionDto> findAllOptions(){
        return convertToDtoList(optionRepository.findAll());
    }

    public List<OptionDto> findAllPollOptions(String timeStamp, String channelId){
        return convertToDtoList(optionRepository.findAllOptionsByPollID(new Poll(pollRepository.getId(timeStamp,channelId))));
    }

    private List<OptionDto> convertToDtoList(List<Option> optionList){
        List<OptionDto> optionDtoList = new ArrayList<>();
        optionList.forEach(option -> optionDtoList.add(optionMapper.map(option)));
        return optionDtoList;
    }

    public OptionDto findLastOption(){
        return optionMapper.map(optionRepository.findFirstByOrderByIdDesc());
    }

    public OptionDto findOptionByPollIdAndOptionName(String timeStamp, String channelId, String optionName){
        return optionMapper.map(optionRepository.findPollOptionsByPollIdAndOptionText(
                new Poll(pollRepository.getId(timeStamp,channelId)),
                optionName
        ));
    }

    @Transactional
    public void deleteOptionById(int id){
        optionRepository.deleteById(id);
    }

    @Transactional
    public void deleteOption(OptionDto optionDto){
        optionRepository.delete(optionMapper.map(optionDto));
    }

    @Transactional
    public void deleteOptionsByPollID(String timestamp, String channelID){
        optionRepository.deletePollOptionsByPollID(new Poll(pollRepository.getId(timestamp,channelID)));
    }

    @Transactional
    public void deleteOptionByPollIdAndOptionName(String timeStamp, String channelID, String optionName){
        optionRepository.deletePollOptionsByPollIdAndOptionText(new Poll(pollRepository.getId(timeStamp,channelID)),optionName);
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
