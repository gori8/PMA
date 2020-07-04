package rs.ac.uns.ftn.sportly.ui.event.create_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventDTO;
import rs.ac.uns.ftn.sportly.dto.SyncDataDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.ui.time_picker.TimePickerFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class CreateEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private int selectedTimePicker;


    private TextView tvLocation;
    private TextInputEditText etName;
    private TextInputEditText etStartingTime;
    private TextInputEditText etEndingTime;
    private TextInputEditText etDate;
    private TextInputEditText etPrice;
    private TextInputEditText etDescription;
    private TextInputEditText etCurrency;
    private TextInputEditText etNumOfPpl;
    private Button confirmBtn;
    private ProgressDialog mProgressDialog;

    private Long sportsFieldServerId;
    private Long sportsFieldId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Create event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sportsFieldServerId = getIntent().getExtras().getLong("sportsFieldServerId");
        sportsFieldId = getIntent().getExtras().getLong("sportsFieldId");
        String category = getIntent().getStringExtra("category");

        String locationText = getIntent().getExtras().getString("location");


        confirmBtn = findViewById(R.id.confirmButton);

        tvLocation = findViewById(R.id.location);
        tvLocation.setText(locationText);

        etName = findViewById(R.id.name);
        etStartingTime = findViewById(R.id.startingTime);
        etEndingTime = findViewById(R.id.endingTime);
        etDate = findViewById(R.id.date);
        etPrice = findViewById(R.id.price);
        etDescription = findViewById(R.id.description);
        etCurrency = findViewById(R.id.currency);
        etNumOfPpl = findViewById(R.id.num_of_people);


        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @SneakyThrows
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(CreateEventActivity.this);
                mProgressDialog.setTitle("Creating Event...");
                mProgressDialog.setMessage("Please wait while we are processing your create action.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");

                EventDTO eventDTO = new EventDTO();

                eventDTO.setName(etName.getText().toString());
                eventDTO.setDateFrom(etDate.getText().toString());
                eventDTO.setDateTo(etDate.getText().toString());
                eventDTO.setTimeFrom(etStartingTime.getText().toString());
                eventDTO.setTimeTo(etEndingTime.getText().toString());
                eventDTO.setCurr(etCurrency.getText().toString());
                eventDTO.setPrice(Double.parseDouble(etPrice.getText().toString()));
                eventDTO.setDescription(etDescription.getText().toString());
                eventDTO.setNumbOfPpl(Short.parseShort(etNumOfPpl.getText().toString()));
                eventDTO.setSportsFieldId(sportsFieldServerId);

                String jwt = JwtTokenUtils.getJwtToken(CreateEventActivity.this);
                String authHeader = "Bearer " + jwt;

                Call<EventDTO> call = SportlyServerServiceUtils.sportlyServerService.createEvent(authHeader,eventDTO);

                call.enqueue(new Callback<EventDTO>() {


                    @Override
                    public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                        if(response.code()==201){

                            EventDTO respEventDTO = response.body();

                            ContentValues valuesEvent = new ContentValues();
                            valuesEvent.put(DataBaseTables.EVENTS_NAME,respEventDTO.getName());
                            valuesEvent.put(DataBaseTables.EVENTS_CURR,respEventDTO.getCurr());
                            valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PPL,respEventDTO.getNumbOfPpl());
                            valuesEvent.put(DataBaseTables.EVENTS_PRICE,respEventDTO.getPrice());
                            valuesEvent.put(DataBaseTables.EVENTS_DESCRIPTION,respEventDTO.getDescription());
                            valuesEvent.put(DataBaseTables.EVENTS_DATE_FROM,respEventDTO.getDateFrom().toString());
                            valuesEvent.put(DataBaseTables.EVENTS_DATE_TO,respEventDTO.getDateTo().toString());
                            valuesEvent.put(DataBaseTables.EVENTS_TIME_FROM,respEventDTO.getTimeFrom().toString());
                            valuesEvent.put(DataBaseTables.EVENTS_TIME_TO,respEventDTO.getTimeTo().toString());
                            valuesEvent.put(DataBaseTables.EVENTS_SPORTS_FILED_ID,sportsFieldId);
                            valuesEvent.put(DataBaseTables.SERVER_ID,respEventDTO.getId());
                            valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,1);
                            valuesEvent.put(DataBaseTables.EVENTS_APPLICATION_STATUS,"CREATOR");
                            valuesEvent.put(DataBaseTables.EVENTS_CREATOR,respEventDTO.getCreator());
                            valuesEvent.put(DataBaseTables.EVENTS_CATEGORY,category);
                            valuesEvent.put(DataBaseTables.EVENTS_CREATOR_ID,JwtTokenUtils.getUserId(CreateEventActivity.this));
                            valuesEvent.put(DataBaseTables.EVENTS_IMAGE_REF,respEventDTO.getImageRef());


                            getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_EVENTS),
                                    valuesEvent);

                            Intent intent = new Intent(CreateEventActivity.this, EventActivity.class);
                            intent.putExtra("eventId", respEventDTO.getId());
                            intent.putExtra("sportsFieldId", sportsFieldId);

                            CreateEventActivity.this.startActivity(intent);


                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<EventDTO> call, Throwable t) {
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

        Button cancelButton = findViewById(R.id.cancelCEButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @SneakyThrows
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        TextView tvLocation = findViewById(R.id.location);

        tvLocation.setText(myIntent.getStringExtra("location"));



        setUpDatePicker();
        setUpTimePicker(etStartingTime);
        setUpTimePicker(etEndingTime);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpDatePicker(){

        final Calendar myCalendar = Calendar.getInstance();

        TextInputEditText edittext= (TextInputEditText) findViewById(R.id.date);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(edittext,myCalendar);
            }

        };

        edittext.setOnClickListener(v -> {

            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });
    }

    private void updateDateLabel(TextInputEditText edittext, Calendar myCalendar){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("sr","RS"));
        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    private void setUpTimePicker(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedTimePicker = v.getId();

                TextInputEditText edittext = findViewById(selectedTimePicker);
                String [] timeArray = edittext.getText().toString().split(":");

                int hour;
                int minute;

                try{
                    hour = Integer.parseInt(timeArray[0]);
                }catch(Exception e){
                    hour = 0;
                }

                try{
                    minute = Integer.parseInt(timeArray[1]);
                }catch(Exception e){
                    minute = 0;
                }

                DialogFragment timePicker = new TimePickerFragment();
                Bundle args = new Bundle();
                args.putInt("hour", hour);
                args.putInt("minute",minute);
                timePicker.setArguments(args);
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextInputEditText edittext = findViewById(selectedTimePicker);

        String hourOfDayString = hourOfDay+"";
        String minuteString = minute+"";

        if(hourOfDayString.length()==1){
            hourOfDayString = "0"+hourOfDayString;
        }

        if(minuteString.length()==1){
            minuteString = "0"+minuteString;
        }

        edittext.setText(hourOfDayString + ":" + minuteString);
    }
}
