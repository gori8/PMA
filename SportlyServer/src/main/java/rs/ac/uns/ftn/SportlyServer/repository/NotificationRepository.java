package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
