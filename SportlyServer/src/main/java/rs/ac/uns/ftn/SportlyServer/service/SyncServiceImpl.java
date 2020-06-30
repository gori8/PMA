package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.*;
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

            if(sportsFieldIsFavorite(user.getFavourite(),sportsField.getId())){
                sfDTO.setFavorite(true);
            }else{
                sfDTO.setFavorite(false);
            }

            for (Event event : sportsField.getEvents()){
                System.out.println("EVENT FROM: "+event.getDateFrom());
                Long time = new Date().getTime();
                Date date = new Date(time - time % (24 * 60 * 60 * 1000)-2*(60 * 60 * 1000));
                System.out.println("TODAY: "+date);
                if(!event.getDateFrom().before(date)){

                    EventDTO eventDTO = event.createEventDTO();

                    short numOfParticipants = 1;

                    if(event.getCreator().getId() == user.getId()){

                        eventDTO.setApplicationStatus("CREATOR");

                        for(EventRequest eventRequest : event.getEventRequests()){
                            if(eventRequest.getStatus() == EventStatusEnum.PENDING){
                                User u = eventRequest.getUser();

                                ApplierDTO applier = new ApplierDTO();

                                applier.setId(u.getId());
                                applier.setFirstName(u.getFirstName());
                                applier.setLastName(u.getLastName());
                                applier.setUsername(u.getUsername());
                                applier.setEmail(u.getEmail());
                                applier.setRequestId(eventRequest.getId());
                                applier.setParticipationId(null);

                                if(eventRequest.getEventRequestType() == EventRequestTypeEnum.REQUESTED_BY_PARTICIPANT){
                                    applier.setStatus("QUEUE");
                                }else{
                                    applier.setStatus("INVITED");
                                }

                                eventDTO.getApplicationList().add(applier);
                            }
                        }
                    }

                    else if(containsUserInParticipationList(event.getParticipationList(),user.getId())){

                        eventDTO.setApplicationStatus("PARTICIPANT");

                    }

                    else if(containsUserInEventRequestsParticipant(event.getEventRequests(),user.getId())){

                        eventDTO.setApplicationStatus("QUEUE");


                    }else if(containsUserInEventRequestsCreator(event.getEventRequests(),user.getId())){

                        eventDTO.setApplicationStatus("INVITED");

                        for(EventRequest eventRequest : event.getEventRequests()){
                            if(eventRequest.getUser().getId() == user.getId()){
                                ApplierDTO applier = new ApplierDTO();

                                applier.setId(user.getId());
                                applier.setFirstName(user.getFirstName());
                                applier.setLastName(user.getLastName());
                                applier.setUsername(user.getUsername());
                                applier.setEmail(user.getEmail());
                                applier.setRequestId(eventRequest.getId());
                                applier.setParticipationId(null);
                                applier.setStatus("INVITED");

                                eventDTO.getApplicationList().add(applier);
                            }
                        }

                    }else{

                        eventDTO.setApplicationStatus("NONE");

                    }

                    for(Participation participation : event.getParticipationList()){
                        if(!participation.isDeleted()){
                            User u = participation.getUser();

                            ApplierDTO applier = new ApplierDTO();

                            applier.setId(u.getId());
                            applier.setFirstName(u.getFirstName());
                            applier.setLastName(u.getLastName());
                            applier.setUsername(u.getUsername());
                            applier.setEmail(u.getEmail());
                            applier.setStatus("PARTICIPATING");
                            applier.setRequestId(null);
                            applier.setParticipationId(participation.getId());

                            eventDTO.getApplicationList().add(applier);

                            numOfParticipants++;
                        }
                    }

                    for(EventRequest er : event.getEventRequests()){

                    }

                    eventDTO.setNumOfParticipants(numOfParticipants);

                    sfDTO.getEvents().add(eventDTO);
                }

                Collections.sort(sfDTO.getEvents(), Collections.reverseOrder());
            }

            syncDataDTO.getAllSportsFields().add(sfDTO);
        }

        return syncDataDTO;
    }


    public boolean containsUserInParticipationList(final List<Participation> list, final Long userId){
        return list.stream().filter(o -> o.getUser().getId().equals(userId) && o.isDeleted()==false).findFirst().isPresent();
    }

    public boolean containsUserInEventRequestsParticipant(final List<EventRequest> list, final Long userId){
        return list.stream().filter(o -> o.getUser().getId().equals(userId) && o.getStatus()==EventStatusEnum.PENDING && o.getEventRequestType() == EventRequestTypeEnum.REQUESTED_BY_PARTICIPANT).findFirst().isPresent();
    }

    public boolean containsUserInEventRequestsCreator(final List<EventRequest> list, final Long userId){
        return list.stream().filter(o -> o.getUser().getId().equals(userId) && o.getStatus()==EventStatusEnum.PENDING && o.getEventRequestType() == EventRequestTypeEnum.REQUESTED_BY_CREATOR).findFirst().isPresent();
    }

    public boolean sportsFieldIsFavorite(final List<SportsField> list, final Long sportsFieldId){
        return list.stream().filter(o -> o.getId().equals(sportsFieldId)).findFirst().isPresent();
    }

}
