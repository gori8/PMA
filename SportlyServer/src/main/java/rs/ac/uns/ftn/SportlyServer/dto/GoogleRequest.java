package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleRequest {

    private String email;

    private String idToken;

}
