package rs.ac.uns.ftn.sportly.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.sync.SyncDataService;
import rs.ac.uns.ftn.sportly.utils.SportlyUtils;

public class NotificationReciever extends BroadcastReceiver {

    private static int notificationID = 1;
    private static String channelID = "My_Chan_Id";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID);


        if(intent.getAction().equals(MainActivity.NOTIFICATION)){

                Bitmap bm = null;


                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_basketball_event);

                mBuilder.setLargeIcon(bm);
                mBuilder.setSmallIcon(R.drawable.ic_basketball_event);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(notificationID, mBuilder.build());
                AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

                Intent alarmNotificationIntent = new Intent(context, NotificationService.class);
                PendingIntent pendingNotificationIntent = PendingIntent.getService(context, 889, alarmNotificationIntent, 0);

                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10*1000, pendingNotificationIntent);

        }
        }
    }
