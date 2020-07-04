package rs.ac.uns.ftn.sportly.ui.friends;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.database.SportlySQLiteHelper;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class RequestCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMyDatabase;
    private DatabaseReference mFriendsDatabase;
    private ProgressDialog mProgressDialog;


    public RequestCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
        super(context,layout,c,new String[]{from[0]},to);
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView nameText = (TextView)view.findViewById(R.id.name);
        ImageView imageView = (ImageView)view.findViewById(R.id.request_image);

        int firstNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_FIRST_NAME);
        int lastNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_LAST_NAME);
        int serverIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID);
        int emailIndex=cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_EMAIL);

        nameText.setText(cursor.getString(firstNameIndex)+" "+cursor.getString(lastNameIndex));

        String userId = cursor.getString(serverIdIndex);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(userId).child(JwtTokenUtils.getUserId(context).toString());
        mMyDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(JwtTokenUtils.getUserId(context).toString()).child(userId);


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


        String jwt = JwtTokenUtils.getJwtToken(context);
        String authHeader = "Bearer " + jwt;

        Button confirmButton = view.findViewById(R.id.confirm_button);
        String email = cursor.getString(emailIndex);

        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Inviting Friend...");
                mProgressDialog.setMessage("Please wait while we are processing your invitation.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                FriendshipRequestDto request = new FriendshipRequestDto();
                request.setRecEmail(email);

                Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.confirmRequest(authHeader,request);

                call.enqueue(new Callback<FriendshipDTO>() {
                    @Override
                    public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                        if (response.code() == 200){

                            Log.i("CONFIRM FRIEND", "CALL TO SERVER SUCCESSFUL");


                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put("date", new Date().toString());

                            mMyDatabase.setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mFriendsDatabase.setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task1) {

                                                if (task1.isSuccessful()) {

                                                    ContentValues values = new ContentValues();
                                                    values.put(DataBaseTables.FRINEDS_TYPE,"CONFIRMED");

                                                    context.getContentResolver().update(
                                                            Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
                                                            values,
                                                            DataBaseTables.FRIENDS_EMAIL + " = '"+ email +"'",
                                                            null);

                                                }

                                            }
                                        });
                                    }

                                }
                            });


                        }else{
                            Log.i("CONFIRM FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("CONFIRM FRIEND", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        Button removeButton = view.findViewById(R.id.delete_button);

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

                                mProgressDialog = new ProgressDialog(context);
                                mProgressDialog.setTitle("Inviting Friend...");
                                mProgressDialog.setMessage("Please wait while we are processing your invitation.");
                                mProgressDialog.setCanceledOnTouchOutside(false);
                                mProgressDialog.show();

                                FriendshipRequestDto request = new FriendshipRequestDto();
                                request.setRecEmail(email);

                                Call<FriendshipDTO> call = SportlyServerServiceUtils.sportlyServerService.deleteFriend(authHeader,request);

                                call.enqueue(new Callback<FriendshipDTO>() {
                                    @Override
                                    public void onResponse(Call<FriendshipDTO> call, Response<FriendshipDTO> response) {
                                        if (response.code() == 200){

                                            Log.i("REMOVE FRIEND", "CALL TO SERVER SUCCESSFUL");

                                            context.getContentResolver().delete(
                                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_FRIENDS),
                                                    DataBaseTables.FRIENDS_EMAIL+" = '"+email+"'",
                                                    null);
                                        }else{
                                            Log.i("REMOVE FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                                        }
                                        mProgressDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<FriendshipDTO> call, Throwable t) {
                                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                                        Log.i("REMOVE FRIEND", "CALL TO SERVER FAILED");
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .show();
            }
        });


    }
}
