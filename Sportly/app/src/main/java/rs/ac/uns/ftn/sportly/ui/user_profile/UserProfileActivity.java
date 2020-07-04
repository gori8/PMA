package rs.ac.uns.ftn.sportly.ui.user_profile;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.UserRatingDTO;
import rs.ac.uns.ftn.sportly.dto.UserWithRatingsDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.messages.chat.ChatActivity;
import rs.ac.uns.ftn.sportly.ui.rating.RatingActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class UserProfileActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;

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

        TextView tvName = findViewById(R.id.user_profile_name);
        TextView tvEmail = findViewById(R.id.user_profile_email);
        Button addFriendButton = findViewById(R.id.addFriendButton);
        Button removeFriendButton = findViewById(R.id.removeFriendButton);
        Button messageButton = findViewById(R.id.messageButton);
        ImageView imageView = findViewById(R.id.user_profile_icon);
        ProgressBar loadingCircle = findViewById(R.id.loadingCircle);

        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.FRIENDS_FIRST_NAME,
                DataBaseTables.FRIENDS_LAST_NAME,
                DataBaseTables.FRIENDS_EMAIL,
                DataBaseTables.FRIENDS_USERNAME,
                DataBaseTables.FRINEDS_TYPE,
                DataBaseTables.SERVER_ID
        };

        if(JwtTokenUtils.getUserId(this) == userId){
            addFriendButton.setVisibility(View.GONE);
            removeFriendButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
        }else{

            boolean isFriend = false;

            Cursor cursor = getContentResolver().query(
                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
                    allColumns,null,null,null
            );

            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                if(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID)) == userId){
                    isFriend = true;
                    break;
                }
                cursor.moveToNext();
            }

            if(isFriend){
                addFriendButton.setVisibility(View.GONE);
                removeFriendButton.setVisibility(View.VISIBLE);
            }else{
                removeFriendButton.setVisibility(View.GONE);
                addFriendButton.setVisibility(View.VISIBLE);
            }
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId.toString());

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("thumb_image").getValue() != null){
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avatar).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String jwt = JwtTokenUtils.getJwtToken(this);
        String authHeader = "Bearer " + jwt;

        Call<UserWithRatingsDTO> call = SportlyServerServiceUtils.sportlyServerService.getUserWithRatings(authHeader,userId);

        call.enqueue(new Callback<UserWithRatingsDTO>() {
            @Override
            public void onResponse(Call<UserWithRatingsDTO> call, Response<UserWithRatingsDTO> response) {
                if (response.code() == 200){

                    Log.i("GET USER", "CALL TO SERVER SUCCESSFUL");

                    tvName.setText(response.body().getFirstName() + " " +response.body().getLastName());
                    tvEmail.setText(response.body().getEmail());

                    TextView emptyListText = UserProfileActivity.this.findViewById(R.id.emptyListText);
                    ListView listView = (ListView) findViewById(R.id.user_profile_ratings);


                    if(response.body().getRatings().size() == 0 || response.body().getRatings() == null){
                        emptyListText.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }else{
                        emptyListText.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }

                    List<String> names = new ArrayList<>();
                    for (UserRatingDTO r : response.body().getRatings()) {
                        names.add(r.getCreatorFirstName() + " " +r.getCreatorLastName());
                    }

                    UserProfileAdapter adapter = new UserProfileAdapter(UserProfileActivity.this, response.body().getRatings(),names);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Long userId = adapter.getRatings().get(position).getCreatorId();

                        Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);

                        intent.putExtra("id",userId);

                        startActivity(intent);
                    });
                }else{
                    Log.i("GET USER", "CALL TO SERVER RESPONSE CODE: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<UserWithRatingsDTO> call, Throwable t) {
                Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                Log.i("GET USER", "CALL TO SERVER FAILED");
            }
        });

        removeFriendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(UserProfileActivity.this)
                        .setTitle("Remove friend")
                        .setMessage("Do you really want to remove this friend?")
                        .setIcon(R.drawable.ic_delete_black_24dp)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                removeFriendButton.setVisibility(View.GONE);
                                loadingCircle.setVisibility(View.VISIBLE);

                                FriendshipRequestDto request = new FriendshipRequestDto();
                                String email = tvEmail.getText().toString();
                                request.setRecEmail(email);

                                Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.deleteFriend(authHeader,request);

                                call.enqueue(new Callback<FriendshipDTO>() {
                                    @Override
                                    public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                                        if (response.code() == 200){

                                            Log.i("REMOVE FRIEND", "CALL TO SERVER SUCCESSFUL");

                                            getContentResolver().delete(
                                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
                                                    DataBaseTables.FRIENDS_EMAIL+" = '"+email+"'",
                                                    null);

                                            loadingCircle.setVisibility(View.GONE);
                                            addFriendButton.setVisibility(View.VISIBLE);
                                        }else{
                                            Log.i("REMOVE FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                                            loadingCircle.setVisibility(View.GONE);
                                            removeFriendButton.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                                        Log.i("REMOVE FRIEND", "CALL TO SERVER FAILED");
                                        loadingCircle.setVisibility(View.GONE);
                                        removeFriendButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }})
                        .show();
            }
        });

        addFriendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFriendButton.setVisibility(View.GONE);
                loadingCircle.setVisibility(View.VISIBLE);

                FriendshipRequestDto request = new FriendshipRequestDto();
                String email = tvEmail.getText().toString();
                request.setRecEmail(email);

                Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.addFriend(authHeader,request);

                call.enqueue(new Callback<FriendshipDTO>() {
                    @Override
                    public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                        if (response.code() == 200){

                            Log.i("ADD FRIEND", "CALL TO SERVER SUCCESSFUL");

                            loadingCircle.setVisibility(View.GONE);
                            removeFriendButton.setVisibility(View.VISIBLE);

                        }else{
                            Log.i("ADD FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                            loadingCircle.setVisibility(View.GONE);
                            addFriendButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("ADD FRIEND", "CALL TO SERVER FAILED");
                        loadingCircle.setVisibility(View.GONE);
                        addFriendButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        messageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(UserProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("user_id", userId);
                chatIntent.putExtra("user_name", tvName.getText());
                startActivity(chatIntent);

            }
        });
    }
}