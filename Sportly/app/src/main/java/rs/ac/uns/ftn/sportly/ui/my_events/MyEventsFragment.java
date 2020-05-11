package rs.ac.uns.ftn.sportly.ui.my_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.model.Event;
import rs.ac.uns.ftn.sportly.ui.adapters.EventsAdapter;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;

public class MyEventsFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_role_selection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.roles_array, R.layout.spinner_role_item);
        adapter.setDropDownViewResource(R.layout.spinner_role_dropdown_item);
        spinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Event> events = new ArrayList<>();

        Event event1 = new Event();
        event1.setLocation("Đačko igralište");
        event1.setName("FTN Friday Basketball");
        event1.setDescription("Basket, petak uveče. Dođite posle predavanja na koju partiju 3 na 3 basketa.");
        event1.setFrom("18:00");
        event1.setTo("20:00");
        event1.setSignedUpPlayers((short)2);
        event1.setTotalPlayers((short)6);
        event1.setSport("basketball");

        Event event2 = new Event();
        event1.setLocation("Đačko igralište");
        event2.setName("Mali fudbal za programere");
        event2.setDescription("Programeri koji zele posle posla da se druze uz mali fudbal su dobrodosli.");
        event2.setFrom("20:00");
        event2.setTo("21:30");
        event2.setSignedUpPlayers((short)1);
        event2.setTotalPlayers((short)10);
        event2.setSport("football");

        events.add(event1);
        events.add(event2);

        EventsAdapter adapter = new EventsAdapter(getContext(), events);

        ListView listView = (ListView) getView().findViewById(R.id.my_events_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, vieww, position, id) -> {

            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy");

            Event event = (Event) listView.getAdapter().getItem(position);

            MainActivity mainActivity = (MainActivity) getContext();

            Intent intent = new Intent(mainActivity, EventActivity.class);
            intent.putExtra("location", event.getLocation());
            intent.putExtra("name", event.getName());
            intent.putExtra("time", event.getFrom()+" - "+event.getTo());
            intent.putExtra("people", event.getSignedUpPlayers()+"/"+event.getTotalPlayers()+" people");
            intent.putExtra("date", formater.format(event.getDate()));
            intent.putExtra("price", "Free");
            intent.putExtra("creator", "Pera Perić");
            intent.putExtra("description", event.getDescription());
            intent.putExtra("imageView", R.drawable.djacko);
            intent.putExtra("isCreator",position%2==0?true:false);
            mainActivity.startActivity(intent);

        });

        Spinner spinner = view.findViewById(R.id.spinner_role_selection);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                TextView textView = view.findViewById(R.id.spinner_role_selection_item_text);
                adapter.getFilter().filter(textView.getText());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.VISIBLE);
    }
}
