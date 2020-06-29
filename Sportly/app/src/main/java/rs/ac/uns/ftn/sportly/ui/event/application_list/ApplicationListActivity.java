package rs.ac.uns.ftn.sportly.ui.event.application_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import rs.ac.uns.ftn.sportly.R;

public class ApplicationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);
        BottomNavigationView navView = findViewById(R.id.app_list_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_queue, R.id.navigation_accepted, R.id.navigation_invite)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.app_list_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        Intent myIntent = getIntent();
        String eventName = myIntent.getStringExtra("eventName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            if(eventName != null){
                setTitle(eventName);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
