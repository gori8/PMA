package rs.ac.uns.ftn.sportly.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserWithRatingsDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String imageSrc;
    private List<UserRatingDTO> ratings;

}
