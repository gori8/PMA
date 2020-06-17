package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.SportsField;

@Repository
public interface SportsFieldRepository extends JpaRepository<SportsField, Long> {

}

