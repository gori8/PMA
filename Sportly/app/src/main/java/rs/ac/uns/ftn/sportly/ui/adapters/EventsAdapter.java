package rs.ac.uns.ftn.sportly.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.model.Event;


public class EventsAdapter extends ArrayAdapter<Event> implements Filterable {

    private Context context;
    private List<Event> events;
    private List<Event> filteredEvents;
    private RoleFilter roleFilter = new RoleFilter();

    public EventsAdapter(Context c, List<Event> events){
        super(c, R.layout.event_item,events);
        this.context = c;
        this.events = events;
        this.filteredEvents = events;
    }

    public int getCount() {
        return filteredEvents.size();
    }

    public Event getItem(int position) {
        return filteredEvents.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = layoutInflater.inflate(R.layout.event_item, parent, false);
        Event event = filteredEvents.get(position);

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

    public Filter getFilter() {
        return roleFilter;
    }

    private class RoleFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            boolean isCreator = false;

            if(filterString.equals("creator")){
                isCreator = true;
            }



            FilterResults results = new FilterResults();

            final List<Event> list = events;

            int count = list.size();
            final ArrayList<Event> nlist = new ArrayList<Event>(count);


            for (int i = 0; i < count; i++) {
                //check if creator, no data so random for now
                if(isCreator && i%2==0){
                    nlist.add(events.get(i));
                }
                if(!isCreator && i%2==1){
                    nlist.add(events.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredEvents = (ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }

    }
}

