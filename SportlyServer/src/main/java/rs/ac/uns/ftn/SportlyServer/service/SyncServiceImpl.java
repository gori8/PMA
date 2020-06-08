package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.Notification;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.SportsFieldRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SyncServiceImpl implements SyncService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SportsFieldRepository sportsFieldRepository;

    @Override
    public SyncDataDTO getSyncData(String username){
        SyncDataDTO syncDataDTO = new SyncDataDTO();


        //USER DATA
        User user = userRepository.findByEmail(username);

        for (User friend : user.getFriends()) {
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setId(friend.getId());
            friendDTO.setUsername(friend.getUsername());
            friendDTO.setFirstName(friend.getFirstName());
            friendDTO.setLastName(friend.getLastName());
            friendDTO.setEmail(friend.getEmail());

            syncDataDTO.getFriends().add(friendDTO);
        }

        for (SportsField sportsField : user.getFavourite()){
            syncDataDTO.getFavorite().add(sportsField.getId());
        }

        for (Notification notification : user.getNotifications()){
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setId(notification.getId());
            notificationDTO.setContent(notification.getContent());
            notificationDTO.setDate(notification.getDate());

            syncDataDTO.getNotifications().add(notificationDTO);
        }

        Collections.sort(syncDataDTO.getNotifications(), Collections.reverseOrder());

        for (Event event : user.getCreatorEvents()){
            syncDataDTO.getCreatorEvents().add(event.getId());
        }

        for (Event event : user.getParticipantEvents()){
            syncDataDTO.getParticipantEvents().add(event.getId());
        }

        //SPORTS FIELDS DATA

        List<SportsField> allSportsFields = sportsFieldRepository.findAll();

        for (SportsField sportsField : allSportsFields){
            SportsFieldDTO sfDTO = new SportsFieldDTO();
            sfDTO.setId(sportsField.getId());
            sfDTO.setName(sportsField.getName());
            sfDTO.setDescription(sportsField.getDescription());
            sfDTO.setLongitude(sportsField.getLongitude());
            sfDTO.setLatitude(sportsField.getLatitude());
            sfDTO.setRating(sportsField.getRating());
            sfDTO.setEvents(new ArrayList<EventDTO>());

            for (Event event : sportsField.getEvents()){
                if(event.getDateFrom().after(new Date())){
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setId(event.getId());
                    eventDTO.setName(event.getName());
                    eventDTO.setDateFrom(event.getDateFrom());
                    eventDTO.setDateTo(event.getDateTo());
                    eventDTO.setTimeFrom(event.getTimeFrom());
                    eventDTO.setTimeTo(event.getTimeTo());
                    eventDTO.setPrice(event.getPrice());
                    eventDTO.setNumbOfPpl(event.getNumbOfPpl());
                    eventDTO.setCurr(event.getCurr());
                    eventDTO.setDescription(event.getDescription());

                    sfDTO.getEvents().add(eventDTO);
                }

                Collections.sort(sfDTO.getEvents(), Collections.reverseOrder());
            }

            syncDataDTO.getAllSportsFields().add(sfDTO);
        }

        return syncDataDTO;
    }
}
