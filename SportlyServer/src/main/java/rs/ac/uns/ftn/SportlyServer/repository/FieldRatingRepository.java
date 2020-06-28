package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.FieldRating;

@Repository
public interface FieldRatingRepository extends JpaRepository<FieldRating, Long> {
    FieldRating getById(Long id);
}
