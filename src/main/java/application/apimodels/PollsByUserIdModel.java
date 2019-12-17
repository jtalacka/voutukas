package application.apimodels;

import application.dto.PollDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollsByUserIdModel {

    @JsonIgnoreProperties({"owner"})
    private List<PollDto> polls;
}
