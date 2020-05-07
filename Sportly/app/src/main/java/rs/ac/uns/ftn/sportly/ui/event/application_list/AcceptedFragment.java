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

public class AcceptedFragment extends Fragment {

    public AcceptedFragment() {
        // Required empty public constructor
    }

    public static AcceptedFragment newInstance(String param1, String param2) {
        AcceptedFragment fragment = new AcceptedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accepted, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> names = new ArrayList<>();
        names.add("Milan Škrbić");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.milan_skrbic);

        AcceptedAdapter adapter = new AcceptedAdapter(getContext(), names, images);

        ListView listView = (ListView) getActivity().findViewById(R.id.accepted_list);
        listView.setAdapter(adapter);
    }
}
