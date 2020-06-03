package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Date timeFrom;

    private Date timeTo;

    private short numbOfPpl;

    private double price;

    private String curr;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User creator;

    @ManyToOne(fetch = FetchType.EAGER)
    protected SportsField sportsField;

    @ManyToMany(mappedBy = "participantEvents")
    private List<User> participants = new ArrayList<>();
}
