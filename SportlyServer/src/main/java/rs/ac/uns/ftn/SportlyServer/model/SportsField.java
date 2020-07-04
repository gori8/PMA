package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.SportsFieldDTO;

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

    private double latitude;

    private double longitude;

    private float rating;

    private String category;

    private String photoReference;

    @Column(unique=true)
    private String place_id;

    @ManyToMany(mappedBy = "favourite")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "sportsField")
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "field")
    private List<FieldRating> fieldRatings = new ArrayList<>(); //lista ocena koje je dobio od korisnika

    public SportsFieldDTO createSportsFieldDTO(){
        SportsFieldDTO sportsFieldDTO = new SportsFieldDTO();
        sportsFieldDTO.setId(this.getId());
        sportsFieldDTO.setName(this.getName());
        sportsFieldDTO.setDescription(this.getDescription());
        sportsFieldDTO.setLatitude(this.getLatitude());
        sportsFieldDTO.setLongitude(this.getLongitude());
        sportsFieldDTO.setRating(this.getRating());
        sportsFieldDTO.setCategory(this.getCategory());
        sportsFieldDTO.setImageRef(this.getPhotoReference());
        return sportsFieldDTO;
    }
}
