package rs.ac.uns.ftn.sportly.ui.event.application_list;

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

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;

public class QueueFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private QueueCursorAdapter adapter;
    private Long eventId;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] { DataBaseTables.APPLICATION_LIST_FIRST_NAME, DataBaseTables.APPLICATION_LIST_LAST_NAME };
        int[] to = new int[] {R.id.name};
        adapter = new QueueCursorAdapter(getActivity(), R.layout.queue_item, null, from, to);
        ListView listView = (ListView) getActivity().findViewById(R.id.queue_list);
        listView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = QueueFragment.this.getActivity().getIntent();
        eventId = intent.getLongExtra("eventId",-1);

        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+ DataBaseTables.TABLE_APPLICATION_LIST);
        String selection = DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID+" = " + eventId
                + " AND " + DataBaseTables.APPLICATION_LIST_STATUS+" = 'QUEUE'";
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID,
                DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID,
                DataBaseTables.APPLICATION_LIST_FIRST_NAME,
                DataBaseTables.APPLICATION_LIST_LAST_NAME,
                DataBaseTables.APPLICATION_LIST_USERNAME,
                DataBaseTables.APPLICATION_LIST_EMAIL,
                DataBaseTables.APPLICATION_LIST_STATUS,
                DataBaseTables.SERVER_ID
        };

        return new CursorLoader(getActivity(), uri,
                allColumns, selection, null, null);
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
