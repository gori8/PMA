package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.PeopleDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserDTO;
import rs.ac.uns.ftn.SportlyServer.dto.UserWithRatingsDTO;

import java.util.List;

public interface UserService {

    public List<PeopleDTO> searchPeople(String filterText);
    public UserWithRatingsDTO getUserWithRatings(Long id);
    UserDTO editUser(UserDTO userDTO, String reqEmail);
}
