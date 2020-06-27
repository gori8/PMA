package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.EventRequestDTO;
import rs.ac.uns.ftn.SportlyServer.dto.EventRequestTypeEnum;
import rs.ac.uns.ftn.SportlyServer.dto.EventStatusEnum;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class EventRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User user;

    @ManyToOne(fetch = FetchType.EAGER)
    protected Event event;

    @Enumerated(EnumType.STRING)
    private EventRequestTypeEnum eventRequestType;

    private EventStatusEnum status;

    public EventRequestDTO createEventRequestDTO(){
        EventRequestDTO erDTO = new EventRequestDTO();
        erDTO.setId(this.getId());
        erDTO.setUserEmail(this.getUser().getEmail());
        erDTO.setUserFirstName(this.getUser().getFirstName());
        erDTO.setUserLastName(this.getUser().getLastName());
        erDTO.setEventId(this.getEvent().getId());
        erDTO.setEventName(this.getEvent().getName());
        erDTO.setEventRequestType(this.getEventRequestType());
        return erDTO;
    }


}
