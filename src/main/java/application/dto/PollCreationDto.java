package application.dto;

import application.domain.Option;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollCreationDto {

    private String channelId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<String> properties;

    private List<String> options;

    private String question;

    private String ownerId;

    private String ownerName;

    private String ownerUserName;

}
