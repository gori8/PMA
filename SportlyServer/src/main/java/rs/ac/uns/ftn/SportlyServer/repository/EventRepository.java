package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.dto.RatingSchedulerEnum;
import rs.ac.uns.ftn.SportlyServer.model.Event;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event getById(Long id);
    List<Event> findByDateFromAfterAndDateToBefore(Date dateFrom , Date dateTo);
    List<Event> findByNameContains(String name);
    List<Event> findAllByRatingSchedulerEnum(RatingSchedulerEnum ratingSchedulerEnum);
}
