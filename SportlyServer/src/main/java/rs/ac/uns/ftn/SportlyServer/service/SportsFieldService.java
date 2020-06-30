package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.SportsFieldDTO;

public interface SportsFieldService {
    SportsFieldDTO addSportsFieldToFavourites(Long id, String userEmail);

    SportsFieldDTO removeSportsFieldFromFavourites(Long id, String userEmail);
}
