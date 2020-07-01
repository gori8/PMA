package rs.ac.uns.ftn.sportly.ui.user_profile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.PersonToRateDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;

public class UserProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        Long userId = myIntent.getLongExtra("id",-1L);

        Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.addFriend(authHeader,request);

        call.enqueue(new Callback<FriendshipDTO>() {
            @Override
            public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                if (response.code() == 200){

                    Log.i("ADD FRIEND", "CALL TO SERVER SUCCESSFUL");

                    loadingCircle.setVisibility(View.GONE);
                    removeButton.setVisibility(View.VISIBLE);

                }else{
                    Log.i("ADD FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                    loadingCircle.setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                Log.i("ADD FRIEND", "CALL TO SERVER FAILED");
                loadingCircle.setVisibility(View.GONE);
                addButton.setVisibility(View.VISIBLE);
            }
        });

        TextView nameSurnameUsernameTV = findViewById(R.id.user_profile_name_surname_username);
        String nameSurnameUsernameText = name + " " + surname + " (" + username + ") ";
        nameSurnameUsernameTV.setText(nameSurnameUsernameText);

        TextView emailTV = findViewById(R.id.user_profile_email);
        emailTV.setText(email);

        ImageView img = findViewById(R.id.user_profile_icon);
        img.setImageResource(photoUrl);

        List<String> names = new ArrayList<>();

        List<PersonToRateDTO> ratings = new ArrayList<>();

        UserProfileAdapter adapter = new UserProfileAdapter(this, ratings, names);

        ListView listView = (ListView) findViewById(R.id.user_profile_ratings);
        listView.setAdapter(adapter);
    }
}