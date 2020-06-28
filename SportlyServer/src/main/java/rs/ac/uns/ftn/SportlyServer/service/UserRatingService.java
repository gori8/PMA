package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;

import java.util.List;

public interface UserRatingService {
    public List<UserRatingDTO> getAllRatings(String userEmail);

    public UserRatingDTO createRating(UserRatingDTO ratingDTO);

    public UserRatingDTO editRating(UserRatingDTO ratingDTO);

    public UserRatingDTO deleteRating(Long id);
}

