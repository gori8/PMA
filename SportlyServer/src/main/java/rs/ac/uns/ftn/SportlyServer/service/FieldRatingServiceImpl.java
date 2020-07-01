package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.BundleRatingDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;
import rs.ac.uns.ftn.SportlyServer.dto.PersonToRateDTO;
import rs.ac.uns.ftn.SportlyServer.model.FieldRating;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.model.UserRating;
import rs.ac.uns.ftn.SportlyServer.repository.FieldRatingRepository;
import rs.ac.uns.ftn.SportlyServer.repository.SportsFieldRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRatingRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class FieldRatingServiceImpl implements FieldRatingService {
    @Autowired
    SportsFieldRepository sportsFieldRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FieldRatingRepository fieldRatingRepository;

    @Autowired
    UserRatingRepository userRatingRepository;

    @Override
    public FieldRatingDTO getRating(Long id) {
        FieldRating fieldRating = fieldRatingRepository.getById(id);
        if(fieldRating == null)
            return null;

        return fieldRating.createRatingDTO();
    }

    @Override
    public List<FieldRatingDTO> getAllRatings(Long fieldId) {
        SportsField sportsField = sportsFieldRepository.getById(fieldId);
        if(sportsField == null)
            return null;

        List<FieldRatingDTO> ratingDTOs = new ArrayList<>();
        for(FieldRating fieldRating : sportsField.getFieldRatings()){
            FieldRatingDTO ratingDTO = fieldRating.createRatingDTO();
            ratingDTOs.add(ratingDTO);
        }

        return ratingDTOs;
    }

    @Override
    public FieldRatingDTO createRating(FieldRatingDTO ratingDTO) {
        short val = ratingDTO.getValue();
        if(val < 0 || val > 5)
            return null;

        User creator = userRepository.findByEmail(ratingDTO.getCreatorEmail());
        SportsField sportsField = sportsFieldRepository.getById(ratingDTO.getFieldId());

        FieldRating fieldRating = new FieldRating();
        fieldRating.setValue(val);
        fieldRating.setComment(ratingDTO.getComment());
        fieldRating.setField(sportsField);
        fieldRating.setUserCreator(creator);

        FieldRating newFieldRating = fieldRatingRepository.save(fieldRating);
        ratingDTO.setId(newFieldRating.getId());
        ratingDTO.setCreatorFirstName(newFieldRating.getUserCreator().getFirstName());
        ratingDTO.setCreatorLastName(newFieldRating.getUserCreator().getLastName());
        UpdateAverageFieldRating(sportsField);
        return ratingDTO;
    }

    @Override
    public boolean checkIfFieldRatingExists(FieldRatingDTO ratingDTO) {
        User creator = userRepository.findByEmail(ratingDTO.getCreatorEmail());
        if(creator == null)
            return false;

        SportsField sportsField = sportsFieldRepository.getById(ratingDTO.getFieldId());
        if(sportsField == null)
            return false;

        for(FieldRating fieldRating : sportsField.getFieldRatings()){
            if(fieldRating.getUserCreator().getEmail().equals(creator.getEmail()))
                return false;
        }

        return true;
    }

    @Override
    public FieldRatingDTO editRating(FieldRatingDTO ratingDTO) {
        FieldRating fieldRating = fieldRatingRepository.getById(ratingDTO.getId());
        if(fieldRating == null)
            return null;

        short val = ratingDTO.getValue();
        if(val < 0 || val > 5)
            return null;

        fieldRating.setValue(val);
        fieldRating.setComment(ratingDTO.getComment());
        fieldRatingRepository.save(fieldRating);
        UpdateAverageFieldRating(fieldRating.getField());
        return ratingDTO;
    }

    @Override
    public FieldRatingDTO deleteRating(Long id) {
        FieldRating fieldRating = fieldRatingRepository.getById(id);
        if(fieldRating == null)
            return null;

        fieldRatingRepository.delete(fieldRating);
        UpdateAverageFieldRating(fieldRating.getField());
        return fieldRating.createRatingDTO();
    }

    public SportsField UpdateAverageFieldRating(SportsField sportsField){
        float avg = 0;
        OptionalDouble avgRating = sportsField.getFieldRatings().stream().mapToInt(o -> o.getValue()).average();
        if(avgRating.isPresent())
            avg = (float)avgRating.getAsDouble();

        sportsField.setRating(avg);
        sportsFieldRepository.save(sportsField);
        return sportsField;
    }

    @Override
    public Long createBundleRating(BundleRatingDTO bundle, String reqEmail){

        User creator = userRepository.findByEmail(reqEmail);
        if(creator == null)
            return null;

        SportsField sportsField = sportsFieldRepository.getById(bundle.getSportsFieldId());
        if(sportsField == null)
            return null;

        for (PersonToRateDTO person : bundle.getPeople()) {

            if(person.getRating() == 0.0){
                continue;
            }

            User user = userRepository.getOne(person.getId());

            if(user == null)
                return null;

            UserRating userRating = new UserRating();
            userRating.setUserCreator(creator);
            userRating.setUser(user);
            userRating.setComment(person.getComment());
            userRating.setValue((short) Math.round(person.getRating()));

            userRating = userRatingRepository.save(userRating);

            creator.getRatedUsers().add(userRating);
            userRepository.save(creator);

            user.getUserRatings().add(userRating);
            userRepository.save(user);
        }

        if(bundle.getSportsFieldRating() == 0.0){
            return -1L;
        }

        FieldRating fieldRating = new FieldRating();
        fieldRating.setComment(bundle.getSportsFieldComment());
        fieldRating.setValue((short) Math.round(bundle.getSportsFieldRating()));
        fieldRating.setField(sportsField);
        fieldRating.setUserCreator(creator);

        fieldRating = fieldRatingRepository.save(fieldRating);

        creator.getRatedFields().add(fieldRating);
        userRepository.save(creator);

        sportsField.getFieldRatings().add(fieldRating);
        sportsFieldRepository.save(sportsField);

        UpdateAverageFieldRating(sportsField);

        return fieldRating.getId();
    }
}
