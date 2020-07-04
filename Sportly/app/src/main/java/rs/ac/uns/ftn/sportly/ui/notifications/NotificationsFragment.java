package rs.ac.uns.ftn.sportly.ui.notifications;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.ui.favorites.FavoriteCursorAdapter;

public class NotificationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private NotificationCursorAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] {
                DataBaseTables.NOTIFICATIONS_TITTLE,
                DataBaseTables.NOTIFICATIONS_MESSAGE,
                DataBaseTables.NOTIFICATIONS_DATE
        };
        int[] to = new int[] {R.id.notification_name, R.id.notification_info, R.id.notification_time};
        adapter = new NotificationCursorAdapter(getActivity(), R.layout.favorite_item, null, from,
                to);

        ListView listView = (ListView) getActivity().findViewById(R.id.notification_list);
        listView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.NOTIFICATIONS_TITTLE,
                DataBaseTables.NOTIFICATIONS_TYPE,
                DataBaseTables.NOTIFICATIONS_MESSAGE,
                DataBaseTables.NOTIFICATIONS_DATE,
                DataBaseTables.SERVER_ID
        };

        return new CursorLoader(getActivity(), Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_NOTIFICATIONS),
                allColumns, null, null, null);
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
