package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit.http.DELETE;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.*;
import rs.ac.uns.ftn.SportlyServer.repository.*;

import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SportsFieldRepository spRepository;

    @Autowired
    EventRequestRepository erRepository;

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Override
    public List<EventDTO> getCreatorEvents(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();

        System.out.println("getCreatorEvents SIZE: "+user.getCreatorEvents().size());

        for(Event event : user.getCreatorEvents()){
            if(!event.isDeleted()){

                EventDTO eventDTO = event.createEventDTO();

                eventDTOs.add(eventDTO);
            }
        }

        return eventDTOs;
    }

    @Override
    public List<EventDTO> getParticipantEvents(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Participation participation : user.getParticipationList()){
            if(!participation.isDeleted()){

                Event event = participation.getEvent();

                if(!event.isDeleted()) {

                    EventDTO eventDTO = event.createEventDTO();

                    eventDTOs.add(eventDTO);
                }
            }
        }

        return eventDTOs;
    }

    @Override
    public List<EventDTO> getEventsByDate(Date from, Date to) {
        List<Event> events = eventRepository.findByDateFromAfterAndDateToBefore(from, to);
        if(events == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event event : events){
            if(!event.isDeleted())
                eventDTOs.add(event.createEventDTO());
        }

        return eventDTOs;
    }

    @Override
    public List<EventDTO> getEventsByName(String name) {
        List<Event> events = eventRepository.findByNameContains(name);
        if(events == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event event : events){
            if(!event.isDeleted())
                eventDTOs.add(event.createEventDTO());
        }

        return eventDTOs;
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.getById(id);
        if(event == null || event.isDeleted())
            return null;

        EventDTO eventDTO = event.createEventDTO();
        return eventDTO;
    }

    @Override
    public EventDTO createEvent(String creatorEmail, EventDTO eventDTO) {

        User creator = userRepository.findByEmail(creatorEmail);
        if(creator == null)
            return null;

        SportsField sp = spRepository.getOne(eventDTO.getSportsFieldId());
        if(sp == null)
            return null;

        Event newEvent = new Event();
        newEvent.setCreator(creator);
        newEvent.setSportsField(sp);
        newEvent.setName(eventDTO.getName());
        newEvent.setDateFrom(eventDTO.getDateFrom());
        newEvent.setDateTo(eventDTO.getDateTo());
        newEvent.setTimeFrom(eventDTO.getTimeFrom());
        newEvent.setTimeTo(eventDTO.getTimeTo());
        newEvent.setNumbOfPpl(eventDTO.getNumbOfPpl());
        newEvent.setPrice(eventDTO.getPrice());
        newEvent.setCurr(eventDTO.getCurr());
        newEvent.setDescription(eventDTO.getDescription());
        newEvent.setDeleted(false);

        eventRepository.save(newEvent);
        return eventDTO;
    }

    @Override
    public EventDTO editEvent(EventDTO eventDTO) {
        Event event = eventRepository.getById(eventDTO.getId());
        if(event == null || event.isDeleted())
            return null;

        event.setName(eventDTO.getName());
        event.setDateFrom(eventDTO.getDateFrom());
        event.setDateTo(eventDTO.getDateTo());
        event.setTimeFrom(eventDTO.getTimeFrom());
        event.setTimeTo(eventDTO.getTimeTo());
        event.setNumbOfPpl(eventDTO.getNumbOfPpl());
        event.setPrice(eventDTO.getPrice());
        event.setCurr(eventDTO.getCurr());
        event.setDescription(eventDTO.getDescription());

        eventRepository.save(event);
        return eventDTO;
    }

    @Override
    public EventDTO deleteEvent(Long id) {
        Event event = eventRepository.getById(id);
        if(event == null || event.isDeleted())
            return null;

        event.setDeleted(true);
        eventRepository.save(event);
        return event.createEventDTO();
    }

    @Override
    public List<UserDTO> getEventParticipants(Long id) {
        Event event = eventRepository.getById(id);
        if(event == null || event.isDeleted())
            return null;

        List<UserDTO> userDTOs = new ArrayList<>();
        for(Participation participation : event.getParticipationList()){
            if(!participation.isDeleted()){

                User user = participation.getUser();

                UserDTO userDTO = new UserDTO();
                userDTO.setIme(user.getFirstName());
                userDTO.setPrezime(user.getLastName());
                userDTO.setEmail(user.getEmail());
                userDTOs.add(userDTO);
            }
        }

        return userDTOs;
    }

    @Override
    public UserDTO getEventCreator(Long id) {
        Event event = eventRepository.getById(id);
        if(event == null || event.isDeleted())
            return null;

        User user = event.getCreator();
        UserDTO userDTO = new UserDTO();
        userDTO.setIme(user.getFirstName());
        userDTO.setPrezime(user.getLastName());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    @Override
    public EventRequestDTO getEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if(eventRequest == null || eventRequest.getStatus() != EventStatusEnum.PENDING)
            return null;

        EventRequestDTO eventRequestDTO = eventRequest.createEventRequestDTO();
        return eventRequestDTO;

    }

    @Override
    public List<EventRequestDTO> getAllEventRequestsByEvent(Long eventId) {
        Event event = eventRepository.getById(eventId);
        if(event == null || event.isDeleted())
            return null;

        List<EventRequestDTO> eventRequestDTOs = new ArrayList<>();
        for(EventRequest eventRequest : event.getEventRequests()){
            if(eventRequest.getStatus() == EventStatusEnum.PENDING)
                eventRequestDTOs.add(eventRequest.createEventRequestDTO());
        }

        return eventRequestDTOs;
    }

    @Override
    public List<EventRequestDTO> getAllEventRequestsByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if(user == null)
            return null;

        List<EventRequestDTO> eventRequestDTOs = new ArrayList<>();
        for(EventRequest eventRequest : user.getEventRequests()){
            if(eventRequest.getStatus() == EventStatusEnum.PENDING)
                eventRequestDTOs.add(eventRequest.createEventRequestDTO());
        }

        return eventRequestDTOs;
    }

    @Override
    public EventRequestDTO createEventRequest(EventRequestRequest request, EventRequestTypeEnum eventRequestType) {
        Event event = eventRepository.getById(request.getEventId());
        if(event == null || event.isDeleted())
            return null;

        User user = userRepository.findByEmail(request.getUserEmail());
        if(user == null)
            return null;

        EventRequest eventRequest = new EventRequest();
        eventRequest.setUser(user);
        eventRequest.setEvent(event);
        eventRequest.setEventRequestType(eventRequestType);
        eventRequest.setStatus(EventStatusEnum.PENDING);

        Long erId = erRepository.save(eventRequest).getId();

        if(eventRequestType == EventRequestTypeEnum.REQUESTED_BY_PARTICIPANT){
            Map<String,String> data = new HashMap<>();
            data.put("eventId",event.getId().toString());
            data.put("applierId",user.getId().toString());
            data.put("firstName",user.getFirstName());
            data.put("lastName",user.getLastName());
            data.put("email",user.getEmail());
            data.put("username",user.getUsername());
            data.put("status","QUEUE");
            data.put("requestId",erId.toString());
            data.put("notificationType","APPLY_FOR_EVENT");
            data.put("title","New application for your event");
            data.put("message",user.getFirstName() + " " + user.getLastName() + " applied for the event "+event.getName()+".");

            PushNotificationRequest notificationRequest = new PushNotificationRequest();
            notificationRequest.setMessage(user.getFirstName() + " " + user.getLastName() + " applied for the event "+event.getName()+".");
            notificationRequest.setTitle("New application for your event");
            notificationRequest.setTopic(event.getCreator().getId().toString());
            pushNotificationService.sendPushNotification(notificationRequest,data);
        }

        eventRequest.setId(erId);

        EventRequestDTO ret = eventRequest.createEventRequestDTO();

        return ret;
    }

    @Override
    public EventRequestDTO acceptEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if(eventRequest == null || eventRequest.getStatus()!=EventStatusEnum.PENDING)
            return null;

        User user = eventRequest.getUser();
        if(user == null)
            return null;

        Event event = eventRequest.getEvent();
        if(event == null || event.isDeleted())
            return null;

        Participation participation = new Participation();
        participation.setUser(user);
        participation.setEvent(event);
        participation.setDeleted(false);

        event.getParticipationList().add(participation); //dodamo usera u listu participanta eventa
        eventRequest.setStatus(EventStatusEnum.CONFIRMED);

        Long pId = participationRepository.save(participation).getId();
        eventRepository.save(event);
        erRepository.save(eventRequest);

        Map<String,String> data = new HashMap<>();
        data.put("eventId",event.getId().toString());
        data.put("applierId",user.getId().toString());
        data.put("firstName",user.getFirstName());
        data.put("lastName",user.getLastName());
        data.put("email",user.getEmail());
        data.put("username",user.getUsername());
        data.put("participationId",pId.toString());
        data.put("status","PARTICIPATING");
        data.put("eventStatus","PARTICIPANT");
        data.put("notificationType","ACCEPTED_APPLICATION");
        data.put("title","Accepted Application for the Event");
        data.put("message","Your application for the event "+event.getName()+" has been accepted.");

        PushNotificationRequest notificationRequest = new PushNotificationRequest();
        notificationRequest.setMessage("Your application for the event "+event.getName()+" has been accepted.");
        notificationRequest.setTitle("Accepted Application for the Event");
        notificationRequest.setTopic(user.getId().toString());
        pushNotificationService.sendPushNotification(notificationRequest,data);

        eventRequest.setId(pId);

        return eventRequest.createEventRequestDTO();
    }

    @Override
    public EventRequestDTO rejectEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if (eventRequest == null || eventRequest.getStatus()!=EventStatusEnum.PENDING)
            return null;

        eventRequest.setStatus(EventStatusEnum.REJECTED);
        erRepository.save(eventRequest);
        return eventRequest.createEventRequestDTO();
    }

    @Override
    public EventRequestDTO deleteEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if (eventRequest == null || eventRequest.getStatus()!=EventStatusEnum.PENDING)
            return null;

        eventRequest.setStatus(EventStatusEnum.DELETED);
        erRepository.save(eventRequest);
        return eventRequest.createEventRequestDTO();
    }

    @Override
    public ParticipationDTO deleteParticipation(Long id) {
        Participation participation = participationRepository.getOne(id);
        if (participation == null || participation.isDeleted())
            return null;

        participation.setDeleted(true);
        participationRepository.save(participation);

        ParticipationDTO participationDTO = new ParticipationDTO();
        participationDTO.setEventId(participation.getEvent().getId());
        participationDTO.setEventName(participation.getEvent().getName());
        participationDTO.setId(participation.getId());
        participationDTO.setUserEmail(participation.getUser().getEmail());
        participationDTO.setUserFirstName(participation.getUser().getFirstName());
        participationDTO.setUserLastName(participation.getUser().getLastName());

        return participationDTO;
    }
}
