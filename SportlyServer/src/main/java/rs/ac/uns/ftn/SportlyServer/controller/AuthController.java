package rs.ac.uns.ftn.SportlyServer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.SportlyServer.dto.FacebookRequest;
import rs.ac.uns.ftn.SportlyServer.dto.GoogleRequest;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.security.TokenUtils;
import rs.ac.uns.ftn.SportlyServer.security.auth.JwtAuthenticationRequest;
import rs.ac.uns.ftn.SportlyServer.service.LoginServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    LoginServiceImpl loginService;



    @RequestMapping(value = "/standard/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException, IOException {
        UserDTO user=loginService.login(authenticationRequest);
        if(user!=null){
            // Vrati user-a sa tokenom kao odgovor na uspesno autentifikaciju
            return ResponseEntity.ok(user);
        }else{
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/google/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenGoogle(@RequestBody GoogleRequest googleRequest) throws AuthenticationException, IOException, GeneralSecurityException {

        UserDTO user=loginService.loginGoogle(googleRequest);
        if(user!=null){
            // Vrati user-a sa tokenom kao odgovor na uspesno autentifikaciju
            return ResponseEntity.ok(user);
        }else{
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/facebook/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenFacebook(@RequestBody FacebookRequest facebookRequest) throws AuthenticationException, IOException, GeneralSecurityException {

        UserDTO user=loginService.loginFacebook(facebookRequest);
        if(user!=null){
            // Vrati user-a sa tokenom kao odgovor na uspesno autentifikaciju
            return ResponseEntity.ok(user);
        }else{
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public UserDTO register(@RequestBody UserDTO userDTO)
    {
        return loginService.register(userDTO);
    }


}