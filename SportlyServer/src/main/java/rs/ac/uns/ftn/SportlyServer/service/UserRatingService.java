package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.UserRatingDTO;

import java.util.List;

public interface UserRatingService {
    UserRatingDTO getRating(Long id);

    List<UserRatingDTO> getAllRatings(String userEmail);

    UserRatingDTO createRating(UserRatingDTO ratingDTO);

    boolean checkIfUserRatingExists(UserRatingDTO ratingDTO);

    UserRatingDTO editRating(UserRatingDTO ratingDTO);

    UserRatingDTO deleteRating(Long id);
}

