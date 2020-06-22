package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.Friendship;
import rs.ac.uns.ftn.SportlyServer.model.User;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllByFriendshipReceiver(User user);
    List<Friendship> findAllByFriendshipRequester(User user);
    Friendship findByFriendshipRequesterAndFriendshipReceiver(User req, User rec);
    Friendship getById(Long id);
}
