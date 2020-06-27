package rs.ac.uns.ftn.SportlyServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.SportlyServer.model.Participation;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

}
