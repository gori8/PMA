package rs.ac.uns.ftn.sportly.ui.event.edit_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;
import rs.ac.uns.ftn.sportly.dto.EventDTO;
import rs.ac.uns.ftn.sportly.service.SportlyServerServiceUtils;
import rs.ac.uns.ftn.sportly.ui.event.EventActivity;
import rs.ac.uns.ftn.sportly.ui.event.create_event.CreateEventActivity;
import rs.ac.uns.ftn.sportly.ui.time_picker.TimePickerFragment;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

public class EditEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

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

    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        eventId = getIntent().getLongExtra("eventId",-1);


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

                mProgressDialog = new ProgressDialog(EditEventActivity.this);
                mProgressDialog.setTitle("Creating Event...");
                mProgressDialog.setMessage("Please wait while we are processing your create action.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                EventDTO eventDTO = new EventDTO();

                eventDTO.setId(eventId);
                eventDTO.setName(etName.getText().toString());
                eventDTO.setDateFrom(etDate.getText().toString());
                eventDTO.setDateTo(etDate.getText().toString());
                eventDTO.setTimeFrom(etStartingTime.getText().toString());
                eventDTO.setTimeTo(etEndingTime.getText().toString());
                eventDTO.setCurr(etCurrency.getText().toString());
                eventDTO.setPrice(Double.parseDouble(etPrice.getText().toString()));
                eventDTO.setDescription(etDescription.getText().toString());
                eventDTO.setNumbOfPpl(Short.parseShort(etNumOfPpl.getText().toString()));
                //eventDTO.setSportsFieldId(sportsFieldServerId);

                String jwt = JwtTokenUtils.getJwtToken(EditEventActivity.this);
                String authHeader = "Bearer " + jwt;

                Call<EventDTO> call = SportlyServerServiceUtils.sportlyServerService.editEvent(authHeader,eventDTO);

                call.enqueue(new Callback<EventDTO>() {


                    @Override
                    public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                        if(response.code()==200){

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
                            //valuesEvent.put(DataBaseTables.EVENTS_SPORTS_FILED_ID,sportsFieldId);
                            valuesEvent.put(DataBaseTables.SERVER_ID,respEventDTO.getId());
                            valuesEvent.put(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS,respEventDTO.getNumOfParticipants());
                            valuesEvent.put(DataBaseTables.EVENTS_APPLICATION_STATUS,"CREATOR");
                            valuesEvent.put(DataBaseTables.EVENTS_CREATOR,respEventDTO.getCreator());



                            getContentResolver().insert(
                                    Uri.parse(SportlyContentProvider.CONTENT_URI + DataBaseTables.TABLE_EVENTS),
                                    valuesEvent);

                            Intent intent = new Intent(EditEventActivity.this, EventActivity.class);
                            intent.putExtra("eventId", respEventDTO.getId());

                            EditEventActivity.this.startActivity(intent);


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
        String name = myIntent.getStringExtra("name");
        String startingTime = myIntent.getStringExtra("startingTime");
        String endingTime = myIntent.getStringExtra("endingTime");
        String num_of_people = myIntent.getStringExtra("num_of_people");
        String date = myIntent.getStringExtra("date");
        String currency = myIntent.getStringExtra("currency");
        String price = myIntent.getStringExtra("price");
        String description = myIntent.getStringExtra("description");
        String location = myIntent.getStringExtra("location");


        etName.setText(name);
        etStartingTime.setText(startingTime);
        etEndingTime.setText(endingTime);
        etNumOfPpl.setText(num_of_people);
        etDate.setText(date);
        etCurrency.setText(currency);
        etPrice.setText(price);
        etDescription.setText(description);
        tvLocation.setText(location);

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
