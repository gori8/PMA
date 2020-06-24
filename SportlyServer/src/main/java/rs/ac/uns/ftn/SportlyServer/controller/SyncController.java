package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.dto.SyncDataDTO;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;
import rs.ac.uns.ftn.SportlyServer.service.PushNotificationService;
import rs.ac.uns.ftn.SportlyServer.service.SyncService;
import rs.ac.uns.ftn.SportlyServer.service.UserService;

@Controller
@RequestMapping("/sync")
@CrossOrigin("*")
public class SyncController {

    @Autowired
    SyncService syncService;

    @Autowired
    UserService userService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getSyncData() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(username);

        //testing notifications purpose
        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage("Your data just synced - "+username);
        request.setTitle("Sync");
        request.setTopic(user.getId().toString());
        pushNotificationService.sendPushNotificationWithoutData(request);


        return new ResponseEntity<>(syncService.getSyncData(username), HttpStatus.OK);
    }


    @RequestMapping(value = "/test/{title}/{body}", method = RequestMethod.POST)
    public ResponseEntity<?> testNotif(@PathVariable("title") String title, @PathVariable("body") String body) {
        //set to whoever you want to send notification to
        String username = "goriantolovic@gmail.com";

        User user = userRepository.findByEmail(username);

        //testing notifications purpose
        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage(body);
        request.setTitle(title);
        request.setTopic(user.getId().toString());
        pushNotificationService.sendPushNotificationWithoutData(request);


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
