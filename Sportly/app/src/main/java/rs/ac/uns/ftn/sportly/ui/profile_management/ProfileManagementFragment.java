package rs.ac.uns.ftn.sportly.ui.profile_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import rs.ac.uns.ftn.sportly.R;

public class ProfileManagementFragment extends Fragment {

    private ProfileManagementViewModel profileManagementViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileManagementViewModel =
                ViewModelProviders.of(this).get(ProfileManagementViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_management, container, false);
        //final TextView textView = root.findViewById(R.id.text_profile_management);
        profileManagementViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s)
            {
                //textView.setText(s);
            }
        });
        return root;
    }
}
