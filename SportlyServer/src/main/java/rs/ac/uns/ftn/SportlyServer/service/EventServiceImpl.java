package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.EventDTO;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.EventRepository;
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
    public EventDTO createEvent(String creatorEmail, Long sportFieldId, EventDTO eventDTO) {

        User creator = userRepository.findByEmail(creatorEmail);
        if(creator == null)
            return null;

        SportsField sp = spRepository.getOne(sportFieldId);
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
}
