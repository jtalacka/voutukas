package application.dto;

        import lombok.*;

        import java.util.HashSet;
        import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PropertiesDto {

    private int id;

    private String name;

    public PropertiesDto(int id) {
        this.id = id;
    }
    public PropertiesDto(String name) {
        this.name = name;
    }
}
