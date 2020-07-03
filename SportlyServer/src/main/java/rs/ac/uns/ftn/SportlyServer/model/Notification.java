package rs.ac.uns.ftn.SportlyServer.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;

    private String type;

    private String title;

    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User user;
}
