package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.SportsFieldDTO;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.SportsFieldRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

@Service
public class SportsFieldServiceImpl implements SportsFieldService {
    @Autowired
    SportsFieldRepository sportsFieldRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public SportsFieldDTO addSportsFieldToFavourites(Long id, String userEmail) {
        SportsField sportsField = sportsFieldRepository.getById(id);
        if(sportsField == null)
            return null;

        User user = userRepository.findByEmail(userEmail);
        if(user == null)
            return null;

        user.getFavourite().add(sportsField);
        userRepository.save(user);

        sportsField.getUsers().add(user);
        sportsFieldRepository.save(sportsField);

        SportsFieldDTO sportsFieldDTO = sportsField.createSportsFieldDTO();
        sportsFieldDTO.setFavorite(true);
        return sportsFieldDTO;

    }

    @Override
    public SportsFieldDTO removeSportsFieldFromFavourites(Long id, String userEmail) {
        SportsField sportsField = sportsFieldRepository.getById(id);
        if(sportsField == null)
            return null;

        User user = userRepository.findByEmail(userEmail);
        if(user == null)
            return null;

        user.getFavourite().remove(sportsField);
        userRepository.save(user);

        sportsField.getUsers().remove(user);
        sportsFieldRepository.save(sportsField);

        SportsFieldDTO sportsFieldDTO = sportsField.createSportsFieldDTO();
        sportsFieldDTO.setFavorite(false);
        return sportsFieldDTO;
    }
}
