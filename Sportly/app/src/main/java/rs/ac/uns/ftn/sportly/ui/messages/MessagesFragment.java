package rs.ac.uns.ftn.sportly.ui.messages;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.map.MapViewModel;
import rs.ac.uns.ftn.sportly.ui.notifications.NotificationAdapter;

public class MessagesFragment extends Fragment {

   public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_messages, container, false);

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> names = new ArrayList<>();
        names.add("Milan Škrbić");
        names.add("Stevan Vulić");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.milan_skrbic);
        images.add(R.drawable.stevan_vulic);

        List<String> info = new ArrayList<>();
        info.add("Vazi, mozemo se prijaviti u petak.");
        info.add("Pozdrav!");

        List<String> time = new ArrayList<>();
        time.add("20:18");
        time.add("2d");

        MessagesAdapter adapter = new MessagesAdapter(getContext(), names, images, info, time);

        ListView listView = (ListView) getActivity().findViewById(R.id.messages_list);
        listView.setAdapter(adapter);
    }

}
