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
import android.widget.Filterable;
import android.widget.ImageButton;
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
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class QueueCursorAdapter extends SimpleCursorAdapter implements Filterable {

    private Context mContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private Long eventId;

    public QueueCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to,Long eId) {
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

        TextView nameText = (TextView)view.findViewById(R.id.name);
        ImageView imageView = (ImageView)view.findViewById(R.id.image);

        int firstNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_FIRST_NAME);
        int lastNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_LAST_NAME);
        int applierIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID);

        nameText.setText(cursor.getString(firstNameIndex)+" "+cursor.getString(lastNameIndex));

        String applierId = cursor.getString(applierIdIndex);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(applierId);

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

        int requestIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_REQUEST_ID);
        Long requestId = cursor.getLong(requestIdIndex);
        String serverId = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID));

        ImageButton acceptButton = view.findViewById(R.id.acceptButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Accepting a user for the event...");
                mProgressDialog.setMessage("Please wait while we are processing your accept.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.acceptApplicationForEvent(authHeader,requestId);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("ACCEPT APPLICATION", "CALL TO SERVER SUCCESSFUL");

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.APPLICATION_LIST_STATUS,"PARTICIPATING");
                            values.put(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID, response.body().getId());

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                    values,
                                    DataBaseTables.SERVER_ID + " = '"+serverId+"'",
                                    null);

                            Cursor eventCursor = context.getContentResolver().query(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS+"/"+eventId),
                                    new String[]{DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS},
                                    null,null,null
                            );

                            eventCursor.moveToFirst();
                            Integer numOfParticipants =  1 + eventCursor.getInt(eventCursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS));

                            ContentValues valuesEvent = new ContentValues();
                            valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,numOfParticipants);

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                    valuesEvent,
                                    DataBaseTables.SERVER_ID+" = "+eventId,
                                    null
                            );
                        }else{
                            Log.i("ACCEPT APPLICATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("ACCEPT APPLICATION", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });

            }
        });


        ImageButton denyButton = view.findViewById(R.id.denyButton);

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Declining Application...");
                mProgressDialog.setMessage("Please wait while we are processing your declining.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.declineApplicationForEvent(authHeader,requestId);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("DECLINE APPLICATION", "CALL TO SERVER SUCCESSFUL");

                            context.getContentResolver().delete(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                    DataBaseTables.APPLICATION_LIST_REQUEST_ID+" = "+requestId,
                                    null
                            );

                        }else{
                            Log.i("DECLINE APPLICATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("DECLINE APPLICATION", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });

            }
        });
    }
}
