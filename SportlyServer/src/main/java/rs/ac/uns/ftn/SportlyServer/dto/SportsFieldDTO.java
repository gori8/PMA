package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SportsFieldDTO {

    private String name;

    private String description;

    private float latitude;

    private float longitude;
}
