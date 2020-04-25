package rs.ac.uns.ftn.sportly.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;

public class NotificationsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        return root;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<String> names = new ArrayList<>();
        names.add("Igor Antolović");
        names.add("Stevan Vulić");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.igor_antolovic);
        images.add(R.drawable.stevan_vulic);

        List<String> info = new ArrayList<>();
        info.add("Has accepted your friend request.");
        info.add("Has invited you to the event.");

        List<String> time = new ArrayList<>();
        time.add("14:30");
        time.add("3d");

        NotificationAdapter adapter = new NotificationAdapter(getContext(), names, images, info, time);

        ListView listView = (ListView) getActivity().findViewById(R.id.notification_list);
        listView.setAdapter(adapter);
    }
}
