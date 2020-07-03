package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.PeopleDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserWithRatingsDTO;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.model.UserRating;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipService friendshipService;

    @Override
    public List<PeopleDTO> searchPeople(String filterText){

        filterText = filterText.toLowerCase().trim();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String[] wordsList = filterText.split(" ");

        System.out.println("Filter text: "+filterText);
        System.out.println("Email: "+username);

        List<PeopleDTO> ret = new ArrayList<>();
        for (String word:wordsList) {

            if(word.trim().equals("")){
                continue;
            }

            List<User> peopleList = userRepository.findByFilterText(word, username);

            for(User u : peopleList){
                if(!containsUser(ret,u.getId())){
                    System.out.println(u.getEmail());

                    PeopleDTO dto = new PeopleDTO();
                    dto.setEmail(u.getEmail());
                    dto.setFirstName(u.getFirstName());
                    dto.setId(u.getId());
                    dto.setLastName(u.getLastName());
                    dto.setUsername(u.getUsername());

                    if(friendshipService.isFriend(username, u.getEmail())){
                        dto.setFriend(true);
                    }else{
                        dto.setFriend(false);
                    }

                    ret.add(dto);
                }
            }
        }

        return ret;
    }

    public boolean containsUser(final List<PeopleDTO> list, final Long id){
        return list.stream().filter(o -> o.getId().equals(id)).findFirst().isPresent();
    }

    @Override
    public UserWithRatingsDTO getUserWithRatings(Long id){

        User user = userRepository.getOne(id);

        if(user == null)
            return null;

        UserWithRatingsDTO ret = new UserWithRatingsDTO();

        ret.setId(user.getId());
        ret.setEmail(user.getEmail());
        ret.setFirstName(user.getFirstName());
        ret.setLastName(user.getLastName());
        ret.setImageSrc(user.getImageSrc());
        ret.setRatings(new ArrayList<>());

        for (UserRating rating : user.getUserRatings()) {
            UserRatingDTO ratingDTO = new UserRatingDTO();

            ratingDTO.setId(rating.getId());
            ratingDTO.setCreatorEmail(rating.getUserCreator().getEmail());
            ratingDTO.setCreatorFirstName(rating.getUserCreator().getFirstName());
            ratingDTO.setCreatorLastName(rating.getUserCreator().getLastName());
            ratingDTO.setCreatorId(rating.getUserCreator().getId());
            ratingDTO.setCreatorImageSrc(rating.getUserCreator().getImageSrc());
            ratingDTO.setComment(rating.getComment());
            ratingDTO.setValue(rating.getValue());
            ratingDTO.setUserEmail(rating.getUser().getEmail());

            ret.getRatings().add(ratingDTO);
        }

        return ret;
    }

    @Override
    public UserDTO editUser(UserDTO userDTO, String reqEmail) {
        if(userDTO == null)
            return null;

        User user = userRepository.findByEmail(reqEmail);
        if(user == null)
            return null;

        if(userDTO.getIme() == null || userDTO.getIme().equals(""))
            return null;

        if(userDTO.getPrezime() == null || userDTO.getPrezime().equals(""))
            return null;

        user.setFirstName(userDTO.getIme());
        user.setLastName(userDTO.getPrezime());

        userRepository.save(user);
        return userDTO;
    }
}
