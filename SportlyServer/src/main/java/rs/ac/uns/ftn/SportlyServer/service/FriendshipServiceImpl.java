package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipTypeEnum;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.FriendshipRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<FriendDTO> getUserFriends(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;
        List<Friendship> requestedFriendships = friendshipRepository.findAllByFriendshipRequester(user);
        List<Friendship> receivedFriendships = friendshipRepository.findAllByFriendshipReceiver(user);
        List<FriendDTO> friends = new ArrayList<>();

        for(Friendship friendship : requestedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.CONFIRMED)
                friends.add(friendship.getFriendshipReceiver().createFriendDto());
        }

        for(Friendship friendship : receivedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.CONFIRMED)
                friends.add(friendship.getFriendshipRequester().createFriendDto());
        }

        return friends;
    }

    @Override
    public List<FriendDTO> getPendingFriends(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;
        List<Friendship> requestedFriendships = friendshipRepository.findAllByFriendshipRequester(user);
        List<FriendDTO> friends = new ArrayList<>();

        for(Friendship friendship : requestedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.PENDING)
                friends.add(friendship.getFriendshipReceiver().createFriendDto());
        }

        return friends;
    }

    @Override
    public List<FriendDTO> getFriendRequests(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;
        List<Friendship> receivedFriendships = friendshipRepository.findAllByFriendshipReceiver(user);
        List<FriendDTO> friends = new ArrayList<>();

        for(Friendship friendship : receivedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.PENDING)
                friends.add(friendship.getFriendshipRequester().createFriendDto());
        }

        return friends;
    }

    @Override
    public Friendship addPendingFriendship(String reqEmail, String recEmail) {
        //send friend request to somebody
        User req = userRepository.findByEmail(reqEmail);
        User rec = userRepository.findByEmail(recEmail);
        if(req == null || rec == null)
            return null;

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req, rec);
        if(friendship != null && !friendship.getFriendshipType().equals(FriendshipTypeEnum.DELETED)){
            //postoji prijateljstvo sa statusom pending ili confirmed, zato se zahtev ne moze poslati
            return null;
        }

        Friendship reverseFriendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(rec, req);
        if(reverseFriendship != null && !reverseFriendship.getFriendshipType().equals(FriendshipTypeEnum.DELETED)){
            //postoji obrnuto prijateljstvo sa statusom pending ili confirmed, zato se zahtev ne moze poslati
            return null;
        }

        Friendship newFriendship = new Friendship();
        newFriendship.setFriendshipRequester(req);
        newFriendship.setFriendshipReceiver(rec);
        newFriendship.setFriendshipType(FriendshipTypeEnum.PENDING);

        friendshipRepository.save(newFriendship);
        return newFriendship;
    }

    @Override
    public Friendship confirmFriendship(String reqEmail, String recEmail) {
        User req = userRepository.findByEmail(reqEmail);
        User rec = userRepository.findByEmail(recEmail);
        if(req == null || rec == null)
            return null;

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req, rec);
        if(friendship == null || !friendship.getFriendshipType().equals(FriendshipTypeEnum.PENDING)){
            //ne postoji prijateljstvo sa statusom PENDING, zato se zahtev ne moze poslati
            return null;
        }

        friendship.setFriendshipType(FriendshipTypeEnum.CONFIRMED);
        friendshipRepository.save(friendship);

        return friendship;
    }

    @Override
    public Friendship deleteFriendship(String reqEmail, String otherEmail) {
        User req = userRepository.findByEmail(reqEmail);
        User rec = userRepository.findByEmail(otherEmail);
        if(req == null || rec == null)
            return null;

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req, rec);
        if(friendship == null || friendship.getFriendshipType().equals(FriendshipTypeEnum.DELETED)){
            //ne postoji prijateljstvo sa statusom PENDING ili CONFIRMED, zato se zahtev ne moze poslati
            return null;
        }

        friendship.setFriendshipType(FriendshipTypeEnum.DELETED);
        friendshipRepository.save(friendship);

        return friendship;
    }

    @Override
    public Friendship getFriendshipById(Long id) {
        Friendship friendship = friendshipRepository.getById(id);
        return friendship;
    }

    @Override
    public boolean isFriend(String userEmail, String friendEmail) {
        List<FriendDTO> friends = getUserFriends(userEmail);
        for(FriendDTO friend : friends){
            if(friend.getEmail().equals(friendEmail)){
                return true;
            }
        }
        return false;
    }
}
