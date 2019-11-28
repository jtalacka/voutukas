package application.apimodels;

import application.dto.OptionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollResultsDataModel {
    @JsonProperty("number_of_votes")
    private int numberOfVotes;

    private List<OptionDataModel> options;

    //    data
//        NumberOfVotes
//        options
//           [
//              optionText
//              votes
//              percentage of votes
//              votesByUsers:
//                  user1, user2.....
//           ]
//        properties:{...}
}
