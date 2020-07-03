package rs.ac.uns.ftn.sportly.ui.friends;

import android.content.Intent;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.user_profile.UserProfileActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class FriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private FriendsCursorAdapter adapter;
    private FriendsFilterAdapter filterAdapter;
    private RequestCursorAdapter requestAdapter;

    List<String> names = new ArrayList<>();
    List<PeopleDTO> peopleList = new ArrayList<>();

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ScrollView friendsScrollView = (ScrollView) getView().findViewById(R.id.friends_scroll_view);
                ListView peopleListView = (ListView) getView().findViewById(R.id.people_list);

                if(newText.equals("")) {
                    friendsScrollView.setVisibility(View.VISIBLE);
                    peopleListView.setVisibility(View.GONE);
                }else{
                    searchPeople(newText);
                    friendsScrollView.setVisibility(View.GONE);
                    peopleListView.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //CURSOR ADAPTER
        getLoaderManager().initLoader(0, null, this);
        String[] from = new String[] { DataBaseTables.FRIENDS_FIRST_NAME, DataBaseTables.FRIENDS_LAST_NAME };
        int[] to = new int[] {R.id.name};
        adapter = new FriendsCursorAdapter(getActivity(), R.layout.friend_item, null, from, to);
        ListView friendsListView = (ListView) getView().findViewById(R.id.friends_list);
        friendsListView.setAdapter(adapter);

        friendsListView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor)adapter.getItem(position);

            MainActivity mainActivity = (MainActivity) getActivity();
            Intent intent = new Intent(mainActivity, UserProfileActivity.class);

            intent.putExtra("id",cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID)));

            startActivity(intent);
        });

        //REQUEST ADAPTER
        getLoaderManager().initLoader(1, null, this);
        String[] fromRequest = new String[] { DataBaseTables.FRIENDS_FIRST_NAME, DataBaseTables.FRIENDS_LAST_NAME };
        int[] toRequest = new int[] {R.id.name};
        requestAdapter = new RequestCursorAdapter(getActivity(), R.layout.request_item, null, fromRequest, toRequest);
        ListView requestsListView = (ListView) getView().findViewById(R.id.requests_list);
        requestsListView.setAdapter(requestAdapter);

        //FILTER ADAPTER
        filterAdapter = new FriendsFilterAdapter(FriendsFragment.this.getContext(), peopleList, names);

        ListView peopleListView = (ListView) getView().findViewById(R.id.people_list);
        peopleListView.setAdapter(filterAdapter);

        peopleListView.setOnItemClickListener((parent, view, position, id) -> {
            Long userId = filterAdapter.getPeopleList().get(position).getId();

            MainActivity mainActivity = (MainActivity) getActivity();
            Intent intent = new Intent(mainActivity, UserProfileActivity.class);

            intent.putExtra("id",userId);

            startActivity(intent);
        });

    }

    private void searchPeople(String filterText){

        String jwt = JwtTokenUtils.getJwtToken(this.getContext());
        String authHeader = "Bearer " + jwt;
        Call<List<PeopleDTO>> call = SportlyServerServiceUtils.sportlyServerService.searchPeople(authHeader,filterText);

        call.enqueue(new Callback<List<PeopleDTO>>() {
             @Override
             public void onResponse(Call<List<PeopleDTO>> call, Response<List<PeopleDTO>> response) {
                 if (response.code() == 200){

                     peopleList = response.body();
                     filterAdapter.setPeopleList(peopleList);
                     names.clear();

                     for (PeopleDTO p : peopleList) {
                        names.add(p.getFirstName() + " " + p.getLastName());
                     }

                     filterAdapter.notifyDataSetChanged();

                     Log.d("SEARCH PEOPLE", "CALL TO SERVER SUCCESSFUL");
                     Log.d("SEARCH PEOPLE", "RESPONSE SIZE: "+response.body().size());
                 }else{
                     Log.d("SEARCH PEOPLE", "CALL TO SERVER RESPONSE CODE: "+response.code());
                 }
             }

             @Override
             public void onFailure(Call<List<PeopleDTO>> call, Throwable t) {
                 Log.d("REZ", t.getMessage() != null?t.getMessage():"error");
                 Log.d("SEARCH PEOPLE", "CALL TO SERVER FAILED");
             }
         });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS);
        String selection = DataBaseTables.FRINEDS_TYPE+" = ";
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.FRIENDS_FIRST_NAME,
                DataBaseTables.FRIENDS_LAST_NAME,
                DataBaseTables.FRIENDS_EMAIL,
                DataBaseTables.FRIENDS_USERNAME,
                DataBaseTables.FRINEDS_TYPE,
                DataBaseTables.SERVER_ID
        };

        switch (id) {
            case 0:
                selection = selection + "'CONFIRMED'";
                break;
            case 1:
                selection = selection + "'PENDING'";
                break;
        }

        return new CursorLoader(getActivity(), uri,
                allColumns, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case 0:
                adapter.swapCursor(data);
                break;
            case 1:
                requestAdapter.swapCursor(data);
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                adapter.swapCursor(null);
                break;
            case 1:
                requestAdapter.swapCursor(null);
                break;
        }
    }

}
