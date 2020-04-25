package rs.ac.uns.ftn.sportly.ui.my_ratings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import rs.ac.uns.ftn.sportly.R;

public class MyRatingsFragment extends Fragment {

    private MyRatingsViewModel myRatingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myRatingsViewModel =
                ViewModelProviders.of(this).get(MyRatingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_ratings, container, false);
        final TextView textView = root.findViewById(R.id.text_my_ratings);
        myRatingsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
