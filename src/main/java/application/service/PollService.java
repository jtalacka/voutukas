package application.service;

import application.Repositories.PollRepository;
import application.domain.Poll;
import application.domain.PollID;
import application.domain.User;
import application.dto.OptionDto;
import application.dto.PollDto;
import application.mapper.PollMapper;

import java.util.ArrayList;
import java.util.List;

public class PollService {

    private PollRepository pollRepository;
    private PollMapper pollMapper = new PollMapper();
    private OptionService optionService;

    public PollService(PollRepository pollRepository, OptionService optionService) {
        this.pollRepository = pollRepository;
        this.pollMapper = pollMapper;
        this.optionService = optionService;
    }

    public PollDto findPollByID(String timeStamp, String channleId){
        return pollMapper.map(pollRepository.getOne(new PollID(timeStamp,channleId)));
    }

    public List<PollDto> findPollsByUserId(String id){
        return convertToDtoList(pollRepository.findPollByUser(new User(id)));
    }

    public List<PollDto> findPollsByChannelID(String channelId){
        return convertToDtoList(pollRepository.findByIdChannelId(channelId));
    }

    public List<PollDto> findAllPolls(){
        return convertToDtoList(pollRepository.findAll());
    }

    private List<PollDto> convertToDtoList(List<Poll> pollList){
        List<PollDto> pollDtoList = new ArrayList();
        pollList.forEach(poll -> {
            pollDtoList.add(pollMapper.map(poll));
        });
        return pollDtoList;
    }

    public void deletePollById(String timeStamp, String channelID){
        pollRepository.deleteById(new PollID(timeStamp,channelID));
    }

    public void deleteAllUsersPolls(String id){
        pollRepository.deleteAllPollByUser(new User(id));
    }

    public PollDto insert(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    public PollDto insert(PollDto pollDto, List<OptionDto> optionDtoList){
        optionDtoList.forEach(optionDto -> {
            optionService.insert(optionDto);
        });
        return savePoll(pollMapper.map(pollDto));
    }

    public PollDto update(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    private PollDto savePoll(Poll poll){
        pollRepository.save(poll);
        return findPollByID(poll.getId().getTimeStamp(),poll.getId().getChannelId());
    }
}
