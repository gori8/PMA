package rs.ac.uns.ftn.SportlyServer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SyncDataDTO {

    private List<FriendDTO> friends = new ArrayList<>();
    private List<NotificationDTO> notifications = new ArrayList<>();
    private List<SportsFieldDTO> allSportsFields = new ArrayList<>();
}
