package rs.ac.uns.ftn.sportly.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import rs.ac.uns.ftn.sportly.R;

public class NotificationAdapter  extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;
    private List<String> info;
    private List<String> time;

    NotificationAdapter (Context c, List<String> n, List<Integer> i, List<String> in, List<String> t){
        super(c, R.layout.notification_item, R.id.notification_name, n);
        this.context = c;
        this.names = n;
        this.images = i;
        this.info = in;
        this.time = t;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.notification_item, parent, false);
        ImageView imageView = row.findViewById(R.id.notification_image);
        TextView textViewName = row.findViewById(R.id.notification_name);
        TextView textViewInfo = row.findViewById(R.id.notification_info);
        TextView textViewTime = row.findViewById(R.id.notification_time);

        imageView.setImageResource(images.get(position));
        textViewName.setText(names.get(position));
        textViewInfo.setText(info.get(position));
        textViewTime.setText(time.get(position));

        return row;
    }
}
