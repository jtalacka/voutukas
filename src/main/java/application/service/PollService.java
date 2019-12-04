package application.service;

import application.Repositories.PollRepository;
import application.domain.Poll;
import application.domain.PollID;
import application.domain.User;
import application.dto.OptionDto;
import application.dto.PollDto;
import application.mapper.PollMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
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

    @Transactional
    public void deletePollById(String timeStamp, String channelID){
        optionService.deleteOptionsByPollID(timeStamp,channelID);
        pollRepository.deleteById(new PollID(timeStamp,channelID));
    }

    @Transactional
    public void deleteAllUsersPolls(String id){
        pollRepository.selectAllPollsByUser(new User(id)).forEach(poll -> {
            deletePollById(poll.getId().getTimeStamp(),poll.getId().getChannelId());
        });
    }

    @Transactional
    public PollDto insert(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    @Transactional
    public PollDto insert(PollDto pollDto){
        PollDto tempPollDto = savePoll(pollMapper.map(pollDto));
        return tempPollDto;
    }

    @Transactional
    public PollDto update(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    private PollDto savePoll(Poll poll){
        pollRepository.save(poll);
        return findPollByID(poll.getId().getTimeStamp(),poll.getId().getChannelId());
    }
}
