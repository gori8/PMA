package rs.ac.uns.ftn.sportly.ui.event.application_list;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventRequestDTO;
import rs.ac.uns.ftn.sportly.dto.EventRequestRequest;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class InviteCursorAdapter extends SimpleCursorAdapter implements Filterable {

    private Context mContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private Long eventId;

    public InviteCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to,Long eId) {
        super(context,layout,c,new String[]{from[0]},to);
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
        eventId = eId;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        int serverIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID);
        Long serverId = cursor.getLong(serverIdIndex);
        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_LAST_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_EMAIL));


        TextView nameText = (TextView)view.findViewById(R.id.name);
        ImageView imageView = (ImageView)view.findViewById(R.id.image);

        int firstNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_FIRST_NAME);
        int lastNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.FRIENDS_LAST_NAME);

        nameText.setText(cursor.getString(firstNameIndex)+" "+cursor.getString(lastNameIndex));

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(serverId.toString());

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

        Button inviteButton = view.findViewById(R.id.inviteButton);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Inviting Friend...");
                mProgressDialog.setMessage("Please wait while we are processing your invitation.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                EventRequestRequest request = new EventRequestRequest();
                request.setEventId(eventId);
                request.setUserEmail(email);

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.inviteFriendOnEvent(authHeader,request);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("INVITE FRIEND", "CALL TO SERVER SUCCESSFUL");

                            ContentValues valuesApplicationList = new ContentValues();
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID,eventId);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID,serverId);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_FIRST_NAME,firstName);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_LAST_NAME,lastName);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_USERNAME,email);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EMAIL,email);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_STATUS,"INVITED");
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_REQUEST_ID,response.body().getId());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID, 0);
                            valuesApplicationList.put(DataBaseTables.SERVER_ID,"E"+eventId+"A"+serverId);

                            context.getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                    valuesApplicationList);
                        }else{
                            Log.i("INVITE FRIEND", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("INVITE FRIEND", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });

            }
        });
    }
}
