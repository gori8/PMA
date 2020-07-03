package rs.ac.uns.ftn.SportlyServer.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserWithRatingsDTO;
import rs.ac.uns.ftn.SportlyServer.service.UserService;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWithRatings(@PathVariable Long id) {

        UserWithRatingsDTO ret = userService.getUserWithRatings(id);

        if(ret == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> editUser(@RequestBody UserDTO userDTO) {
        String reqEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO newUserDTO = userService.editUser(userDTO, reqEmail);
        if(newUserDTO == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(newUserDTO, HttpStatus.OK);
    }

}
