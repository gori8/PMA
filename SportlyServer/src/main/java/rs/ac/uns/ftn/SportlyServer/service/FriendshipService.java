package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.model.User;

import java.util.List;

public interface FriendshipService {
    public List<FriendDTO> getUserFriends(String email);

    public List<FriendDTO> getPendingFriends(String email);

    public List<FriendDTO> getFriendRequests(String email);

    public Friendship addPendingFriendship (String reqEmail, String recEmail);

    public Friendship confirmFriendship (String reqEmail, String recEmail);

    public Friendship deleteFriendship (String reqEmail, String recEmail);

    public Friendship getFriendshipById(Long id);

    public boolean isFriend(String userEmail, String friendEmail);
}
