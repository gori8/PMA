package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.BundleRatingDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;
import rs.ac.uns.ftn.SportlyServer.service.FieldRatingService;

import java.util.List;

@RestController
@RequestMapping(value = "/fieldRatings", produces = MediaType.APPLICATION_JSON_VALUE)
public class FieldRatingController {

    @Autowired
    FieldRatingService fieldRatingService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id) {
        FieldRatingDTO fieldRatingDTO = fieldRatingService.getRating(id);
        if(fieldRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(fieldRatingDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{fieldId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAll(@PathVariable Long fieldId) {
        List<FieldRatingDTO> fieldRatingDTOs = fieldRatingService.getAllRatings(fieldId);
        if(fieldRatingDTOs == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(fieldRatingDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody FieldRatingDTO frDTO) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        frDTO.setCreatorEmail(reqEmail);
        if(!fieldRatingService.checkIfFieldRatingExists(frDTO))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        FieldRatingDTO fieldRatingDTO = fieldRatingService.createRating(frDTO);
        if(fieldRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(fieldRatingDTO, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody FieldRatingDTO frDTO) {
        if(id != frDTO.getId())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        FieldRatingDTO fieldRatingDTO = fieldRatingService.editRating(frDTO);
        if(fieldRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(fieldRatingDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        FieldRatingDTO fieldRatingDTO = fieldRatingService.deleteRating(id);
        if(fieldRatingDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(fieldRatingDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/bundle", method = RequestMethod.POST)
    public ResponseEntity<?> createBundleRating(@RequestBody BundleRatingDTO dto) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Long id = fieldRatingService.createBundleRating(dto,reqEmail);
        if(id == null || id == 0L)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
}


