package rs.ac.uns.ftn.sportly.ui.favorites;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.friends.FriendsAdapter;

public class FavoritesFragment extends Fragment {

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

        List<String> names = new ArrayList<>();
        names.add("Sportski centar Spens");
        names.add("Bolesnikov");
        names.add("Đačko igralište");
        names.add("Karađorđe Stadium");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.spens);
        images.add(R.drawable.bolesnikov);
        images.add(R.drawable.djacko);
        images.add(R.drawable.karadjordjestadium);

        List<String> descriptions = new ArrayList<>();
        descriptions.add("Zadužen za sve sportske aktivnosti.");
        descriptions.add("Fudbalski tereni. Treninzi i škola fudbala.");
        descriptions.add("Tereni na otvorenom.");
        descriptions.add("Stadion FK Vojvodina.");

        List<Float> ratings = new ArrayList<>();
        ratings.add(4f);
        ratings.add(3f);
        ratings.add(1.8f);
        ratings.add(4.5f);

        FavoriteAdapter adapter = new FavoriteAdapter(getContext(), names, images, descriptions, ratings);

        ListView listView = (ListView) getActivity().findViewById(R.id.favorite_list);
        listView.setAdapter(adapter);
    }
}
