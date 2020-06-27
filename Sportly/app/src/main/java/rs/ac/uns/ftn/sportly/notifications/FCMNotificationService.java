package rs.ac.uns.ftn.sportly.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
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

import java.util.Map;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;

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

        Map<String,String> data = remoteMessage.getData();
        if(!data.isEmpty()){
            if(data.get("notificationType").equals("REQUEST")){
                ContentValues values = new ContentValues();
                values.put(DataBaseTables.FRIENDS_FIRST_NAME,data.get("firstName"));
                values.put(DataBaseTables.FRIENDS_LAST_NAME,data.get("lastName"));
                values.put(DataBaseTables.FRIENDS_EMAIL,data.get("email"));
                values.put(DataBaseTables.FRIENDS_USERNAME,data.get("username"));
                values.put(DataBaseTables.FRINEDS_TYPE,"PENDING");
                values.put(DataBaseTables.SERVER_ID,data.get("id"));

                getContentResolver().insert(
                        Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                        values);
            }else if(data.get("notificationType").equals("CONFIRMATION")){
                ContentValues values = new ContentValues();
                values.put(DataBaseTables.FRIENDS_FIRST_NAME,data.get("firstName"));
                values.put(DataBaseTables.FRIENDS_LAST_NAME,data.get("lastName"));
                values.put(DataBaseTables.FRIENDS_EMAIL,data.get("email"));
                values.put(DataBaseTables.FRIENDS_USERNAME,data.get("username"));
                values.put(DataBaseTables.FRINEDS_TYPE,"CONFIRMED");
                values.put(DataBaseTables.SERVER_ID,data.get("id"));

                getContentResolver().insert(
                        Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_FRIENDS),
                        values);
            }else if(data.get("notificationType").equals("APPLY_FOR_EVENT")){
                ContentValues valuesApplicationList = new ContentValues();
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID,data.get("eventId"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID,data.get("applierId"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_FIRST_NAME,data.get("firstName"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_LAST_NAME,data.get("lastName"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_USERNAME,data.get("username"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EMAIL,data.get("email"));
                valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_STATUS,data.get("status"));
                valuesApplicationList.put(DataBaseTables.SERVER_ID,"E"+data.get("eventId")+"A"+data.get("applierId")+"");

                getContentResolver().insert(
                        Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_APPLICATION_LIST),
                        valuesApplicationList);
            }

            Intent ints = new Intent(MainActivity.NOTIFICATION_INTENT);

            ints.putExtra("title",data.get("title"));
            ints.putExtra("body",data.get("message"));

            sendBroadcast(ints);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
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
