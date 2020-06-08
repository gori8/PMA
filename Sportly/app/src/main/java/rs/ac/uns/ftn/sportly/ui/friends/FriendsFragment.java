package rs.ac.uns.ftn.sportly.ui.friends;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.database.SportlySQLiteHelper;

public class FriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.message));
        searchAutoComplete.setTextColor(ContextCompat.getColor(getActivity(), R.color.message));
        View searchplate = (View)searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(R.drawable.background_search);
        ImageView searchClose = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_clear_24dp);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] { DataBaseTables.FRIENDS_FIRST_NAME, DataBaseTables.FRIENDS_LAST_NAME };
        int[] to = new int[] {R.id.firstName, R.id.lastName};
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.friend_item, null, from,
                to, 0);
        ListView listView = (ListView) getView().findViewById(R.id.friends_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor)adapter.getItem(position);
            String name = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_FIRST_NAME));
            String surname = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_LAST_NAME));
            String username = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_FIRST_NAME));
            String email = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_EMAIL));
            int photoUrl = 0;

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.goToUserProfileActivity(name, surname, username, email, photoUrl);
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.FRIENDS_FIRST_NAME,
                DataBaseTables.FRIENDS_LAST_NAME,
                DataBaseTables.FRIENDS_EMAIL,
                DataBaseTables.FRIENDS_USERNAME,
                DataBaseTables.SERVER_ID
        };

        return new CursorLoader(getActivity(), Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
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
