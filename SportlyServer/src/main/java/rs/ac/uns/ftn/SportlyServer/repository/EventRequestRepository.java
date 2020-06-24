package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.EventRequest;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    EventRequest getById(Long id);
}
