package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;
import rs.ac.uns.ftn.SportlyServer.model.FieldRating;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.FieldRatingRepository;
import rs.ac.uns.ftn.SportlyServer.repository.SportsFieldRepository;
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
        User creator = userRepository.findByEmail(ratingDTO.getCreatorEmail());
        if(creator == null)
            return null;

        SportsField sportsField = sportsFieldRepository.getById(ratingDTO.getFieldId());
        if(sportsField == null)
            return null;

        short val = ratingDTO.getValue();
        if(val < 0 || val > 5)
            return null;

        FieldRating fieldRating = new FieldRating();
        fieldRating.setValue(val);
        fieldRating.setComment(ratingDTO.getComment());
        fieldRating.setSportsField(sportsField);
        fieldRating.setUserCreator(creator);

        FieldRating newFieldRating = fieldRatingRepository.save(fieldRating);
        ratingDTO.setId(newFieldRating.getId());
        UpdateAverageFieldRating(sportsField);
        return ratingDTO;
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
        UpdateAverageFieldRating(fieldRating.getSportsField());
        return ratingDTO;
    }

    @Override
    public FieldRatingDTO deleteRating(Long id) {
        FieldRating fieldRating = fieldRatingRepository.getById(id);
        if(fieldRating == null)
            return null;

        fieldRatingRepository.delete(fieldRating);
        UpdateAverageFieldRating(fieldRating.getSportsField());
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
}
