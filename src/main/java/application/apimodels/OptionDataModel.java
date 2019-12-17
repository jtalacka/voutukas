package application.apimodels;

import application.dto.PropertiesDto;
import application.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionDataModel {
    @JsonProperty("option_text")
    private String optionText;

    @JsonProperty("votes_count")
    private int votesCount;

    @JsonProperty("percentage_of_votes")
    private double percentageOfVotes;

    @JsonProperty("votes_by_users")
    @JsonIgnoreProperties({"answers"})
    private List<UserDto> votesByUsers;

    private List<PropertiesDto> properties;
}
