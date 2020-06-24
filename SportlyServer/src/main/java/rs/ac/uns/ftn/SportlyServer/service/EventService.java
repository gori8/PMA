package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.EventDTO;
import rs.ac.uns.ftn.SportlyServer.model.Event;

import java.util.Date;
import java.util.List;

public interface EventService {
    public List<EventDTO> getCreatorEvents(String email);
    public List<EventDTO> getParticipantEvents(String email);
    public List<EventDTO> getEventsByDate(Date from, Date to);
    public List<EventDTO> getEventsByName(String name);
    public EventDTO getEventById(Long id);
    public EventDTO createEvent(String creatorEmail, Long sportFieldId, EventDTO eventDTO);
    public EventDTO editEvent(EventDTO eventDTO);
    public EventDTO deleteEvent(Long id);
}
