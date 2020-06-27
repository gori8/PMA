package rs.ac.uns.ftn.sportly.ui.event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.ui.event.application_list.ApplicationListActivity;
import rs.ac.uns.ftn.sportly.ui.event.edit_event.EditEventActivity;

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
                DataBaseTables.EVENTS_CREATOR
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
        Integer numOfParticipants = data.getInt(data.getColumnIndex(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS));
        String people = numOfParticipants + "/" + numOfPpl;

        String date = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_DATE_FROM));

        Double price = data.getDouble(data.getColumnIndex(DataBaseTables.EVENTS_PRICE));

        String creator = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_CREATOR));

        String description = data.getString(data.getColumnIndex(DataBaseTables.EVENTS_DESCRIPTION));



        if(isCreator.equals("CREATOR")){
            LinearLayout creatorButtons = findViewById(R.id.creatorButtons);
            creatorButtons.setVisibility(View.VISIBLE);
        }else{
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
