package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.model.Event;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SportsFieldDTO {

    private Long id;

    private String name;

    private String description;

    private float latitude;

    private float longitude;

    private List<EventDTO> events;
}
