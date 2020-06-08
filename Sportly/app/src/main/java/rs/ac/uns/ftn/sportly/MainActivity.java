package rs.ac.uns.ftn.sportly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;




import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import rs.ac.uns.ftn.sportly.sync.SyncDataService;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;
import rs.ac.uns.ftn.sportly.ui.user_profile.UserProfileActivity;

public class MainActivity extends AppCompatActivity {

    //TODO fragment se ponovo kreira kada se na toolbaru izabere vec selektovani!!!

    DrawerLayout drawer;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    AppBarConfiguration appBarConfiguration;
    NavController navController;

    public static String SYNC_DATA = "SYNC_DATA";

    private PendingIntent pendingIntent;

    private AlarmManager manager;


    String name;
    String surname;
    String username;
    String email;
    int photoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent alarmIntent = new Intent(this, SyncDataService.class);
        pendingIntent = PendingIntent.getService(this, 888, alarmIntent, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        init();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void init() {
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.nav_view);

        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_friends, R.id.navigation_favorites, R.id.navigation_map, R.id.navigation_notifications, R.id.navigation_messages)
                .setDrawerLayout(drawer)
                .build();

        createOnClickListenerForSignOut();


        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                return;
            }

        });

        fillDataBasedOnEmail();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    public void createOnClickListenerForSignOut(){
        final Button button = findViewById(R.id.logout);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLogoutPopup();
            }
        });
    }

    private void goToLoginActivity(){
        MainActivity mainActivity = (MainActivity) this;
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.startActivity(intent);
    }

    private void showLogoutPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        boolean google = false;
                        boolean facebook = false;
                        boolean email = false;

                        if (LoginActivity.signInMethod == LoginActivity.GOOGLE){
                            google = tryGoogleSignOut();
                        }else if(LoginActivity.signInMethod == LoginActivity.FACEBOOK){
                            facebook = tryFacebookSignOut();
                        }else if(LoginActivity.signInMethod == LoginActivity.EMAIL_ACCOUNT){
                            email = true;
                        }

                        if(google || facebook || email) {
                            goToLoginActivity();
                        }
                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private boolean tryGoogleSignOut(){
        try{
            LoginActivity.mGoogleSignInClient.signOut();
            System.out.println("Google sign out success");
            return true;
        }catch (Exception e){
            System.out.println("Google sign out error");
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean tryFacebookSignOut(){
        try{
            LoginManager.getInstance().logOut();
            System.out.println("Facebook sign out success");
            return true;
        }catch (Exception e){
            System.out.println("Facebook sign out error");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void goToUserProfileActivity(String name, String surname, String username, String email, int photoUrl){
        MainActivity mainActivity = (MainActivity) this;
        Intent intent = new Intent(mainActivity, UserProfileActivity.class);

        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("email", email);
        intent.putExtra("photoUrl", photoUrl);

        mainActivity.startActivity(intent);
    }

    public void onDrawerIconClick(View v) {
        goToUserProfileActivity(name, surname, username, email, photoUrl);
    }

    //TEMP FUNCTION FOR FILLING DATA
    public void fillDataBasedOnEmail(){
        String current_email = LoginActivity.userEmail;
        if(current_email.equals("None")){
            return;
        }

        String stevanAccount = "stevan@gmail.com";
        String stevanGoogle = "stevanvulic96@gmail.com";
        String stevanFacebook = "stevafudbal@gmail.com";

        String milanAccount = "milan@gmail.com";
        String milanGoogle = "kickapoo889@gmail.com";
        String milanFacebook = "kickapoo889@gmail.com";

        String igorAccount = "igor@gmail.com";
        String igorGoogle = "goriantolovic@gmail.com";
        String igorFacebook = "goriantolovic@gmail.com";

        ImageView drawerIcon = navigationView.getHeaderView(0).findViewById(R.id.main_drawer_icon);
        TextView nameTV = navigationView.getHeaderView(0).findViewById(R.id.drawer_title);
        TextView emailTV = navigationView.getHeaderView(0).findViewById(R.id.drawer_subTitle);

        if(current_email.equals(stevanAccount) || current_email.equals(stevanGoogle) || current_email.equals(stevanFacebook)){
            name = "Stevan";
            surname = "Vulić";
            username = "Vul4";
            photoUrl = R.drawable.stevan_vulic;
        }else if(current_email.equals(milanAccount) || current_email.equals(milanGoogle) || current_email.equals(milanFacebook)){
            name = "Milan";
            surname = "Skrbić";
            username = "shekrba";
            photoUrl = R.drawable.milan_skrbic;
        }else if(current_email.equals(igorAccount) || current_email.equals(igorGoogle) || current_email.equals(igorFacebook)){
            name = "Igor";
            surname = "Antolović";
            username = "gori8";
            photoUrl = R.drawable.igor_antolovic;
        }

        email = current_email;

        drawerIcon.setImageResource(photoUrl);
        nameTV.setText(name + " " + surname);
        emailTV.setText(email);
    }


    @Override
    protected void onResume() {
        super.onResume();


        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 20*1000, pendingIntent);
    }




    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
        Log.i("MAIN ACTIVITY","ACTIVITY MAIN PAUSED");

        super.onPause();
    }




}
