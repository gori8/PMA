package rs.ac.uns.ftn.sportly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Event {

    private String name;
    private String from;
    private String to;
    private short signedUpPlayers;
    private short totalPlayers;
    private String description;
    private String sport;

}
