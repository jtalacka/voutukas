package application.apimodels;

import application.dto.OptionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"number_of_votes", "options"})
public class PollResultsDataModel {
    @JsonProperty("number_of_votes")
    private int numberOfVotes;

    private List<OptionDataModel> options;

    public void appendOption(OptionDataModel option){
        options.add(option);
    }

    public void setOption(int index, OptionDataModel option){
        options.set(index, option);
    }

    public PollResultsDataModel(){
        options = new ArrayList<>();
    }

}
