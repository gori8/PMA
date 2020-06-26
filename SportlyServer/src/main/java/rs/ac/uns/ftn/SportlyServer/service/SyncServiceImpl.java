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
        syncDataDTO.getFriends().addAll(friendshipService.getFriendRequests(username));

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

                    if(event.getCreator().getId() == user.getId()){

                        eventDTO.setApplicationStatus("CREATOR");

                        for(User u : event.getParticipants()){

                            System.out.println("PARTICIPANT "+u.getId());

                            ApplierDTO applier = new ApplierDTO();

                            applier.setId(u.getId());
                            applier.setFirstName(u.getFirstName());
                            applier.setLastName(u.getLastName());
                            applier.setUsername(u.getUsername());
                            applier.setEmail(u.getEmail());
                            applier.setStatus("PARTICIPATING");

                            eventDTO.getApplicationList().add(applier);
                        }

                        for(User u : event.getQueue()){

                            System.out.println("QUEUE "+u.getId());

                            ApplierDTO applier = new ApplierDTO();

                            applier.setId(u.getId());
                            applier.setFirstName(u.getFirstName());
                            applier.setLastName(u.getLastName());
                            applier.setUsername(u.getUsername());
                            applier.setEmail(u.getEmail());
                            applier.setStatus("QUEUE");

                            eventDTO.getApplicationList().add(applier);
                        }
                    }

                    else if(containsUserInEventList(event.getParticipants(),user.getId())){

                        eventDTO.setApplicationStatus("PARTICIPANT");

                        for(User u : event.getParticipants()){

                            System.out.println("PARTICIPANT "+u.getId());

                            ApplierDTO applier = new ApplierDTO();

                            applier.setId(u.getId());
                            applier.setFirstName(u.getFirstName());
                            applier.setLastName(u.getLastName());
                            applier.setUsername(u.getUsername());
                            applier.setEmail(u.getEmail());
                            applier.setStatus("PARTICIPATING");

                            eventDTO.getApplicationList().add(applier);
                        }
                    }

                    else if(containsUserInEventList(event.getQueue(),user.getId())){

                        eventDTO.setApplicationStatus("QUEUE");

                        for(User u : event.getParticipants()){

                            System.out.println("PARTICIPANT "+u.getId());

                            ApplierDTO applier = new ApplierDTO();

                            applier.setId(u.getId());
                            applier.setFirstName(u.getFirstName());
                            applier.setLastName(u.getLastName());
                            applier.setUsername(u.getUsername());
                            applier.setEmail(u.getEmail());
                            applier.setStatus("PARTICIPATING");

                            eventDTO.getApplicationList().add(applier);
                        }
                    }else{
                        eventDTO.setApplicationStatus("NONE");
                    }

                    sfDTO.getEvents().add(eventDTO);
                }

                Collections.sort(sfDTO.getEvents(), Collections.reverseOrder());
            }

            syncDataDTO.getAllSportsFields().add(sfDTO);
        }

        return syncDataDTO;
    }


    public boolean containsUserInEventList(final List<User> list, final Long userId){
        return list.stream().filter(o -> o.getId().equals(userId)).findFirst().isPresent();
    }
}
