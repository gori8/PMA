package rs.ac.uns.ftn.sportly.ui.user_profile;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import rs.ac.uns.ftn.sportly.R;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent myIntent = getIntent();
        String name = myIntent.getStringExtra("name");
        String surname = myIntent.getStringExtra("surname");
        String username = myIntent.getStringExtra("username");
        String email = myIntent.getStringExtra("email");
        int photoUrl = myIntent.getIntExtra("photoUrl",0);

        TextView nameSurnameUsernameTV = findViewById(R.id.user_profile_name_surname_username);
        String nameSurnameUsernameText = name + " " + surname + " (" + username + ") ";
        nameSurnameUsernameTV.setText(nameSurnameUsernameText);

        TextView emailTV = findViewById(R.id.user_profile_email);
        emailTV.setText(email);

        ImageView img = findViewById(R.id.user_profile_icon);
        img.setImageResource(photoUrl);

        List<String> names = new ArrayList<>();
        names.add("Milan Skrbic");
        names.add("Igor Antolovic");

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.milan_skrbic);
        images.add(R.drawable.igor_antolovic);

        List<String> descriptions = new ArrayList<>();
        descriptions.add("Odlican igrac. Odlicna saradnja i dogovor.");
        descriptions.add("Nije lose moze bolje.");

        List<Float> ratings = new ArrayList<>();
        ratings.add(4f);
        ratings.add(3f);

        UserProfileAdapter adapter = new UserProfileAdapter(this, names, images, descriptions, ratings);

        ListView listView = (ListView) findViewById(R.id.user_profile_ratings);
        listView.setAdapter(adapter);
    }
}