package application.service;

import application.Repositories.PollRepository;
import application.apimodels.OptionDataModel;
import application.apimodels.PollResultsDataModel;
import application.apimodels.PollsByUserIdModel;
import application.domain.Poll;
import application.domain.User;
import application.dto.OptionDto;
import application.dto.PollDto;
import application.mapper.PollMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private PollMapper pollMapper = new PollMapper();
    private OptionService optionService;
    private PropertiesService propertiesService;

    public PollService(PollRepository pollRepository, OptionService optionService) {
        this.pollRepository = pollRepository;
        this.optionService = optionService;
    }

    public int getPollId(String timeStamp, String channelId){
        return pollRepository.getId(timeStamp,channelId);
    }

    public PollDto findPollByTimeStampAnChannelID(String timeStamp, String channleId) {
        return pollMapper.map(pollRepository.findByChannelIdAndTimeStampOrderByIdAsc(channleId, timeStamp));
    }

    public PollsByUserIdModel findPollsByUserId(String id){
        List<Poll> pollList = pollRepository.findPollByUser(new User(id));
        List<PollDto> pollDtoList = new ArrayList();
        pollList.forEach(poll -> pollDtoList.add(pollMapper.mapEntityToDtoWithOptions(poll, optionService.findAllPollOptions(poll.getTimeStamp(), poll.getChannelId()))));
        return new PollsByUserIdModel(pollDtoList);
    }

    //CIA VA
    public PollResultsDataModel getPollResultsDataById(String timeStamp, String channelId){
        PollResultsDataModel pollResults = new PollResultsDataModel();
        int totalVotes = 0;

        List<OptionDto> options = optionService.findAllPollOptions(timeStamp, channelId);

        for(OptionDto option : options){
            OptionDataModel optionData = new OptionDataModel(
                    option.getOptionText(),
                    option.getAnswers().size(),
                    0.0,
                    new ArrayList<>(option.getAnswers()),
                    null        //Change when properties work
            );
            pollResults.appendOption(optionData);
            totalVotes += option.getAnswers().size();
        }

        DecimalFormat dec = new DecimalFormat("#0.00");
        for(int i = 0; i < pollResults.getOptions().size(); i++){
            OptionDataModel option = pollResults.getOptions().get(i);
            option.setPercentageOfVotes(((int)((((double)option.getVotesCount()/totalVotes)*100)*100)/100.00)); //Set double precision to 2 decimal points
            pollResults.setOption(i, option);
        }

        pollResults.setNumberOfVotes(totalVotes);
        return pollResults;
    }

    public List<PollDto> findPollsByChannelID(String channelId){
        return convertToDtoList(pollRepository.findByChannelIdOrderByIdAsc(channelId));
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
        pollRepository.deleteByChannelIdAndTimeStamp(channelID,timeStamp);
    }

    @Transactional
    public void deleteAllUsersPolls(String id){
        pollRepository.selectAllPollsByUser(new User(id)).forEach(poll -> {
            deletePollById(poll.getTimeStamp(),poll.getChannelId());
        });
    }

    @Transactional
    public PollDto insert(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    @Transactional
    public PollDto insert(PollDto pollDto, List<OptionDto> optionDtoList){
        PollDto tempPollDto = savePoll(pollMapper.map(pollDto));
        optionDtoList.forEach(optionDto -> {
            optionService.insert(optionDto);
        });
        return tempPollDto;
    }

    @Transactional
    public PollDto update(PollDto pollDto){
        return savePoll(pollMapper.map(pollDto));
    }

    private PollDto savePoll(Poll poll){
        pollRepository.saveAndFlush(poll);
        return findPollByTimeStampAnChannelID(poll.getTimeStamp(),poll.getChannelId());
    }
}
