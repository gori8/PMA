package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class EventDTO {
    private Date timeFrom;

    private Date timeTo;

    private short numbOfPpl;

    private double price;

    private String curr;
}
