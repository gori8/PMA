package rs.ac.uns.ftn.SportlyServer.service;

import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;

import java.util.Map;

public interface NotificationService {
    void addNotification(PushNotificationRequest request, Map<String,String> data);
}
