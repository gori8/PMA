package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.model.User;

@Getter
@Setter
@NoArgsConstructor
public class FriendDTO {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String friendType;

}
