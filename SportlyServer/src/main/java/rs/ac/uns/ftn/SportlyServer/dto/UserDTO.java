package rs.ac.uns.ftn.SportlyServer.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String ime;
    private String prezime;
    private String email;
    private String password;
    private String token;
    //private String photoUrl;
    private float rating;
    private int expiresIn;


}
