package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BundleRatingDTO {

    private Long sportsFieldId;
    private float sportsFieldRating;
    private String sportsFieldComment;
    private List<PersonToRateDTO> people = new ArrayList<>();

}
