package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE (LOWER(u.firstName) LIKE CONCAT('%',?1,'%') OR LOWER(u.lastName) LIKE CONCAT('%',?1,'%')) AND u.email <> ?2")
    List<User> findByFilterText(String filterText, String email);

}
