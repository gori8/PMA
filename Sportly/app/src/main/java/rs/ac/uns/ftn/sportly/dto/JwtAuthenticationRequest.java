package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtAuthenticationRequest {
    private String username;    //email
    private String password;
}
