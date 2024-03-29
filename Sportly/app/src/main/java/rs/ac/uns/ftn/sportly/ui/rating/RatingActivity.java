package rs.ac.uns.ftn.sportly.ui.rating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.BundleRatingDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.PersonToRateDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.friends.FriendsFilterAdapter;
import rs.ac.uns.ftn.sportly.ui.friends.FriendsFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class RatingActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private List<PersonToRateDTO> participantList;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Leave Ratings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        Long sportsFieldId = Long.parseLong(myIntent.getStringExtra("sportsFieldId"));
        String sportsFieldName = myIntent.getStringExtra("sportsFieldName");
        int numberOfParticipants = Integer.parseInt(myIntent.getStringExtra("numbOfParticipants"));

        participantList = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= numberOfParticipants; i++){
            PersonToRateDTO p = new PersonToRateDTO();
            p.setId(Long.parseLong(myIntent.getStringExtra("id"+i)));
            p.setName(myIntent.getStringExtra("name"+i));
            names.add(myIntent.getStringExtra("name"+i));

            participantList.add(p);
        }

        TextView tvSportsFieldName = findViewById(R.id.sfName);
        RatingBar sportsFieldRatingBar = findViewById(R.id.sfRatingBar);
        TextInputEditText sportsFieldComment = findViewById(R.id.sfComment);
        ImageView sportsFieldImage= findViewById(R.id.sfImage);

        tvSportsFieldName.setText(sportsFieldName);

        createList();

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){

                mProgressDialog = new ProgressDialog(RatingActivity.this);
                mProgressDialog.setTitle("Rating...");
                mProgressDialog.setMessage("Please wait while we are processing your ratings.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String sfComment="";

                if(sportsFieldComment.getText() != null){
                    sfComment = sportsFieldComment.getText().toString();
                }

                BundleRatingDTO bundle = new BundleRatingDTO();
                bundle.setSportsFieldId(sportsFieldId);
                bundle.setSportsFieldComment(sfComment);
                bundle.setSportsFieldRating(sportsFieldRatingBar.getRating());
                bundle.setPeople(participantList);

                String jwt = JwtTokenUtils.getJwtToken(RatingActivity.this);
                String authHeader = "Bearer " + jwt;

                Call<Long> call = SportlyServerServiceUtils.sportlyServerService.rateEverything(authHeader,bundle);

                call.enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.code() == 201){

                            Log.i("RATE EVERYTHING", "CALL TO SERVER SUCCESSFUL");

                            mProgressDialog.dismiss();

                            Intent intent = new Intent(RatingActivity.this, MainActivity.class);
                            startActivity(intent);

                        }else{
                            Log.i("RATE EVERYTHING", "CALL TO SERVER RESPONSE CODE: "+response.code());
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("RATE EVERYTHING", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        sportsFieldRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(v == 0.0){
                    sportsFieldComment.setText("");
                    sportsFieldComment.setEnabled(false);
                }else{
                    sportsFieldComment.setEnabled(true);
                }
            }
        });
    }

    private void createList(){

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.participant_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (PersonToRateDTO personDTO : participantList) {
            View row  = inflater.inflate(R.layout.rate_participant_item, linearLayout, false);

            ImageView imageView = row.findViewById(R.id.image);
            TextView tvName = row.findViewById(R.id.name);
            RatingBar ratingBar = row.findViewById(R.id.ratingBar);
            TextInputEditText commentInput = row.findViewById(R.id.comment);

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(personDTO.getId().toString());

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

            tvName.setText(personDTO.getName());

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    personDTO.setRating(v);
                }
            });

            commentInput.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String comment = "";
                    if(s != null && s.toString()!=null){
                        comment = s.toString();
                    }

                    personDTO.setComment(comment);
                }
            });

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    if(v == 0.0){
                        commentInput.setText("");
                        commentInput.setEnabled(false);
                    }else{
                        commentInput.setEnabled(true);
                    }
                }
            });

            linearLayout.addView(row);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Leaving Activity")
                .setMessage("Once you leave this activity you won't be able to leave ratings about this event. Are you sure that you want to leave?")
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onBackPressed();
                    }})
                .show();
        return true;
    }
}