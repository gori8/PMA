package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FieldRatingDTO {
    private long id;

    private short value;

    private String comment;

    private long fieldId;

    private String creatorEmail;

    private String creatorFirstName;

    private String creatorLastName;
}
