package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
