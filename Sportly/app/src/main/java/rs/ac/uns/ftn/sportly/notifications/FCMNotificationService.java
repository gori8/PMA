package rs.ac.uns.ftn.sportly.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;

public class FCMNotificationService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    private FCMNotificationReceiver notifReceiver;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

        // Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

            Intent ints = new Intent(MainActivity.NOTIFICATION_INTENT);

            ints.putExtra("title",remoteMessage.getNotification().getTitle());
            ints.putExtra("body",remoteMessage.getNotification().getBody());

            sendBroadcast(ints);

        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        notifReceiver = new FCMNotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.NOTIFICATION_INTENT);

        registerReceiver(notifReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(notifReceiver != null){
            unregisterReceiver(notifReceiver);
        }
    }
}
