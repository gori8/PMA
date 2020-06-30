package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.SportsFieldDTO;
import rs.ac.uns.ftn.SportlyServer.service.SportsFieldService;

@RestController
@RequestMapping(value = "/favouriteFields", produces = MediaType.APPLICATION_JSON_VALUE)
public class SportsFieldController {
    @Autowired
    SportsFieldService sportsFieldService;

    @RequestMapping(value = "/{fieldId}", method = RequestMethod.POST)
    public ResponseEntity<?> addToFavourites(@PathVariable Long fieldId) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        SportsFieldDTO sportsFieldDTO = sportsFieldService.addSportsFieldToFavourites(fieldId, reqEmail);
        if(sportsFieldDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(sportsFieldDTO, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{fieldId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeFromFavourites(@PathVariable Long fieldId) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        SportsFieldDTO sportsFieldDTO = sportsFieldService.removeSportsFieldFromFavourites(fieldId, reqEmail);
        if(sportsFieldDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(sportsFieldDTO, HttpStatus.OK);
    }
}
