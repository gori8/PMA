package rs.ac.uns.ftn.sportly.ui.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class NotificationCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public NotificationCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView tvTittle = view.findViewById(R.id.notification_name);
        TextView tvMessage = view.findViewById(R.id.notification_info);
        TextView tvDate = view.findViewById(R.id.notification_time);
        ImageView imageView = view.findViewById(R.id.notification_image);

        tvTittle.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.NOTIFICATIONS_TITTLE)));
        tvMessage.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.NOTIFICATIONS_MESSAGE)));
        tvDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.NOTIFICATIONS_DATE)));

        String type = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.NOTIFICATIONS_TYPE));

        switch (type) {
            case "REQUEST":{
                imageView.setImageResource(R.drawable.ic_notification_request);
            }break;

            case "CONFIRMATION":{
                imageView.setImageResource(R.drawable.ic_notification_confirmation);
            }break;

            case "APPLY_FOR_EVENT":{
                imageView.setImageResource(R.drawable.ic_notification_apply_for_event);
            }break;

            case "ACCEPTED_APPLICATION":{
                imageView.setImageResource(R.drawable.ic_notification_accepted_application);
            }break;

            case "INVITE_FRIEND":{
                imageView.setImageResource(R.drawable.ic_notification_invite_friend);
            }break;

            case "RATING_REQUEST":{
                imageView.setImageResource(R.drawable.ic_notification_rating_request);
            }break;

            case "EVENT_DELETED":{
                imageView.setImageResource(R.drawable.ic_notification_event_delete);
            }break;

            default:{
                System.out.println("NOTIFICATION TYPE IS NOT IMPLEMENTED");
            }
        }

    }
}
