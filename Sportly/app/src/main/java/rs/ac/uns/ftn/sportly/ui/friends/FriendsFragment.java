package rs.ac.uns.ftn.sportly.ui.friends;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;

public class FriendsFragment extends Fragment {

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

        List<String> names = new ArrayList<>();
        names.add("Milan Škrbić");
        names.add("Igor Antolović");
        names.add("Stevan Vulić");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.milan_skrbic);
        images.add(R.drawable.igor_antolovic);
        images.add(R.drawable.stevan_vulic);

        FriendsAdapter adapter = new FriendsAdapter(getContext(), names, images);

        ListView listView = (ListView) getView().findViewById(R.id.friends_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = "";
            String surname = "";
            String username = "";
            String email = "";
            int photoUrl = 0;

            String nameFromList = names.get(position);
            if(nameFromList.equals("Milan Škrbić")){
                name = "Milan";
                surname = "Skrbic";
                username = "shekrba";
                email = "milan@gmail.com";
                photoUrl = R.drawable.milan_skrbic;
            } else if(nameFromList.equals("Igor Antolović")){
                name = "Igor";
                surname = "Antolovic";
                username = "gori8";
                email = "igor@gmail.com";
                photoUrl = R.drawable.igor_antolovic;
            } else if(nameFromList.equals("Stevan Vulić")){
                name = "Stevan";
                surname = "Vulic";
                username = "Vul4";
                email = "stevan@gmail.com";
                photoUrl = R.drawable.stevan_vulic;
            }

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.goToUserProfileActivity(name, surname, username, email, photoUrl);
        });

    }
}
