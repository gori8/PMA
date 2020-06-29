package rs.ac.uns.ftn.sportly.ui.invite;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.ui.event.application_list.InviteCursorAdapter;
import rs.ac.uns.ftn.sportly.ui.event.application_list.InviteFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;


public class InviteRequestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private InviteRequestCursorAdapter adapter;

    public InviteRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_invite, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] {  };
        int[] to = new int[] {R.id.name};
        adapter = new InviteRequestCursorAdapter(getActivity(), R.layout.invite_request_item, null, from, to);
        ListView listView = (ListView) getActivity().findViewById(R.id.invite_list);
        listView.setAdapter(adapter);
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
                DataBaseTables.EVENTS_PRICE,
                DataBaseTables.EVENTS_SPORTS_FILED_ID,
                DataBaseTables.EVENTS_DATE_FROM,
                DataBaseTables.EVENTS_DATE_TO,
                DataBaseTables.EVENTS_TIME_FROM,
                DataBaseTables.EVENTS_TIME_TO,
                DataBaseTables.EVENTS_APPLICATION_STATUS,
                DataBaseTables.SERVER_ID,
                DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,
        };

        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+ DataBaseTables.TABLE_EVENTS);

        return new CursorLoader(getActivity(), uri,
                allColumns, DataBaseTables.EVENTS_APPLICATION_STATUS+" = 'INVITED'", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        TextView emptyListText = InviteRequestFragment.this.getActivity().findViewById(R.id.emptyListText);

        if((data == null) || (data.getCount() == 0)){
            emptyListText.setVisibility(View.VISIBLE);
        }else{
            emptyListText.setVisibility(View.GONE);
        }



        adapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}