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

    private String message;

    private String type;

    private String title;

    private String date;

    @Override
    public int compareTo(NotificationDTO n) {
        return getDate().compareTo(n.getDate());
    }
}
