package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.FieldRatingDTO;

import java.util.List;

public interface FieldRatingService {
    public List<FieldRatingDTO> getAllRatings(Long fieldId);

    public FieldRatingDTO createRating(FieldRatingDTO ratingDTO);

    public FieldRatingDTO editRating(FieldRatingDTO ratingDTO);

    public FieldRatingDTO deleteRating(Long id);
}
