package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.model.Event;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO implements Comparable<EventDTO> {

    private Long id;

    private String name;

    private String dateFrom;

    private String dateTo;

    private String timeFrom;

    private String timeTo;

    private short numbOfPpl;

    private short numOfParticipants;

    private double price;

    private String curr;

    private String description;

    private Long sportsFieldId;

    private List<ApplierDTO> applicationList = new ArrayList<>();

    private String applicationStatus;

    private String category;

    private String creator;

    private Long creatorId;

    private String imageRef;

    @Override
    public int compareTo(EventDTO e) {
        return getDateFrom().compareTo(e.getDateFrom());
    }
}
