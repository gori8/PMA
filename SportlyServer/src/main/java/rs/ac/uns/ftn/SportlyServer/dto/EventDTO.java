package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
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

    @Override
    public int compareTo(EventDTO e) {
        return getDateFrom().compareTo(e.getDateFrom());
    }
}
