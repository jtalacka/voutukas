package application.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PropertiesDto {

    private int id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<PollDto> polls;

    private String name;

}
