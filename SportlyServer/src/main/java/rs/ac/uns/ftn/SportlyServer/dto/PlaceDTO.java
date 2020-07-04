package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlaceDTO {

    private GeometryDTO geometry;
    private String place_id;
    private String name;
    private List<PhotoDTO> photos;

}
