package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.*;
import rs.ac.uns.ftn.SportlyServer.service.EventService;

import java.util.List;

@RestController
@RequestMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    @Autowired
    EventService eventService;

    @RequestMapping(value = "/{id}/get", method = RequestMethod.GET)
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        EventDTO eventDTO = eventService.getEventById(id);
        if(eventDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createEvent(@RequestBody EventDTO request) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        EventDTO eventDTO = eventService.createEvent(reqEmail, request);
        if(eventDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTO, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public ResponseEntity<?> editEvent(@RequestBody EventDTO request) {
        EventDTO eventDTO = eventService.editEvent(request);
        if(eventDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        EventDTO eventDTO = eventService.deleteEvent(id);
        if(eventDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/creatorEvents/{email}", method = RequestMethod.GET)
    public ResponseEntity<?> getCreatorEvents(@PathVariable String email) {
        List<EventDTO> eventDTOs = eventService.getCreatorEvents(email);
        if(eventDTOs == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/participantsEvents/{email}", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantEvents(@PathVariable String email) {
        List<EventDTO> eventDTOs = eventService.getParticipantEvents(email);
        if(eventDTOs == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/{eventId}/get/creator", method = RequestMethod.GET)
    public ResponseEntity<?> getEventCreator(@PathVariable Long eventId) {
        UserDTO userDTO = eventService.getEventCreator(eventId);
        if(userDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "{eventId}/get/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getEventParticipants(@PathVariable Long eventId) {
        List<UserDTO> userDTOs = eventService.getEventParticipants(eventId);
        if(userDTOs == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/{id}/get", method = RequestMethod.GET)
    public ResponseEntity<?> getEventRequest(@PathVariable Long id) {
        EventRequestDTO eventRequestDTO = eventService.getEventRequest(id);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequests/{eventId}/get", method = RequestMethod.GET)
    public ResponseEntity<?> getEventRequestsByEventId(@PathVariable Long eventId) {
        List<EventRequestDTO> eventRequestDTOs = eventService.getAllEventRequestsByEvent(eventId);
        if(eventRequestDTOs == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequests/{userEmail}/get", method = RequestMethod.GET)
    public ResponseEntity<?> getEventRequestsByUserEmail(@PathVariable String userEmail) {
        List<EventRequestDTO> eventRequestDTOs = eventService.getAllEventRequestsByEmail(userEmail);
        if(eventRequestDTOs == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/create/creator", method = RequestMethod.POST)
    public ResponseEntity<?> createEventRequestByCreator(@RequestBody EventRequestRequest request) {
        EventRequestDTO eventRequestDTO = eventService.createEventRequest(request, EventRequestTypeEnum.REQUESTED_BY_CREATOR);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/create/participant", method = RequestMethod.POST)
    public ResponseEntity<?> createEventRequestByParticipant(@RequestBody EventRequestRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        request.setUserEmail(email);

        EventRequestDTO eventRequestDTO = eventService.createEventRequest(request, EventRequestTypeEnum.REQUESTED_BY_PARTICIPANT);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/{id}/accept", method = RequestMethod.PUT)
    public ResponseEntity<?> acceptEventRequest(@PathVariable Long id) {
        EventRequestDTO eventRequestDTO = eventService.acceptEventRequest(id);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/{id}/reject", method = RequestMethod.PUT)
    public ResponseEntity<?> rejectEventRequest(@PathVariable Long id) {
        EventRequestDTO eventRequestDTO = eventService.rejectEventRequest(id);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/eventRequest/{id}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteEventRequest(@PathVariable Long id) {
        EventRequestDTO eventRequestDTO = eventService.deleteEventRequest(id);
        if(eventRequestDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(eventRequestDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/participation/{id}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipation(@PathVariable Long id) {
        ParticipationDTO participationDTO = eventService.deleteParticipation(id);
        if(participationDTO == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(participationDTO, HttpStatus.OK);
    }
}
