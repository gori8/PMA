package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private short value;

    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User user;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User userCreator;

    public UserRatingDTO createRatingDTO(){
        UserRatingDTO ratingDTO = new UserRatingDTO();
        ratingDTO.setId(this.getId());
        ratingDTO.setValue(this.getValue());
        ratingDTO.setComment(this.getComment());
        ratingDTO.setUserEmail(this.getUser().getEmail());
        ratingDTO.setCreatorEmail(this.getUserCreator().getEmail());
        ratingDTO.setCreatorFirstName(this.getUserCreator().getFirstName());
        ratingDTO.setCreatorLastName(this.getUserCreator().getLastName());
        return ratingDTO;
    }

}
