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

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import lombok.SneakyThrows;
import rs.ac.uns.ftn.sportly.sync.SyncDataService;
import rs.ac.uns.ftn.sportly.ui.login.LoginActivity;
import rs.ac.uns.ftn.sportly.ui.user_profile.UserProfileActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;
import rs.ac.uns.ftn.sportly.utils.SportlyUtils;

public class MainActivity extends AppCompatActivity {

    //TODO fragment se ponovo kreira kada se na toolbaru izabere vec selektovani!!!

    DrawerLayout drawer;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    AppBarConfiguration appBarConfiguration;
    NavController navController;

    public static String NOTIFICATION_INTENT = "NOTIFICATION";


    private PendingIntent pendingIntent;


    private AlarmManager manager;



    String name;
    String surname;
    String username;
    String email;
    int photoUrl;

    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(JwtTokenUtils.getUserId(this).toString());

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

                if(SportlyUtils.getConnectivityStatus(MainActivity.this) == 0){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("You are not signed in.").
                            setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog alert1 = alert.create();
                    alert1.show();
                }
                else {
                    showLogoutPopup();
                }
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
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseMessaging.getInstance().unsubscribeFromTopic(JwtTokenUtils.getUserId(MainActivity.this).toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
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

                                        goToLoginActivity();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @SneakyThrows
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("MAIN_ACTIVITY","FAILED TO UNSUBSCRIBE");
                                throw e;
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Log.i("MAIN_ACTIVITY","UNSUBSCRIBE CANCELLED");
                            }
                        });


                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private boolean tryGoogleSignOut(){
        try{
            LoginActivity.mGoogleSignInClient.signOut();
            System.out.println("Google sign out success");
            JwtTokenUtils.removeJwtToken(MainActivity.this);
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
            JwtTokenUtils.removeJwtToken(MainActivity.this);
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

        intent.putExtra("id", JwtTokenUtils.getUserId(this));

        mainActivity.startActivity(intent);
    }

    public void onDrawerIconClick(View v) {
        goToUserProfileActivity(name, surname, username, email, photoUrl);
    }

    //TEMP FUNCTION FOR FILLING DATA
    public void fillDataBasedOnEmail(){
        //FUNKCIJA DA SE POPUNE INFO U NAV DRAWERU

        ImageView drawerIcon = navigationView.getHeaderView(0).findViewById(R.id.main_drawer_icon);
        TextView nameTV = navigationView.getHeaderView(0).findViewById(R.id.drawer_title);
        TextView emailTV = navigationView.getHeaderView(0).findViewById(R.id.drawer_subTitle);

        //drawerIcon.setImageResource(photoUrl);
        nameTV.setText(JwtTokenUtils.getName(this));
        emailTV.setText(JwtTokenUtils.getEmail(this));

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(JwtTokenUtils.getUserId(this).toString());
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("thumb_image").getValue()!=null){
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avatar).into(drawerIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        manager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 30*1000, pendingIntent);
    }




    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
        Log.i("MAIN ACTIVITY","ACTIVITY MAIN PAUSED");



        super.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();

        mUserRef.child("online").setValue("true");

    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }


}
