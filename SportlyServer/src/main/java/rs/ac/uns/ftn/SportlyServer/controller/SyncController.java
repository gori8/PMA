package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.SyncDataDTO;
import rs.ac.uns.ftn.SportlyServer.service.SyncService;

@Controller
@RequestMapping("/sync")
@CrossOrigin("*")
public class SyncController {

    @Autowired
    SyncService syncService;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<?> getSyncData(@PathVariable("username") String username) {

        return new ResponseEntity<>(syncService.getSyncData(username), HttpStatus.OK);
    }
}
