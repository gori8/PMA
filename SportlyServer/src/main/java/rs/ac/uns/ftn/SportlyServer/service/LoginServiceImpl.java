package rs.ac.uns.ftn.SportlyServer.service;



import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.ftn.SportlyServer.dto.FacebookRequest;
import rs.ac.uns.ftn.SportlyServer.dto.FacebookResponse;
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

    @Autowired
    RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);


    @Override
    public User checkCredentials(JwtAuthenticationRequest request) {
        User user=userRepository.findByEmail(request.getUsername());
        if(user!=null){
            if(passwordEncoder.matches(request.getPassword(),user.getPassword())){
                return user;
            }
        }
        return null;
    }



    public UserDTO register(UserDTO userDTO) throws RollbackException {
        User user = new User();

        if(userRepository.findByEmail(userDTO.getEmail()) != null){
            userDTO.setId(null);
            return userDTO;
        }

        user.setLastName(userDTO.getPrezime());
        user.setEnabled(true);
        user.setFirstName(userDTO.getIme());
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
        User user=userRepository.findByEmail(request.getUsername());
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
        User user= userRepository.findByEmail(username);
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
        String username = this.tokenUtils.getEmailFromToken(token);
        User user = (User) userRepository.findByEmail(username);
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
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public UserDTO loginGoogle(GoogleRequest googleRequest) throws GeneralSecurityException, IOException, RollbackException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("985432247508-ku5dtbds3eul9j9mf3mdrhlr6fhborho.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();


        System.out.println("GOOGLE TOKEN: "+googleRequest.getIdToken());

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
            logger.info("Email: " + email);
            logger.info("Name: " + name);
            logger.info("Email verified: " + emailVerified);
            logger.info("Picture url: " + pictureUrl);
            logger.info("Locale: " + locale);
            logger.info("Familiy name: " + familyName);
            logger.info("Given name: " + givenName);

            String[] split = name.split(" ");
            String firstName = split[0];
            String lastName = split[1];

            UserDTO ret =new UserDTO();
            ret.setEmail(email);
            ret.setIme(firstName);
            ret.setPrezime(lastName);
            //ret.setPhotoUrl(pictureUrl);
            String jwt = tokenUtils.generateToken(email);
            int expiresIn = tokenUtils.getExpiredIn();
            ret.setExpiresIn(expiresIn);
            ret.setToken(jwt);

            User user = userRepository.findByEmail(ret.getEmail());

            if(user==null){
                user = new User();
                user.setFirstName(ret.getIme());
                user.setLastName(ret.getPrezime());
                user.setEmail(ret.getEmail());
                user.setPassword(null);
                user.setEnabled(true);
                user = userRepository.save(user);
            }else {
                logger.info("User already exists");
            }

            ret.setId(user.getId());



            return ret;

        } else {
            System.out.println("Invalid ID token.");
        }

        return null;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public UserDTO loginFacebook(FacebookRequest facebookRequest) {


        ResponseEntity<FacebookResponse> facebookResponseResponseEntity = restTemplate.getForEntity("https://graph.facebook.com/v7.0/"+facebookRequest.getUserId()+"?fields=id,name,email"+"&access_token="+facebookRequest.getToken(), FacebookResponse.class);
        FacebookResponse facebookResponse = facebookResponseResponseEntity.getBody();

        //ResponseEntity<Object> facebookPicture = restTemplate.getForEntity("https://graph.facebook.com/v7.0/"+facebookRequest.getUserId()+"?fields=picture"+"&access_token="+facebookRequest.getToken(), Object.class);

        logger.info("---------------------------------- FACEBOOK DATA ----------------------------------");
        logger.info("Email: " + facebookResponse.getEmail());
        logger.info("Name: " + facebookResponse.getName());
        logger.info("Id: " + facebookResponse.getId());
        //logger.info("Picture: " + facebookResponse.getUrl());

        String[] split = facebookResponse.getName().split(" ");
        String firstName = split[0];
        String lastName = split[1];

        UserDTO ret =new UserDTO();
        ret.setEmail(facebookResponse.getEmail());
        ret.setIme(firstName);
        ret.setPrezime(lastName);
        //ret.setPhotoUrl(facebookResponse.getUrl());
        String jwt = tokenUtils.generateToken(facebookResponse.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();
        ret.setExpiresIn(expiresIn);
        ret.setToken(jwt);

        User user = userRepository.findByEmail(ret.getEmail());

        if(user==null){
            user = new User();
            user.setFirstName(ret.getIme());
            user.setLastName(ret.getPrezime());
            user.setEmail(ret.getEmail());
            user.setPassword(null);
            user.setEnabled(true);
            user = userRepository.save(user);
        }else {
            logger.info("User already exists");
        }

        ret.setId(user.getId());

        return ret;
    }

}
