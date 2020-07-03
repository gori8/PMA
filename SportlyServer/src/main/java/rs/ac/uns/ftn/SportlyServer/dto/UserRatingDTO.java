package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRatingDTO {
    private long id;

    private short value;

    private String comment;

    private String userEmail;

    private String creatorEmail;

    private String creatorFirstName;

    private String creatorLastName;

    private Long creatorId;

    private String creatorImageSrc;
}
