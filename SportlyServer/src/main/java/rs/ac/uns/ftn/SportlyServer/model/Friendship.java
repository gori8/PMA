package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipTypeEnum;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FriendshipTypeEnum friendshipType;

    @ManyToOne(fetch = FetchType.EAGER)
    private User friendshipRequester;

    @ManyToOne(fetch = FetchType.EAGER)
    private User friendshipReceiver;

    public FriendshipDTO createFriendshipDTO(){
        FriendshipDTO dto = new FriendshipDTO();
        dto.setId(this.getId());
        dto.setFriendshipReceiverEmail(this.getFriendshipReceiver().getEmail());
        dto.setFriendshipRequesterEmail(this.getFriendshipRequester().getEmail());
        return dto;
    }
}
