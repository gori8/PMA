package rs.ac.uns.ftn.sportly.ui.invite;

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
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventRequestDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.FriendshipRequestDto;
import rs.ac.uns.ftn.sportly.dto.ParticipationDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class InviteRequestCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private ProgressDialog mProgressDialog;

    public InviteRequestCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
        super(context,layout,c,from,to);
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

        TextView eventNameText = (TextView)view.findViewById(R.id.list_event_name);
        TextView timeFromText = (TextView)view.findViewById(R.id.list_event_time_from);
        TextView timeToText = (TextView)view.findViewById(R.id.list_event_time_to);
        TextView pplText = (TextView)view.findViewById(R.id.list_event_ppl);
        TextView participantsText = (TextView)view.findViewById(R.id.list_event_participants);
        ImageView imageView = view.findViewById(R.id.list_event_image);

        int eventIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID);
        int nameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NAME);
        int timeFromIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_FROM);
        int timeToIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_TO);
        int pplIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PPL);
        int participantsIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS);

        Long eventId = cursor.getLong(eventIdIndex);
        Long sportsFieldId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_SPORTS_FILED_ID));

        Cursor spCursor = context.getContentResolver().query(
                Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),
                new String[]{DataBaseTables.SPORTSFIELDS_CATEGORY},
                DataBaseTables.SERVER_ID + " = " + sportsFieldId,
                null,
                null
        );
        spCursor.moveToFirst();
        String category = spCursor.getString(spCursor.getColumnIndexOrThrow(DataBaseTables.SPORTSFIELDS_CATEGORY));

        if(category.equals("basketball")){
            imageView.setImageResource(R.drawable.ic_basketball_event);
        }else if(category.equals("football")){
            imageView.setImageResource(R.drawable.ic_football_event);
        }else if(category.equals("tennis")){
            imageView.setImageResource(R.drawable.ic_tennis_event);
        }

        eventNameText.setText(cursor.getString(nameIndex));
        pplText.setText(cursor.getString(pplIndex));
        timeFromText.setText(cursor.getString(timeFromIndex));
        timeToText.setText(cursor.getString(timeToIndex));
        participantsText.setText(cursor.getString(participantsIndex));

        String serverId = "E"+eventId+"A"+JwtTokenUtils.getUserId(context);
        Integer numbOfParticipants = cursor.getInt(participantsIndex);

        String jwt = JwtTokenUtils.getJwtToken(context);
        String authHeader = "Bearer " + jwt;

        Button confirmButton = view.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Confirming Invitation...");
                mProgressDialog.setMessage("Please wait while we are processing your confirmation.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Cursor alCursor = context.getContentResolver().query(
                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                        new String[]{DataBaseTables.APPLICATION_LIST_REQUEST_ID},
                        DataBaseTables.SERVER_ID + " = '"+serverId+"'",
                        null,
                        null
                );

                alCursor.moveToFirst();
                Long requestId = alCursor.getLong(alCursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_REQUEST_ID));

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.acceptApplicationForEvent(authHeader,requestId);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("CONFIRM EVENT INVITATION", "CALL TO SERVER SUCCESSFUL");

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID, response.body().getId());
                            values.put(DataBaseTables.APPLICATION_LIST_STATUS, "PARTICIPATING");

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                    values,
                                    DataBaseTables.SERVER_ID + " = '"+serverId+"'",
                                    null
                            );

                            ContentValues eventValues = new ContentValues();
                            eventValues.put(DataBaseTables.EVENTS_APPLICATION_STATUS, "PARTICIPANT");
                            eventValues.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS, numbOfParticipants + 1);

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                    eventValues,
                                    DataBaseTables.SERVER_ID + " = "+eventId,
                                    null
                            );


                        }else{
                            Log.i("CONFIRM EVENT INVITATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("CONFIRM EVENT INVITATION", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        Button removeButton = view.findViewById(R.id.delete_button);

        removeButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Rejecting Invitation...");
                mProgressDialog.setMessage("Please wait while we are processing your rejection.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Cursor alCursor = context.getContentResolver().query(
                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                        new String[]{DataBaseTables.APPLICATION_LIST_REQUEST_ID},
                        DataBaseTables.SERVER_ID + " = '"+serverId+"'",
                        null,
                        null
                );

                alCursor.moveToFirst();
                Long requestId = alCursor.getLong(alCursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_REQUEST_ID));

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.declineApplicationForEvent(authHeader,requestId);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("REJECT EVENT INVITATION", "CALL TO SERVER SUCCESSFUL");

                            context.getContentResolver().delete(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                    DataBaseTables.SERVER_ID + " = '"+serverId+"'",
                                    null
                            );

                            ContentValues eventValues = new ContentValues();
                            eventValues.put(DataBaseTables.EVENTS_APPLICATION_STATUS, "NONE");

                            context.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                    eventValues,
                                    DataBaseTables.SERVER_ID + " = "+eventId,
                                    null
                            );


                        }else{
                            Log.i("REJECT EVENT INVITATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }

                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("REJECT EVENT INVITATION", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }
}