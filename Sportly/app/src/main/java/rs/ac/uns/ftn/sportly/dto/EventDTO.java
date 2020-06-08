package rs.ac.uns.ftn.sportly.dto;

import java.util.Date;
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

    private double price;

    private String curr;

    private String description;

    @Override
    public int compareTo(EventDTO e) {
        return getTimeFrom().compareTo(e.getTimeFrom());
    }
}


