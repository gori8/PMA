package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.model.User;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllByFriendshipReceiver(User user);
    List<Friendship> findAllByFriendshipRequester(User user);

    @Query("SELECT f from Friendship f WHERE f.friendshipRequester.id = ?1 AND f.friendshipReceiver.id = ?2 AND f.friendshipType <> 'DELETED'")
    Friendship findByFriendshipRequesterAndFriendshipReceiver(Long reqId, Long recId);

    Friendship getById(Long id);
}
