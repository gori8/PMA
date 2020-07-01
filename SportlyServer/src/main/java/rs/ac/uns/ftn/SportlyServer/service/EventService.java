package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.EventRequest;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface EventService {
    List<EventDTO> getCreatorEvents(String email);
    List<EventDTO> getParticipantEvents(String email);
    List<EventDTO> getEventsByDate(Date from, Date to); //no endpoint, if needed it will be added
    List<EventDTO> getEventsByName(String name); //no endpoint, if needed it will be added
    EventDTO getEventById(Long id);
    EventDTO createEvent(String creatorEmail, EventDTO eventDTO) throws ParseException;
    EventDTO editEvent(EventDTO eventDTO) throws ParseException;
    EventDTO deleteEvent(Long id);
    List<UserDTO> getEventParticipants(Long id);
    UserDTO getEventCreator(Long id);

    EventRequestDTO getEventRequest(Long id);
    List<EventRequestDTO> getAllEventRequestsByEvent(Long eventId);
    List<EventRequestDTO> getAllEventRequestsByEmail(String userEmail);
    EventRequestDTO createEventRequest(EventRequestRequest request, EventRequestTypeEnum eventRequestType);
    EventRequestDTO acceptEventRequest(Long id);
    EventRequestDTO rejectEventRequest(Long id);
    EventRequestDTO deleteEventRequest(Long id);
    ParticipationDTO deleteParticipation(Long id);

    List<Event> getAllEventsByRatingScheduler(RatingSchedulerEnum ratingSchedulerEnum);
}
