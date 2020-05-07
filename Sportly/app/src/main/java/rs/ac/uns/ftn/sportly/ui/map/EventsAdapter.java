package rs.ac.uns.ftn.sportly.ui.map;

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
import rs.ac.uns.ftn.sportly.model.Event;


public class EventsAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> events;

    EventsAdapter(Context c, List<Event> events){
        super(c, R.layout.event_item,events);
        this.context = c;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.event_item, parent, false);
        Event event = events.get(position);

        ImageView eventImage = row.findViewById(R.id.list_event_image);
        TextView eventNameText = row.findViewById(R.id.list_event_name);
        TextView timeText = row.findViewById(R.id.list_event_time);
        TextView participantsText = row.findViewById(R.id.list_event_participants);
        TextView descriptionText = row.findViewById(R.id.list_event_description);


        if(event.getSport().equals("basketball")) {
            eventImage.setImageResource(R.drawable.ic_basketball_event);
        }
        if(event.getSport().equals("football")){
            eventImage.setImageResource(R.drawable.ic_football_event);
        }
        eventNameText.setText(event.getName());
        timeText.setText(event.getFrom()+" - "+event.getTo());
        participantsText.setText(event.getSignedUpPlayers()+"/"+event.getTotalPlayers());
        descriptionText.setText(event.getDescription());

        return row;
    }
}

