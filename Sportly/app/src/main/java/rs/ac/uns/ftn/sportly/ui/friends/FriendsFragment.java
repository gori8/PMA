package rs.ac.uns.ftn.sportly.ui.friends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;

public class FriendsFragment extends Fragment {


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

        ListView listView = (ListView) getActivity().findViewById(R.id.friends_list);
        listView.setAdapter(adapter);
    }
}
