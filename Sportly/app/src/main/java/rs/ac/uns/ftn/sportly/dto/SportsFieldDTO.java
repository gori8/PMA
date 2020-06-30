package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String category;

    private float rating;

    private boolean favorite;
}
