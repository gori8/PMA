package rs.ac.uns.ftn.sportly.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventDTO implements Comparable<EventDTO> {

    private Long id;

    private String name;

    private Date dateFrom;

    private Date dateTo;

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

    @Override
    public int compareTo(EventDTO e) {
        return getTimeFrom().compareTo(e.getTimeFrom());
    }
}


