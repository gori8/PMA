package rs.ac.uns.ftn.sportly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO implements Comparable<NotificationDTO> {

    private Long id;

    private String content;

    private Date date;

    @Override
    public int compareTo(NotificationDTO n) {
        return getDate().compareTo(n.getDate());
    }
}
