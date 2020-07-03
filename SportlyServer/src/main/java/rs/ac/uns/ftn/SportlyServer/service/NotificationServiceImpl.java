package rs.ac.uns.ftn.SportlyServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.model.Notification;
import rs.ac.uns.ftn.SportlyServer.model.User;
import rs.ac.uns.ftn.SportlyServer.repository.NotificationRepository;
import rs.ac.uns.ftn.SportlyServer.repository.UserRepository;

import java.util.Date;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void addNotification(PushNotificationRequest request, Map<String, String> data) {
        String type = data.get("notificationType");
        String title = request.getTitle();
        String message = request.getMessage();
        Date date = new Date();
        String userId = request.getTopic();

        Long id = Long.parseLong(userId);
        User user = userRepository.getOne(id);
        if(user == null)
            return;

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setDate(date);
        notification.setUser(user);
        Notification newNotification = notificationRepository.save(notification);

        user.getNotifications().add(newNotification);
        userRepository.save(user);
    }
}
