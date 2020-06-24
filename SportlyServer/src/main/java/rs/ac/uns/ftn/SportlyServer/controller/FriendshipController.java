package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.service.FriendshipService;
import rs.ac.uns.ftn.SportlyServer.service.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/friendship", produces = MediaType.APPLICATION_JSON_VALUE)
public class FriendshipController {
    @Autowired
    FriendshipService friendshipService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getFriend(@PathVariable Long id) {
        Friendship friendship = friendshipService.getFriendshipById(id);
        if(friendship == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FriendshipDTO friendshipDTO = friendship.createFriendshipDTO();
        return new ResponseEntity<>(friendshipDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> getFriends() {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FriendDTO> friends = friendshipService.getUserFriends(reqEmail);
        if(friends == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @RequestMapping(value = "/sentRequests", method = RequestMethod.GET)
    public ResponseEntity<?> getPendingFriends() {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FriendDTO> friends = friendshipService.getPendingFriends(reqEmail);
        if(friends == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @RequestMapping(value = "/receivedRequests", method = RequestMethod.GET)
    public ResponseEntity<?> getFriendRequests() {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FriendDTO> friends = friendshipService.getFriendRequests(reqEmail);
        if(friends == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @RequestMapping(value = "/sendRequest", method = RequestMethod.POST)
    public ResponseEntity<?> sendFriendRequest(@RequestBody FriendshipRequestDto request) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Friendship friendship = friendshipService.addPendingFriendship(reqEmail, request.getRecEmail());
        if(friendship == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FriendshipDTO friendshipDTO = friendship.createFriendshipDTO();
        return new ResponseEntity<>(friendshipDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/confirmRequest", method = RequestMethod.PUT)
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendshipRequestDto request) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Friendship friendship = friendshipService.confirmFriendship(request.getRecEmail(),reqEmail);
        System.out.println("CONFIRM REQUEST ENDPOINT");
        if(friendship == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FriendshipDTO friendshipDTO = friendship.createFriendshipDTO();
        return new ResponseEntity<>(friendshipDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteFriendship", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteFriend(@RequestBody FriendshipRequestDto request) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Friendship friendship = friendshipService.deleteFriendship(reqEmail, request.getRecEmail());
        if(friendship == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FriendshipDTO friendshipDTO = friendship.createFriendshipDTO();
        return new ResponseEntity<>(friendshipDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/people/{filterText}", method = RequestMethod.GET)
    public ResponseEntity<?> searchPeople(@PathVariable("filterText") String filterText) {

        return new ResponseEntity<>(userService.searchPeople(filterText), HttpStatus.OK);
    }
}
