package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FriendshipDTO {
    private Long id;
    private String friendshipRequesterEmail;
    private String friendshipReceiverEmail;
}
