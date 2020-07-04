package rs.ac.uns.ftn.SportlyServer.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.SportlyServer.dto.PushNotificationRequest;
import rs.ac.uns.ftn.SportlyServer.firebase.FCMService;
import rs.ac.uns.ftn.SportlyServer.model.Notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {

    @Autowired
    NotificationService notificationService;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }


    public void sendPushNotification(PushNotificationRequest request, Map<String,String> data) {
        try {
            Notification notification = notificationService.addNotification(request, data);
            data.put("notificationId",notification.getId().toString());

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            String timeString = notification.getDate().format(timeFormatter);
            data.put("date",timeString);

            fcmService.sendMessage(data, request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendPushNotificationWithoutData(PushNotificationRequest request) {
        try {
            fcmService.sendMessageWithoutData(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }





}
