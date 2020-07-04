package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.model.Notification;

import java.util.Map;

public interface NotificationService {
    Notification addNotification(PushNotificationRequest request, Map<String,String> data);
}
