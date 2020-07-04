package rs.ac.uns.ftn.sportly.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.rating.RatingActivity;


public class FCMNotificationReceiver extends BroadcastReceiver {
	

	
	@Override
	public void onReceive(Context context, Intent intent) {

		if(intent.getAction().equals(MainActivity.NOTIFICATION_INTENT)){
			HashMap<String,String> data = new HashMap<>();
			for(String key : intent.getExtras().keySet()){
				if(key.equals("notificationType") || key.equals("message") || key.equals("title")){
					continue;
				}
				data.put(key,intent.getStringExtra(key));
			}
			sendNotification(context,intent.getStringExtra("title"),intent.getStringExtra("message"), intent.getStringExtra("notificationType"),data);
		}

	}

	private void sendNotification(Context context, String title, String messageBody, String notificationType, HashMap<String,String> data) {
		PendingIntent pendingIntent = null;
		if(notificationType.equals("RATING_REQUEST")) {
			Intent intent = new Intent(context, RatingActivity.class);
			Bundle bundleData = new Bundle();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				bundleData.putString(entry.getKey(), entry.getValue());
			}
			intent.putExtras(bundleData);
			pendingIntent = PendingIntent.getActivity(context, 0, intent,
					PendingIntent.FLAG_ONE_SHOT);
		}


		String channelId = context.getString(R.string.default_notification_channel_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.drawable.ic_basketball_event)
						.setContentTitle(title)
						.setContentText(messageBody)
						.setAutoCancel(true)
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify((int)System.currentTimeMillis(), notificationBuilder.build());
	}

}
