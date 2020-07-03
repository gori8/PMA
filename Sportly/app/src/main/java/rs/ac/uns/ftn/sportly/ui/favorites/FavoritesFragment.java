package rs.ac.uns.ftn.sportly.ui.favorites;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.SportsFieldDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.rating.RatingActivity;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FavoriteCursorAdapter adapter;

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
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        return root;
    }



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] {
                DataBaseTables.SPORTSFIELDS_NAME,
                DataBaseTables.SPORTSFIELDS_RATING
        };
        int[] to = new int[] {R.id.favorite_name, R.id.favorite_ratingBar};
        adapter = new FavoriteCursorAdapter(getActivity(), R.layout.favorite_item, null, from,
                to);

        ListView listView = (ListView) getActivity().findViewById(R.id.favorite_list);
        listView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.SPORTSFIELDS_NAME,
                DataBaseTables.SPORTSFIELDS_DESCRIPTION,
                DataBaseTables.SPORTSFIELDS_FAVORITE,
                DataBaseTables.SPORTSFIELDS_LATITUDE,
                DataBaseTables.SPORTSFIELDS_LONGITUDE,
                DataBaseTables.SPORTSFIELDS_RATING,
                DataBaseTables.SPORTSFIELDS_CATEGORY,
                DataBaseTables.SERVER_ID
        };

        return new CursorLoader(getActivity(), Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),
                allColumns, DataBaseTables.SPORTSFIELDS_FAVORITE+" = "+1, null, null);
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
