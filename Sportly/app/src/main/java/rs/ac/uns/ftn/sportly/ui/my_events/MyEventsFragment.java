package rs.ac.uns.ftn.sportly.ui.my_events;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.model.Event;
import rs.ac.uns.ftn.sportly.ui.adapters.EventsAdapter;
import rs.ac.uns.ftn.sportly.ui.adapters.EventsCursorAdapter;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;

public class MyEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private EventsCursorAdapter adapter;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        /*EventsAdapter adapter = new EventsAdapter(getContext(), events);

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

        });*/

        Spinner spinner = view.findViewById(R.id.spinner_role_selection);

        MyEventsFragment fragment = this;




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                TextView textView = view.findViewById(R.id.spinner_role_selection_item_text);
                Bundle args = new Bundle();
                if(textView.getText().equals("Creator")){
                    args.putString("type","creator");
                    System.out.println("CREATOR");
                }else{
                    args.putString("type","participating");
                    System.out.println("PARTICIPATING");
                }

                if(getLoaderManager().getLoader(0)!=null){
                    getLoaderManager().restartLoader(0, args, fragment);
                }else{
                    args = new Bundle();

                    args.putString("type","creator");


                    getLoaderManager().initLoader(0, args, fragment);
                }


                String[] from = new String[] {
                        DataBaseTables.EVENTS_NAME,
                        DataBaseTables.EVENTS_DESCRIPTION,
                        DataBaseTables.EVENTS_NUMB_OF_PPL,
                        DataBaseTables.EVENTS_TIME_FROM,
                        DataBaseTables.EVENTS_TIME_TO,
                        DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS
                };
                int[] to = new int[] {
                        R.id.list_event_name,
                        R.id.list_event_description,
                        R.id.list_event_ppl,
                        R.id.list_event_time_from,
                        R.id.list_event_time_to,
                        R.id.list_event_participants
                };
                adapter = new EventsCursorAdapter(getActivity(), R.layout.event_item, null, from,
                        to);
                ListView listView = (ListView) getView().findViewById(R.id.my_events_list);
                listView.setAdapter(adapter);




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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.EVENTS_NAME,
                DataBaseTables.EVENTS_CURR,
                DataBaseTables.EVENTS_DESCRIPTION,
                DataBaseTables.EVENTS_NUMB_OF_PPL,
                DataBaseTables.EVENTS_PARTICIPATING,
                DataBaseTables.EVENTS_PRICE,
                DataBaseTables.EVENTS_SPORTS_FILED_ID,
                DataBaseTables.EVENTS_DATE_FROM,
                DataBaseTables.EVENTS_DATE_TO,
                DataBaseTables.EVENTS_TIME_FROM,
                DataBaseTables.EVENTS_TIME_TO,
                DataBaseTables.EVENTS_CREATOR,
                DataBaseTables.SERVER_ID,
                DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS
        };

        Uri uri = SportlyContentProvider.CONTENT_URI;

        if(args.getString("type").equals("creator")){
            uri = Uri.parse(uri+DataBaseTables.TABLE_MY_EVENTS);
        }else if(args.getString("type").equals("participating")){
            uri = Uri.parse(uri+DataBaseTables.TABLE_PARTICIPATING_EVENTS);
        }

        System.out.println("LOADER CALLED");

        return new CursorLoader(getActivity(), uri, allColumns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
