package rs.ac.uns.ftn.SportlyServer.service;



import org.springframework.security.core.userdetails.UserDetailsService;
import rs.ac.uns.ftn.SportlyServer.dto.FacebookRequest;
import rs.ac.uns.ftn.SportlyServer.dto.GoogleRequest;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.security.auth.JwtAuthenticationRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;


public interface LoginService extends UserDetailsService {

    User checkCredentials(JwtAuthenticationRequest request);
    UserDTO register(UserDTO userDTO);
    UserDTO login(JwtAuthenticationRequest request);
    void changePassword(String oldPassword, String newPassword, String username) throws Exception;
    UserDTO refreshAuthenticationToken(HttpServletRequest request);
    UserDTO loginGoogle(GoogleRequest googleRequest) throws GeneralSecurityException, IOException;
    UserDTO loginFacebook(FacebookRequest facebookRequest);

}
