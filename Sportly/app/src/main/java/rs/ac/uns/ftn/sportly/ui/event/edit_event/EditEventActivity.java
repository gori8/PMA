package rs.ac.uns.ftn.sportly.ui.event.edit_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import rs.ac.uns.ftn.sportly.R;

public class EditEventActivity extends AppCompatActivity {

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

        TextInputEditText tvName = findViewById(R.id.name);
        TextInputEditText tvStartingTime = findViewById(R.id.startingTime);
        TextInputEditText tvEndingTime = findViewById(R.id.endingTime);
        TextInputEditText tvPeople = findViewById(R.id.people);
        TextInputEditText tvDate = findViewById(R.id.date);
        TextInputEditText tvPrice = findViewById(R.id.price);
        TextInputEditText tvDescription = findViewById(R.id.description);

        tvName.setText(name);
        tvStartingTime.setText(startingTime);
        tvEndingTime.setText(endingTime);
        tvPeople.setText(people);
        tvDate.setText(date);
        tvPrice.setText(price);
        tvDescription.setText(description);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
