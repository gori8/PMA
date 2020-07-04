package rs.ac.uns.ftn.sportly.notifications;

import android.app.ActivityManager;
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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.rating.RatingActivity;


public class FCMNotificationReceiver extends BroadcastReceiver {

	private Context context;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if(intent.getAction().equals(MainActivity.NOTIFICATION_INTENT)) {
			HashMap<String, String> data = new HashMap<>();
			for (String key : intent.getExtras().keySet()) {
				if (key.equals("notificationType") || key.equals("message") || key.equals("title")) {
					continue;
				}
				data.put(key, intent.getStringExtra(key));
			}

			SharedPreferences sharedPreferences =
					PreferenceManager.getDefaultSharedPreferences(context);
			boolean notifEnabled = sharedPreferences.getBoolean("notifications_enable_settings", true);

			if (notifEnabled) {

				sendNotification(context, intent.getStringExtra("title"), intent.getStringExtra("message"), intent.getStringExtra("notificationType"), data);

			}

		}

	}

	private void sendNotification(Context context, String title, String messageBody, String notificationType, HashMap<String,String> data) {
		boolean chatNotif=false;
		PendingIntent pendingIntent = null;
		if(notificationType.equals("RATING_REQUEST")) {
			Intent intent = new Intent(context, RatingActivity.class);
			Bundle bundleData = new Bundle();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				bundleData.putString(entry.getKey(), entry.getValue());
			}
			intent.putExtras(bundleData);
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
		}else if(notificationType.equals("REQUEST")){
			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","FriendsFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
		}else if (notificationType.equals("CONFIRMATION")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","FriendsFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
		}else if (notificationType.equals("APPLY_FOR_EVENT")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","MyEventsFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);

		}else if (notificationType.equals("ACCEPTED_APPLICATION")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","MyEventsFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);

		}else if (notificationType.equals("CHAT")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","ChatFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
			chatNotif=true;
		}else if (notificationType.equals("EVENT_DELETED")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","NotificationsFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
		}else if (notificationType.equals("INVITE_FRIEND")){

			Intent intent = new Intent(context,MainActivity.class);
			intent.putExtra("goto_fragment","InviteFragment");
			pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent,
					PendingIntent.FLAG_ONE_SHOT);
		}


		String channelId = context.getString(R.string.default_notification_channel_id);

		SharedPreferences sharedPreferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		boolean soundEnabled = sharedPreferences.getBoolean("notifications_sounds_enable_settings", true);


		Uri defaultSoundUri = null;

		if(soundEnabled){
			defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		}

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(context, channelId)
						.setSmallIcon(R.mipmap.ic_launcher)
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
			if(!soundEnabled) {
				channel.setSound(null, null);
			}

			notificationManager.createNotificationChannel(channel);
		}

		if(!(chatNotif && isChatActivityRunning())) {
			notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
		}
	}

	public boolean isChatActivityRunning() {

		try {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> activitys = activityManager.getRunningTasks(Integer.MAX_VALUE);
			boolean isChatActive = false;
			for (int i = 0; i < activitys.size(); i++) {
				Log.i("ACTIVE_ACTIVITY", "ACTIVE ACTIVITY:" + activitys.get(i).topActivity.toString());
				if (activitys.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{rs.ac.uns.ftn.sportly/rs.ac.uns.ftn.sportly.ui.messages.chat.ChatActivity")) {
					isChatActive = true;
				}
			}
			return isChatActive;
		}catch (Exception e){
			return false;
		}
	}

}
