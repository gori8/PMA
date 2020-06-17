package rs.ac.uns.ftn.sportly.model;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Event {

    private String name;
    private String location;
    private Date date = new Date();
    private String from;
    private String to;
    private short signedUpPlayers;
    private short totalPlayers;
    private String description;
    private String sport;

}
