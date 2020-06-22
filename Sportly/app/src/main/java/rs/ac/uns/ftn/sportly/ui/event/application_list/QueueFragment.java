package rs.ac.uns.ftn.sportly.ui.event.application_list;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;

public class QueueFragment extends Fragment {
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

        List<String> names = new ArrayList<>();
        names.add("Igor Antolović");
        names.add("Stevan Vulić");

        List<Integer> images = new ArrayList<>();

        images.add(R.drawable.igor_antolovic);
        images.add(R.drawable.stevan_vulic);

        QueueAdapter adapter = new QueueAdapter(getContext(), names, images);

        ListView listView = (ListView) getActivity().findViewById(R.id.queue_list);
        listView.setAdapter(adapter);
    }
}
