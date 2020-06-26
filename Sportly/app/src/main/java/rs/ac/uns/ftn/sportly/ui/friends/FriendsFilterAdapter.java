package rs.ac.uns.ftn.sportly.ui.friends;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.FriendDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

@Getter
@Setter
public class FriendsFilterAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<PeopleDTO> peopleList;
    private DatabaseReference mUserDatabase;

    FriendsFilterAdapter(Context c, List<PeopleDTO> pl, List<String> names){
        super(c, R.layout.friend_item, R.id.name, names);
        this.context = c;
        this.peopleList = pl;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.friend_item, parent, false);
        TextView textView = row.findViewById(R.id.name);
        ImageView imageView = row.findViewById(R.id.friend_image);

        textView.setText(peopleList.get(position).getFirstName()+" "+peopleList.get(position).getLastName());

        if(!peopleList.get(position).isFriend()){
            AppCompatImageButton deleteButton = row.findViewById(R.id.imageButton);
            AppCompatImageButton addButton = row.findViewById(R.id.addButton);

            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        }

        String userId = peopleList.get(position).getId().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        String jwt = JwtTokenUtils.getJwtToken(context);
        String authHeader = "Bearer " + jwt;

        ImageButton addButton = row.findViewById(R.id.addButton);
        ImageButton removeButton = row.findViewById(R.id.imageButton);
        ProgressBar loadingCircle = row.findViewById(R.id.loadingCircle);

        addButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addButton.setVisibility(View.GONE);
                loadingCircle.setVisibility(View.VISIBLE);

                FriendshipRequestDto request = new FriendshipRequestDto();
                String email = peopleList.get(position).getEmail();
                request.setRecEmail(email);

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
            }
        });


        removeButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(context)
                        .setTitle("Remove friend")
                        .setMessage("Do you really want to remove this friend?")
                        .setIcon(R.drawable.ic_delete_black_24dp)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                removeButton.setVisibility(View.GONE);
                                loadingCircle.setVisibility(View.VISIBLE);

                                FriendshipRequestDto request = new FriendshipRequestDto();
                                String email = peopleList.get(position).getEmail();
                                request.setRecEmail(email);

                                Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.deleteFriend(authHeader,request);

                                call.enqueue(new Callback<FriendshipDTO>() {
                                    @Override
                                    public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                                        if (response.code() == 200){

                                            Log.i("REMOVE FRIEND", "CALL TO SERVER SUCCESSFUL");

                                            context.getContentResolver().delete(
                                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
                                                    DataBaseTables.SERVER_ID+" = "+peopleList.get(position).getId(),
                                                    null);

                                            loadingCircle.setVisibility(View.GONE);
                                            addButton.setVisibility(View.VISIBLE);

                                        }else{
                                            Log.i("REMOVE FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                                            loadingCircle.setVisibility(View.GONE);
                                            removeButton.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                                        Log.i("REMOVE FRIEND", "CALL TO SERVER FAILED");
                                        loadingCircle.setVisibility(View.GONE);
                                        removeButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }})
                        .show();
            }
        });

        return row;
    }
}

