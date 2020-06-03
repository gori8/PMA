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

    private Date timeFrom;

    private Date timeTo;

    private short numbOfPpl;

    private double price;

    private String curr;

    private String description;

    @Override
    public int compareTo(EventDTO e) {
        return getTimeFrom().compareTo(e.getTimeFrom());
    }
}
