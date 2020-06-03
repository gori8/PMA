package rs.ac.uns.ftn.SportlyServer.service;



import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.SportlyServer.dto.GoogleRequest;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.model.Authority;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.AuthorityRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;
import rs.ac.uns.ftn.SportlyServer.security.TokenUtils;
import rs.ac.uns.ftn.SportlyServer.security.auth.JwtAuthenticationRequest;
import rs.ac.uns.ftn.SportlyServer.utils.ObjectMapperUtils;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class LoginServiceImpl implements  LoginService{

    @Autowired
    TokenUtils tokenUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);


    @Override
    public User checkCredentials(JwtAuthenticationRequest request) {
        User user=userRepository.findOneByUsername(request.getUsername());
        if(user!=null){
            if(passwordEncoder.matches(request.getPassword(),user.getPassword())){
                return user;
            }
        }
        return null;
    }



    public UserDTO register(UserDTO userDTO) throws RollbackException {
        User user = new User();

        if(userRepository.findOneByUsername(userDTO.getUsername()) != null){
            userDTO.setId(null);
            return userDTO;
        }

        user.setLastName(userDTO.getPrezime());
        user.setEnabled(true);
        user.setFirstName(userDTO.getIme());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        Authority authority = authorityRepository.findOneByName("ROLE_USER");
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authority);
        user.setAuthorities(authorities);

        user = userRepository.save(user);
        userDTO.setId(user.getId());

        return userDTO;
    }

    @Override
    public UserDTO login(JwtAuthenticationRequest request) {
        User user=userRepository.findOneByUsername(request.getUsername());
        if(user!=null){
            if(passwordEncoder.matches(request.getPassword(),user.getPassword())){
                String jwt = tokenUtils.generateToken(request.getUsername());
                int expiresIn = tokenUtils.getExpiredIn();
                UserDTO userDTO= ObjectMapperUtils.map(user,UserDTO.class);
                userDTO.setExpiresIn(expiresIn);
                userDTO.setToken(jwt);
                // Vrati user-a sa tokenom kao odgovor na uspesnu autentifikaciju
                return userDTO;
            }
        }
        return null;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String username) throws Exception{
        User user= userRepository.findOneByUsername(username);
        if(passwordEncoder.matches(oldPassword,user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }else{
            throw new Exception();
        }
    }

    @Override
    public UserDTO refreshAuthenticationToken(HttpServletRequest request){
        String token = tokenUtils.getToken(request);
        String username = this.tokenUtils.getUsernameFromToken(token);
        User user = (User) userRepository.findOneByUsername(username);
        if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = tokenUtils.refreshToken(token);
            int expiresIn = tokenUtils.getExpiredIn();
            UserDTO userDTO= ObjectMapperUtils.map(user,UserDTO.class);
            userDTO.setExpiresIn(expiresIn);
            userDTO.setToken(refreshedToken);
            return userDTO;
        } else {
            return null;
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }


    @Override
    public UserDTO loginGoogle(GoogleRequest googleRequest) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("985432247508-ku5dtbds3eul9j9mf3mdrhlr6fhborho.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

// (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(googleRequest.getIdToken());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            logger.info("---------------------------------- GOOGLE DATA ----------------------------------");
            logger.info("Email: ",email);
            logger.info("Name: ",name);
            logger.info("Email verified: ",emailVerified);
            logger.info("Picture url: ",pictureUrl);
            logger.info("Locale: ",locale);
            logger.info("Familiy name: ",familyName);
            logger.info("Given name: ",givenName);

            UserDTO ret =new UserDTO();
            ret.setEmail(email);
            ret.setIme(name);
            String jwt = tokenUtils.generateToken(email);
            int expiresIn = tokenUtils.getExpiredIn();
            ret.setExpiresIn(expiresIn);
            ret.setToken(jwt);

            return ret;

        } else {
            System.out.println("Invalid ID token.");
        }

        return null;
    }

}
