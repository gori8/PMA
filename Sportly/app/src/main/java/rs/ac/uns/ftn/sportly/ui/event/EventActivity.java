package rs.ac.uns.ftn.sportly.ui.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventRequestDTO;
import rs.ac.uns.ftn.sportly.dto.EventRequestRequest;
import rs.ac.uns.ftn.sportly.dto.FriendshipDTO;
import rs.ac.uns.ftn.sportly.dto.ParticipationDTO;
import rs.ac.uns.ftn.sportly.model.Event;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.event.application_list.ApplicationListActivity;
import rs.ac.uns.ftn.sportly.ui.event.edit_event.EditEventActivity;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class EventActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Long eventId;
    private TextView tvLocation;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvPeople;
    private TextView tvDate;
    private TextView tvPrice;
    private TextView tvCreator;
    private TextView tvDescription;
    private RoundedImageView imageView;
    private String eventName;
    private ProgressDialog mProgressDialog;
    private String eventStatus;
    private Button applyButton;
    private Button cancelButton;
    Integer numOfParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Event Overview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        eventId = getIntent().getLongExtra("eventId",0);

        getSupportLoaderManager().initLoader(0, null, this);



        tvLocation = findViewById(R.id.location);
        tvName = findViewById(R.id.name);
        tvTime = findViewById(R.id.time);
        tvPeople = findViewById(R.id.people);
        tvDate = findViewById(R.id.date);
        tvPrice = findViewById(R.id.price);
        tvCreator = findViewById(R.id.creator);
        tvDescription = findViewById(R.id.description);
        imageView = findViewById(R.id.eventSportsfieldImage);

        Button applicationListButton = findViewById(R.id.applicationListButton);
        applicationListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ApplicationListActivity.class);
                intent.putExtra("eventId",eventId);
                intent.putExtra("eventName", eventName);
                startActivity(intent);
            }
        });

        String jwt = JwtTokenUtils.getJwtToken(this);
        String authHeader = "Bearer " + jwt;

        cancelButton = findViewById(R.id.cancelButton);
        applyButton = findViewById(R.id.applyButton);

        //APPLY BUTTON
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(EventActivity.this);
                mProgressDialog.setTitle("Applying for the event...");
                mProgressDialog.setMessage("Please wait while we are processing your application.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                EventRequestRequest request = new EventRequestRequest();
                request.setEventId(eventId);

                Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.applyForEvent(authHeader,request);

                call.enqueue(new Callback<EventRequestDTO>() {
                    @Override
                    public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                        if (response.code() == 200){

                            Log.i("APPLY FOR EVENT", "CALL TO SERVER SUCCESSFUL");

                            ContentValues values = new ContentValues();
                            values.put(DataBaseTables.EVENTS_APPLICATION_STATUS,"QUEUE");

                            EventActivity.this.getContentResolver().update(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                    values,
                                    DataBaseTables.SERVER_ID + " = "+eventId,
                                    null);

                            ContentValues valuesApplicationList = new ContentValues();
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EVENT_SERVER_ID,eventId);
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID,JwtTokenUtils.getUserId(EventActivity.this));
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_FIRST_NAME,response.body().getUserFirstName());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_LAST_NAME,response.body().getUserLastName());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_USERNAME,response.body().getUserEmail());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_EMAIL,response.body().getUserEmail());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_STATUS,"QUEUE");
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_REQUEST_ID,response.body().getId());
                            valuesApplicationList.put(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID, 0);
                            valuesApplicationList.put(DataBaseTables.SERVER_ID,"E"+eventId+"A"+JwtTokenUtils.getUserId(EventActivity.this));

                            getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_APPLICATION_LIST),
                                    valuesApplicationList);


                            applyButton.setVisibility(View.GONE);
                            cancelButton.setVisibility(View.VISIBLE);
                        }else{
                            Log.i("APPLY FOR EVENT", "CALL TO SERVER RESPONSE CODE: "+response.code());
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                        Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                        Log.i("APPLY FOR EVENT", "CALL TO SERVER FAILED");
                        mProgressDialog.dismiss();
                    }
                });

            }
        });

        //CANCEL BUTTON
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(EventActivity.this);
                mProgressDialog.setTitle("Canceling Application...");
                mProgressDialog.setMessage("Please wait while we are processing your canceling.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                String serverId ="E"+eventId+"A"+JwtTokenUtils.getUserId(EventActivity.this);

                Cursor cursor = getContentResolver().query(
                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                        new String[]{DataBaseTables.APPLICATION_LIST_REQUEST_ID,DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID},
                        DataBaseTables.SERVER_ID+" = '"+serverId+"'",
                        null,
                        null
                );

                cursor.moveToFirst();

                //PARTICIPATING USER CANCELING
                if(eventStatus.equals("PARTICIPANT")){
                    Long participationId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID));
                    Call<ParticipationDTO> call = SportlyServerServiceUtils.sportlyServerService.deleteParticipationForEvent(authHeader,participationId);

                    call.enqueue(new Callback<ParticipationDTO>() {
                        @Override
                        public void onResponse(Call<ParticipationDTO> call, Response<ParticipationDTO> response) {
                            if (response.code() == 200){

                                Log.i("DELETE PARTICIPATION", "CALL TO SERVER SUCCESSFUL");

                                getContentResolver().delete(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                        DataBaseTables.APPLICATION_LIST_PARTICIPATION_ID+" = "+participationId,
                                        null
                                );

                                numOfParticipants = numOfParticipants - 1;

                                ContentValues values = new ContentValues();
                                values.put(DataBaseTables.EVENTS_APPLICATION_STATUS,"NONE");
                                values.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,numOfParticipants);

                                getContentResolver().update(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                        values,
                                        DataBaseTables.SERVER_ID+" = "+eventId,
                                        null
                                );

                                cancelButton.setVisibility(View.GONE);
                                applyButton.setVisibility(View.VISIBLE);
                            }else{
                                Log.i("DELETE PARTICIPATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                            }
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ParticipationDTO> call, Throwable t) {
                            Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                            Log.i("DELETE PARTICIPATION", "CALL TO SERVER FAILED");
                            mProgressDialog.dismiss();
                        }
                    });
                }

                //QUEUEING USER CANCELING
                else if(eventStatus.equals("QUEUE")){
                    Long requestId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_REQUEST_ID));;
                    Call<EventRequestDTO> call = SportlyServerServiceUtils.sportlyServerService.deleteApplicationForEvent(authHeader,requestId);

                    call.enqueue(new Callback<EventRequestDTO>() {
                        @Override
                        public void onResponse(Call<EventRequestDTO> call, Response<EventRequestDTO> response) {
                            if (response.code() == 200){

                                Log.i("DELETE APPLICATION", "CALL TO SERVER SUCCESSFUL");

                                getContentResolver().delete(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_APPLICATION_LIST),
                                        DataBaseTables.APPLICATION_LIST_REQUEST_ID+" = "+requestId,
                                        null
                                );

                                ContentValues values = new ContentValues();
                                values.put(DataBaseTables.EVENTS_APPLICATION_STATUS,"NONE");

                                getContentResolver().update(
                                        Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_EVENTS),
                                        values,
                                        DataBaseTables.SERVER_ID+" = "+eventId,
                                        null
                                );

                                cancelButton.setVisibility(View.GONE);
                                applyButton.setVisibility(View.VISIBLE);
                            }else{
                                Log.i("DELETE APPLICATION", "CALL TO SERVER RESPONSE CODE: "+response.code());
                            }
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<EventRequestDTO> call, Throwable t) {
                            Log.i("REZ", t.getMessage() != null?t.getMessage():"error");
                            Log.i("DELETE APPLICATION", "CALL TO SERVER FAILED");
                            mProgressDialog.dismiss();
                        }
                    });
                }else{
                    Log.i("DELETE PARTICIPATION OR APPLICATION", "USER IS NOT PARTICIPATING OR QUEUEING FOR THIS EVENT");
                    mProgressDialog.dismiss();
                }
            }
        });

        Button editEventButton = findViewById(R.id.editEventButton);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(v.getContext(), EditEventActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("date", date);

                String numberOfPeople = people.split("/")[1].split(" ")[0];

                intent.putExtra("people", numberOfPeople);
                intent.putExtra("price", price);
                intent.putExtra("description", description);
                intent.putExtra("location", location);

                String[] timeArray = time.split("-");

                intent.putExtra("startingTime", timeArray[0].trim());
                intent.putExtra("endingTime", timeArray[1].trim());
                startActivity(intent);*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {



        Uri uri = Uri.parse(SportlyContentProvider.CONTENT_URI+ DataBaseTables.TABLE_EVENTS);
        String selection = DataBaseTables.SERVER_ID+" = "+eventId;
        String[] allColumns = {
                DataBaseTables.ID,
                DataBaseTables.EVENTS_NAME,
                DataBaseTables.EVENTS_CURR,
                DataBaseTables.EVENTS_NUMB_OF_PPL,
                DataBaseTables.EVENTS_PRICE,
                DataBaseTables.EVENTS_DESCRIPTION,
                DataBaseTables.EVENTS_DATE_FROM,
                DataBaseTables.EVENTS_DATE_TO,
                DataBaseTables.EVENTS_TIME_FROM,
                DataBaseTables.EVENTS_TIME_TO,
                DataBaseTables.EVENTS_SPORTS_FILED_ID,
                DataBaseTables.EVENTS_APPLICATION_STATUS,
                DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,
                DataBaseTables.SERVER_ID,
                DataBaseTables.EVENTS_CREATOR,
        };



        return new CursorLoader(this, uri,
                allColumns, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();

        Long locationId = data.getLong(data.getColumnIndex(DataBaseTables.EVENTS_SPORTS_FILED_ID));

        String[] sportFieldsColumns = {
                DataBaseTables.ID,
                DataBaseTables.SPORTSFIELDS_NAME,
        };

        String sportsfirledsSelection = DataBaseTables.ID + " = " + locationId;

        Cursor sportsfieldData = getContentResolver().query(Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),sportFieldsColumns,sportsfirledsSelection,null,null);

        sportsfieldData.moveToFirst();

        String location = sportsfieldData.getString(sportsfieldData.getColumnIndex(DataBaseTables.SPORTSFIELDS_NAME));

        String isCreator = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_APPLICATION_STATUS));

        String name = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_NAME));
        this.eventName = name;

        String timeFrom  = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_TIME_FROM));
        String timeTo  = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_TIME_TO));
        String time = timeFrom + " - " + timeTo;

        Integer numOfPpl = data.getInt(data.getColumnIndex(DataBaseTables.EVENTS_NUMB_OF_PPL));
        numOfParticipants = data.getInt(data.getColumnIndex(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS));
        String people = numOfParticipants + "/" + numOfPpl;

        String date = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_DATE_FROM));

        Double price = data.getDouble(data.getColumnIndex(DataBaseTables.EVENTS_PRICE));

        String creator = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_CREATOR));

        String description = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_DESCRIPTION));

        eventStatus = data.getString(data.getColumnIndexOrThrow(DataBaseTables.EVENTS_APPLICATION_STATUS));

        if(isCreator.equals("CREATOR")){
            LinearLayout creatorButtons = findViewById(R.id.creatorButtons);
            creatorButtons.setVisibility(View.VISIBLE);
        }else{
            if(eventStatus.equals("PARTICIPANT") || eventStatus.equals("QUEUE")){
                applyButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
            }else{
                cancelButton.setVisibility(View.GONE);
                applyButton.setVisibility(View.VISIBLE);
            }

            LinearLayout applierButtons = findViewById(R.id.applierButtons);
            applierButtons.setVisibility(View.VISIBLE);
        }



        tvLocation.setText(location);
        tvName.setText(name);
        tvTime.setText(time);
        tvPeople.setText(people);
        tvDate.setText(date);
        tvPrice.setText(price.toString());
        tvCreator.setText(creator);
        tvDescription.setText(description);

        //TODO: Use picasso to get Image from firebase

        // imageView.setImageResource(image);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        tvLocation.setText(null);
        tvName.setText(null);
        tvTime.setText(null);
        tvPeople.setText(null);
        tvDate.setText(null);
        tvPrice.setText(null);
        tvCreator.setText(null);
        tvDescription.setText(null);

    }
}
