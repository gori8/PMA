package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ParticipationDTO {
    private Long id;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private Long eventId;
    private String eventName;
}
