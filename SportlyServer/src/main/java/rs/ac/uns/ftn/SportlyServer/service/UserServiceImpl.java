package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.PeopleDTO;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<PeopleDTO> searchPeople(String filterText){

        filterText = filterText.toLowerCase().trim();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username);

        List<User> peopleList = userRepository.findByFilterText(filterText, username);
        System.out.println("Filter text: "+filterText);
        System.out.println("Email: "+username);
        List<PeopleDTO> ret = new ArrayList<>();

        for(User u : peopleList){

            System.out.println(u.getEmail());

            PeopleDTO dto = new PeopleDTO();
            dto.setEmail(u.getEmail());
            dto.setFirstName(u.getFirstName());
            dto.setId(u.getId());
            dto.setLastName(u.getLastName());
            dto.setUsername(u.getUsername());

            if(user.containsFriend(u.getEmail())){
                dto.setFriend(true);
            }else{
                dto.setFriend(false);
            }

            ret.add(dto);
        }

        return ret;
    }
}
