package rs.ac.uns.ftn.SportlyServer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class FieldRating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private short value;

    @ManyToOne(fetch = FetchType.EAGER)
    protected User user;

    @ManyToOne(fetch = FetchType.EAGER)
    protected SportsField sportsField;

}
