package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;
import rs.ac.uns.ftn.SportlyServer.dto.FriendshipTypeEnum;
import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.FriendshipRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Override
    public List<FriendDTO> getUserFriends(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null)
            return null;
        List<Friendship> requestedFriendships = friendshipRepository.findAllByFriendshipRequester(user);
        List<Friendship> receivedFriendships = friendshipRepository.findAllByFriendshipReceiver(user);
        List<FriendDTO> friends = new ArrayList<>();

        for(Friendship friendship : requestedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.CONFIRMED){
                FriendDTO dto = friendship.getFriendshipReceiver().createFriendDto();
                dto.setFriendType("CONFIRMED");
                friends.add(dto);
            }
        }

        for(Friendship friendship : receivedFriendships){
            if(friendship.getFriendshipType() == FriendshipTypeEnum.CONFIRMED){
                FriendDTO dto = friendship.getFriendshipRequester().createFriendDto();
                dto.setFriendType("CONFIRMED");
                friends.add(dto);
            }
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
            if(friendship.getFriendshipType() == FriendshipTypeEnum.PENDING){
                FriendDTO dto = friendship.getFriendshipRequester().createFriendDto();
                dto.setFriendType("PENDING");
                friends.add(dto);
            }
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

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req.getId(), rec.getId());
        if(friendship != null && !friendship.getFriendshipType().equals(FriendshipTypeEnum.DELETED)){
            //postoji prijateljstvo sa statusom pending ili confirmed, zato se zahtev ne moze poslati
            return null;
        }

        Friendship reverseFriendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(rec.getId(), req.getId());
        if(reverseFriendship != null && !reverseFriendship.getFriendshipType().equals(FriendshipTypeEnum.DELETED)){
            //postoji obrnuto prijateljstvo sa statusom pending ili confirmed, zato se zahtev ne moze poslati
            return null;
        }

        Friendship newFriendship = new Friendship();
        newFriendship.setFriendshipRequester(req);
        newFriendship.setFriendshipReceiver(rec);
        newFriendship.setFriendshipType(FriendshipTypeEnum.PENDING);

        friendshipRepository.save(newFriendship);

        Map<String,String> data = new HashMap<>();
        data.put("id",req.getId().toString());
        data.put("firstName",req.getFirstName());
        data.put("lastName",req.getLastName());
        data.put("email",req.getEmail());
        data.put("username",req.getUsername());
        data.put("notificationType","REQUEST");
        data.put("title","Friend Request");
        data.put("message",req.getFirstName() + " " + req.getLastName() + " wants to be friends with you on Sportly.");

        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage(req.getFirstName() + " " + req.getLastName() + " wants to be friends with you on Sportly.");
        request.setTitle("Friend Request");
        request.setTopic(rec.getId().toString());
        pushNotificationService.sendPushNotification(request,data);

        return newFriendship;
    }

    @Override
    public Friendship confirmFriendship(String reqEmail, String recEmail) {
        User req = userRepository.findByEmail(reqEmail);
        User rec = userRepository.findByEmail(recEmail);
        if(req == null || rec == null)
            return null;

        System.out.println("Emails are correct");

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req.getId(), rec.getId());

        System.out.println("REQ "+req.getId());
        System.out.println("REC "+rec.getId());

        if(friendship == null){
            System.out.println("Friendship is NULL");
            return null;
        }

        if(!friendship.getFriendshipType().equals(FriendshipTypeEnum.PENDING)){
            //ne postoji prijateljstvo sa statusom PENDING, zato se zahtev ne moze poslati
            System.out.println("Friendship is not in pending state");
            return null;
        }

        friendship.setFriendshipType(FriendshipTypeEnum.CONFIRMED);
        friendshipRepository.save(friendship);

        Map<String,String> data = new HashMap<>();
        data.put("id",rec.getId().toString());
        data.put("firstName",rec.getFirstName());
        data.put("lastName",rec.getLastName());
        data.put("email",rec.getEmail());
        data.put("username",rec.getUsername());
        data.put("notificationType","CONFIRMATION");
        data.put("title","Friendship Confirmed");
        data.put("message",rec.getFirstName() + " " + rec.getLastName() + " has confirmed that you're friends on Sportly.");

        PushNotificationRequest request = new PushNotificationRequest();
        request.setMessage(rec.getFirstName() + " " + rec.getLastName() + " has confirmed that you're friends on Sportly.");
        request.setTitle("Friendship Confirmed");
        request.setTopic(req.getId().toString());
        pushNotificationService.sendPushNotification(request,data);

        return friendship;
    }

    @Override
    public Friendship deleteFriendship(String reqEmail, String otherEmail) {
        User req = userRepository.findByEmail(reqEmail);
        User rec = userRepository.findByEmail(otherEmail);
        if(req == null || rec == null)
            return null;

        Friendship friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(req.getId(), rec.getId());

        if(friendship == null){
            friendship = friendshipRepository.findByFriendshipRequesterAndFriendshipReceiver(rec.getId(),req.getId());
        }

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
        friends.addAll(getFriendRequests(userEmail));
        friends.addAll(getPendingFriends(userEmail));
        for(FriendDTO friend : friends){
            if(friend.getEmail().equals(friendEmail)){
                return true;
            }
        }
        return false;
    }
}
