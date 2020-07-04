package rs.ac.uns.ftn.sportly.ui.event.application_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import rs.ac.uns.ftn.sportly.R;

public class ApplicationListActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.app_list_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_queue, R.id.navigation_accepted, R.id.navigation_invite)
                .build();
        navController = Navigation.findNavController(this, R.id.app_list_fragment);
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

        navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                return;
            }

        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        ApplicationListActivity.this.finish();
    }
}
