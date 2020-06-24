package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.EventDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Date dateFrom;

    private Date dateTo;

    private String timeFrom;

    private String timeTo;

    private short numbOfPpl;

    private double price;

    private String curr;

    private String description;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    protected SportsField sportsField;

    @ManyToMany(mappedBy = "participantEvents")
    private List<User> participants = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<EventRequest> eventRequests = new ArrayList<>();

    public EventDTO createEventDTO(){
        EventDTO dto = new EventDTO();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setDateFrom(this.getDateFrom());
        dto.setDateTo(this.getDateTo());
        dto.setTimeFrom(this.getTimeFrom());
        dto.setTimeTo(this.getTimeTo());
        dto.setNumbOfPpl(this.getNumbOfPpl());
        dto.setNumOfParticipants((short)(this.getParticipants().size()+1));
        dto.setPrice(this.getPrice());
        dto.setCurr(this.getCurr());
        dto.setDescription(this.getDescription());
        dto.setSportsFieldId(this.sportsField.getId());
        return dto;
    }
}
