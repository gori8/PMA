package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.PeopleDTO;

import java.util.List;

public interface UserService {

    public List<PeopleDTO> searchPeople(String filterText);
}
