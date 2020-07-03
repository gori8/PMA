package rs.ac.uns.ftn.sportly.ui.event.application_list;

import android.content.ContentResolver;
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
import rs.ac.uns.ftn.sportly.ui.user_profile.UserProfileActivity;


public class InviteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private InviteCursorAdapter adapter;
    private Long eventId;

    public InviteFragment() {
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
        String[] from = new String[] { DataBaseTables.FRIENDS_FIRST_NAME, DataBaseTables.FRIENDS_LAST_NAME };
        int[] to = new int[] {R.id.name};
        adapter = new InviteCursorAdapter(getActivity(), R.layout.invite_item, null, from, to, eventId);
        ListView listView = (ListView) getActivity().findViewById(R.id.invite_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor)adapter.getItem(position);

            Intent intent = new Intent(InviteFragment.this.getContext(), UserProfileActivity.class);

            intent.putExtra("id",cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID)));

            startActivity(intent);
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = InviteFragment.this.getActivity().getIntent();
        eventId = intent.getLongExtra("eventId",-1);

        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+ DataBaseTables.TABLE_FRIENDS+"/invite");

        return new CursorLoader(getActivity(), uri,
                null, eventId.toString(), null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        TextView emptyListText = InviteFragment.this.getActivity().findViewById(R.id.emptyListText);

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