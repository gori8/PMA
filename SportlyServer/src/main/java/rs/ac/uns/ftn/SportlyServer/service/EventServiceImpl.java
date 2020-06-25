package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.EventRequest;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.EventRepository;
import rs.ac.uns.ftn.SportlyServer.repository.EventRequestRepository;
import rs.ac.uns.ftn.SportlyServer.repository.SportsFieldRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<EventDTO> getCreatorEvents(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event event : user.getCreatorEvents()){
            if(!event.isDeleted())
                eventDTOs.add(event.createEventDTO());
        }

        return eventDTOs;
    }

    @Override
    public List<EventDTO> getParticipantEvents(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;

        List<EventDTO> eventDTOs = new ArrayList<>();
        for(Event event : user.getParticipantEvents()){
            if(!event.isDeleted())
                eventDTOs.add(event.createEventDTO());
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
        for(User user : event.getParticipants()){
            UserDTO userDTO = new UserDTO();
            userDTO.setIme(user.getFirstName());
            userDTO.setPrezime(user.getLastName());
            userDTO.setEmail(user.getEmail());
            userDTOs.add(userDTO);
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
        if(eventRequest == null || eventRequest.isDeleted())
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
            if(!eventRequest.isDeleted())
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
            if(!eventRequest.isDeleted())
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
        eventRequest.setDeleted(false);

        erRepository.save(eventRequest);
        return eventRequest.createEventRequestDTO();
    }

    @Override
    public EventRequestDTO acceptEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if(eventRequest == null || eventRequest.isDeleted())
            return null;

        User user = eventRequest.getUser();
        if(user == null)
            return null;

        Event event = eventRequest.getEvent();
        if(event == null || event.isDeleted())
            return null;

        event.getParticipants().add(user); //dodamo usera u listu participanta eventa
        eventRequest.setDeleted(true); //obrisemo request
        eventRepository.save(event);
        erRepository.save(eventRequest);

        return eventRequest.createEventRequestDTO();
    }

    @Override
    public EventRequestDTO rejectEventRequest(Long id) {
        EventRequest eventRequest = erRepository.getById(id);
        if (eventRequest == null || eventRequest.isDeleted())
            return null;

        eventRequest.setDeleted(true); //obrisemo request
        erRepository.save(eventRequest);
        return eventRequest.createEventRequestDTO();
    }
}
