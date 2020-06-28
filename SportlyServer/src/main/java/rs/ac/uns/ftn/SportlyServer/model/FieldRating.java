package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class FieldRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private short value;

    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User userCreator;

    @ManyToOne(fetch = FetchType.EAGER)
    protected SportsField field;

    public FieldRatingDTO createRatingDTO(){
        FieldRatingDTO ratingDTO = new FieldRatingDTO();
        ratingDTO.setValue(this.getValue());
        ratingDTO.setComment(this.getComment());
        ratingDTO.setId(this.getId());
        ratingDTO.setFieldId(this.getField().getId());
        ratingDTO.setCreatorEmail(this.getUserCreator().getEmail());
        ratingDTO.setCreatorFirstName(this.getUserCreator().getFirstName());
        ratingDTO.setCreatorLastName(this.getUserCreator().getLastName());
        return ratingDTO;
    }

}
