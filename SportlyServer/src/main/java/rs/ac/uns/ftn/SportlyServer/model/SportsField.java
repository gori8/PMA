package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SportsField {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private float latitude;

    private float longitude;

    private float rating;

    @ManyToMany(mappedBy = "favourite")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "sportsField")
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "sportsField")
    private List<FieldRating> fieldRatings = new ArrayList<>();
}
