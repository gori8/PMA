package rs.ac.uns.ftn.SportlyServer.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.SportlyServer.dto.NotificationDTO;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Notification implements Comparable<Notification>  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private String type;

    private String title;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User user;

    @Override
    public int compareTo(Notification n) {
        return getDate().compareTo(n.getDate());
    }
}
