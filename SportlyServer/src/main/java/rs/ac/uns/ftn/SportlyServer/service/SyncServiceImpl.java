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

    @Autowired
    FriendshipService friendshipService;

    @Autowired
    EventService eventService;

    @Override
    public SyncDataDTO getSyncData(String username){
        SyncDataDTO syncDataDTO = new SyncDataDTO();

        //USER DATA
        User user = userRepository.findByEmail(username);
        if(user == null){
            return null;
        }

        syncDataDTO.setFriends(friendshipService.getUserFriends(username));

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

        List<EventDTO> creatorEventDTOs = eventService.getCreatorEvents(username);
        List<Long> creatorEventIDs = new ArrayList<>();
        for(EventDTO event : creatorEventDTOs){
            creatorEventIDs.add(event.getId());
        }
        syncDataDTO.setCreatorEvents(creatorEventIDs);

        List<EventDTO> participantEventDTOs = eventService.getParticipantEvents(username);
        List<Long> participantEventIDs = new ArrayList<>();
        for(EventDTO event : participantEventDTOs){
            participantEventIDs.add(event.getId());
        }
        syncDataDTO.setParticipantEvents(participantEventIDs);

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
            sfDTO.setCategory(sportsField.getCategory());
            sfDTO.setEvents(new ArrayList<EventDTO>());

            for (Event event : sportsField.getEvents()){
                if(event.getDateFrom().after(new Date())){
                    EventDTO eventDTO = event.createEventDTO();
                    sfDTO.getEvents().add(eventDTO);
                }

                Collections.sort(sfDTO.getEvents(), Collections.reverseOrder());
            }

            syncDataDTO.getAllSportsFields().add(sfDTO);
        }

        return syncDataDTO;
    }
}
