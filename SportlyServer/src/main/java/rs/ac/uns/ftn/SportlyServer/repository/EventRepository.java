package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.Event;
import rs.ac.uns.ftn.SportlyServer.model.User;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event getById(Long id);
    List<Event> findByDateFromAfterAndDateToBefore(Date dateFrom , Date dateTo);
    List<Event> findByNameContains(String name);

    @Query("SELECT e.participants FROM Event e WHERE e.id = ?1")
    List<User> findParticipantListByEventId(Long eventId);
}
