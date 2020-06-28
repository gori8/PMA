package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.UserRating;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    UserRating getById(Long id);
}
