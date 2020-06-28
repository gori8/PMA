package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.model.UserRating;
import rs.ac.uns.ftn.SportlyServer.repository.UserRatingRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRatingRepository userRatingRepository;

    @Override
    public UserRatingDTO getRating(Long id) {
        UserRating userRating = userRatingRepository.getById(id);
        if(userRating == null)
            return null;

        return userRating.createRatingDTO();
    }

    @Override
    public List<UserRatingDTO> getAllRatings(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if(user == null)
            return null;

        List<UserRatingDTO> ratingDTOs = new ArrayList<>();
        for(UserRating userRating : user.getUserRatings()){
            UserRatingDTO ratingDTO = userRating.createRatingDTO();
            ratingDTOs.add(ratingDTO);
        }

        return ratingDTOs;
    }

    @Override
    public UserRatingDTO createRating(UserRatingDTO ratingDTO) {
        short val = ratingDTO.getValue();
        if(val < 0 || val > 5)
            return null;

        User creator = userRepository.findByEmail(ratingDTO.getCreatorEmail());
        User user = userRepository.findByEmail(ratingDTO.getUserEmail());

        UserRating userRating = new UserRating();
        userRating.setValue(val);
        userRating.setComment(ratingDTO.getComment());
        userRating.setUser(user);
        userRating.setUserCreator(creator);

        UserRating newUserRating = userRatingRepository.save(userRating);
        ratingDTO.setId(newUserRating.getId());
        ratingDTO.setCreatorFirstName(newUserRating.getUser().getFirstName());
        ratingDTO.setCreatorLastName(newUserRating.getUser().getLastName());
        UpdateAverageUserRating(user);
        return ratingDTO;
    }

    @Override
    public boolean checkIfUserRatingExists(UserRatingDTO ratingDTO) {
        User creator = userRepository.findByEmail(ratingDTO.getCreatorEmail());
        if(creator == null)
            return false;

        User user = userRepository.findByEmail(ratingDTO.getUserEmail());
        if(user == null)
            return false;

        for(UserRating userRating : user.getUserRatings()){
            if(userRating.getUserCreator().getEmail().equals(creator.getEmail()))
                return false;
        }

        return true;
    }

    @Override
    public UserRatingDTO editRating(UserRatingDTO ratingDTO) {
        UserRating userRating = userRatingRepository.getById(ratingDTO.getId());
        if(userRating == null)
            return null;

        short val = ratingDTO.getValue();
        if(val < 0 || val > 5)
            return null;

        userRating.setValue(val);
        userRating.setComment(ratingDTO.getComment());
        userRatingRepository.save(userRating);
        UpdateAverageUserRating(userRating.getUser());
        return ratingDTO;
    }

    @Override
    public UserRatingDTO deleteRating(Long id) {
        UserRating userRating = userRatingRepository.getById(id);
        if(userRating == null)
            return null;

        userRatingRepository.delete(userRating);
        UpdateAverageUserRating(userRating.getUser());
        return userRating.createRatingDTO();
    }

    public User UpdateAverageUserRating(User user){
        float avg = 0;
        OptionalDouble avgRating = user.getUserRatings().stream().mapToInt(o -> o.getValue()).average();
        if(avgRating.isPresent())
            avg = (float)avgRating.getAsDouble();

        user.setRating(avg);
        userRepository.save(user);
        return user;
    }
}
