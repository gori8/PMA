package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;
import rs.ac.uns.ftn.SportlyServer.service.UserRatingService;

import java.util.List;

@RestController
@RequestMapping(value = "/userRatings", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRatingController {

    @Autowired
    UserRatingService userRatingService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id) {
        UserRatingDTO userRatingDTO = userRatingService.getRating(id);
        if(userRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(userRatingDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{email}", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@PathVariable String email) {
        List<UserRatingDTO> userRatingDTOs = userRatingService.getAllRatings(email);
        if(userRatingDTOs == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(userRatingDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody UserRatingDTO urDTO) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        urDTO.setCreatorEmail(reqEmail);
        if(!userRatingService.checkIfUserRatingExists(urDTO))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        UserRatingDTO userRatingDTO = userRatingService.createRating(urDTO);
        if(userRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(userRatingDTO, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody UserRatingDTO urDTO) {
        if(id != urDTO.getId())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserRatingDTO userRatingDTO = userRatingService.editRating(urDTO);
        if(userRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(userRatingDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        UserRatingDTO userRatingDTO = userRatingService.deleteRating(id);
        if(userRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(userRatingDTO, HttpStatus.OK);
    }

}
