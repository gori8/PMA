package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.BundleRatingDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;

import java.util.List;

public interface FieldRatingService {
    FieldRatingDTO getRating(Long id);

    List<FieldRatingDTO> getAllRatings(Long fieldId);

    FieldRatingDTO createRating(FieldRatingDTO ratingDTO);

    boolean checkIfFieldRatingExists(FieldRatingDTO ratingDTO);

    FieldRatingDTO editRating(FieldRatingDTO ratingDTO);

    FieldRatingDTO deleteRating(Long id);

    Long createBundleRating(BundleRatingDTO bundle, String reqEmail);
}
