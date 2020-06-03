package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.Notification;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

@Service
public class SyncServiceImpl implements SyncService {

    @Autowired
    UserRepository userRepository;

    @Override
    public SyncDataDTO getSyncData(String username){
        SyncDataDTO syncDataDTO = new SyncDataDTO();

        User user = userRepository.findOneByUsername(username);

        for (User friend : user.getFriends()) {
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setUsername(friend.getUsername());
            friendDTO.setFirstName(friend.getFirstName());
            friendDTO.setLastName(friend.getLastName());
            friendDTO.setEmail(friend.getEmail());

            syncDataDTO.getFriends().add(friendDTO);
        }

        for (SportsField sportsField : user.getFavourite()){
            SportsFieldDTO sportsFieldDTO = new SportsFieldDTO();
            sportsFieldDTO.setName(sportsField.getName());
            sportsFieldDTO.setDescription(sportsField.getDescription());
            sportsFieldDTO.setLatitude(sportsField.getLatitude());
            sportsFieldDTO.setLongitude(sportsField.getLongitude());

            syncDataDTO.getFavorite().add(sportsFieldDTO);
        }

        for (Notification notification : user.getNotifications()){
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setContent(notification.getContent());

            syncDataDTO.getNotifications().add(notificationDTO);
        }

        for (Event event : user.getCreatorEvents()){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setTimeFrom(event.getTimeFrom());
            eventDTO.setTimeTo(event.getTimeTo());
            eventDTO.setPrice(event.getPrice());
            eventDTO.setNumbOfPpl(event.getNumbOfPpl());
            eventDTO.setCurr(event.getCurr());

            syncDataDTO.getCreatorEvents().add(eventDTO);
        }

        for (Event event : user.getParticipantEvents()){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setTimeFrom(event.getTimeFrom());
            eventDTO.setTimeTo(event.getTimeTo());
            eventDTO.setPrice(event.getPrice());
            eventDTO.setNumbOfPpl(event.getNumbOfPpl());
            eventDTO.setCurr(event.getCurr());

            syncDataDTO.getParticipantEvents().add(eventDTO);
        }

        return syncDataDTO;
    }
}
