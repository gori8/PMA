package rs.ac.uns.ftn.sportly.ui.settings;

import android.media.audiofx.Equalizer;
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
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import rs.ac.uns.ftn.sportly.R;

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);

    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        BottomNavigationView toolbar = getActivity().findViewById(R.id.nav_view);
        toolbar.setVisibility(View.VISIBLE);
    }
}
