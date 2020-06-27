package rs.ac.uns.ftn.SportlyServer.model;

import javax.persistence.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rs.ac.uns.ftn.SportlyServer.dto.FriendDTO;


@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String password;

    private String firstName;

    private String lastName;

    @Column(nullable = false,unique = true)
    private String email;

    private Date lastPasswordResetDate;

    private Boolean enabled;

    @OneToMany(mappedBy = "friendshipRequester")
    private List<Friendship> requestedFriendships = new ArrayList<>();

    @OneToMany(mappedBy = "friendshipReceiver")
    private List<Friendship> receivedFriendships = new ArrayList<>();

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "favourite",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "sports_field_id"))
    private List<SportsField> favourite = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<Event> creatorEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FieldRating> fieldRatings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<EventRequest> eventRequests = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Participation> participationList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (Collection<? extends GrantedAuthority>) this.authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    public boolean containsFriend(final String email){
//        return friends.stream().filter(o -> o.getEmail().equals(email)).findFirst().isPresent();
//        return true;
//    }

    public FriendDTO createFriendDto(){
        FriendDTO friend = new FriendDTO();
        friend.setId(this.getId());
        friend.setUsername(this.getUsername());
        friend.setFirstName(this.getFirstName());
        friend.setLastName(this.getLastName());
        friend.setEmail(this.getEmail());
        return friend;
    }
}
