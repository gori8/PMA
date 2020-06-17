package rs.ac.uns.ftn.sportly.ui.event.edit_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.time_picker.TimePickerFragment;

public class EditEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private int selectedTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        String name = myIntent.getStringExtra("name");
        String startingTime = myIntent.getStringExtra("startingTime");
        String endingTime = myIntent.getStringExtra("endingTime");
        String people = myIntent.getStringExtra("people");
        String date = myIntent.getStringExtra("date");
        String price = myIntent.getStringExtra("price");
        String description = myIntent.getStringExtra("description");
        String location = myIntent.getStringExtra("location");

        TextInputEditText tvName = findViewById(R.id.name);
        TextInputEditText tvStartingTime = findViewById(R.id.startingTime);
        TextInputEditText tvEndingTime = findViewById(R.id.endingTime);
        TextInputEditText tvPeople = findViewById(R.id.people);
        TextInputEditText tvDate = findViewById(R.id.date);
        TextInputEditText tvPrice = findViewById(R.id.price);
        TextInputEditText tvDescription = findViewById(R.id.description);
        TextView tvLocation = findViewById(R.id.location);

        tvName.setText(name);
        tvStartingTime.setText(startingTime);
        tvEndingTime.setText(endingTime);
        tvPeople.setText(people);
        tvDate.setText(date);
        tvPrice.setText(price);
        tvDescription.setText(description);
        tvLocation.setText(location);

        setUpDatePicker();
        setUpTimePicker(tvStartingTime);
        setUpTimePicker(tvEndingTime);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpDatePicker(){

        final Calendar myCalendar = Calendar.getInstance();

        TextInputEditText edittext= (TextInputEditText) findViewById(R.id.date);

        updateDateLabel(edittext,myCalendar);

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
