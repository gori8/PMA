package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.EventDTO;
import rs.ac.uns.ftn.SportlyServer.dto.RatingSchedulerEnum;

import javax.persistence.*;
import java.text.SimpleDateFormat;
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

    @Enumerated(EnumType.STRING)
    private RatingSchedulerEnum ratingSchedulerEnum;

    @ManyToOne(fetch = FetchType.EAGER)
    private User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    private SportsField sportsField;

    @OneToMany(mappedBy = "event")
    private List<EventRequest> eventRequests = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    private List<Participation> participationList = new ArrayList<>();

    public EventDTO createEventDTO(){

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");

        EventDTO dto = new EventDTO();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setDateFrom(dateFormatter.format(this.getDateFrom()));
        dto.setDateTo(dateFormatter.format(this.getDateTo()));
        dto.setTimeFrom(this.getTimeFrom());
        dto.setTimeTo(this.getTimeTo());
        dto.setNumbOfPpl(this.getNumbOfPpl());
        dto.setPrice(this.getPrice());
        dto.setCurr(this.getCurr());
        dto.setDescription(this.getDescription());
        dto.setSportsFieldId(this.sportsField.getId());
        dto.setCategory(this.sportsField.getCategory());
        dto.setCreatorId(this.creator.getId());
        dto.setCreator(this.creator.getFirstName()+ " " +creator.getLastName());
        dto.setImageRef(this.sportsField.getPhotoReference());
        return dto;
    }
}
