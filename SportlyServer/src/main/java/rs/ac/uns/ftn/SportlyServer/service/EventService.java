package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.EventRequest;

import java.util.Date;
import java.util.List;

public interface EventService {
    public List<EventDTO> getCreatorEvents(String email);
    public List<EventDTO> getParticipantEvents(String email);
    public List<EventDTO> getEventsByDate(Date from, Date to); //no endpoint, if needed it will be added
    public List<EventDTO> getEventsByName(String name); //no endpoint, if needed it will be added
    public EventDTO getEventById(Long id);
    public EventDTO createEvent(String creatorEmail, EventDTO eventDTO);
    public EventDTO editEvent(EventDTO eventDTO);
    public EventDTO deleteEvent(Long id);
    public List<UserDTO> getEventParticipants(Long id);
    public UserDTO getEventCreator(Long id);

    public EventRequestDTO getEventRequest(Long id);
    public List<EventRequestDTO> getAllEventRequestsByEvent(Long eventId);
    public List<EventRequestDTO> getAllEventRequestsByEmail(String userEmail);
    public EventRequestDTO createEventRequest(EventRequestRequest request, EventRequestTypeEnum eventRequestType);
    public EventRequestDTO acceptEventRequest(Long id);
    public EventRequestDTO rejectEventRequest(Long id);
    public EventRequestDTO deleteEventRequest(Long id);
}
