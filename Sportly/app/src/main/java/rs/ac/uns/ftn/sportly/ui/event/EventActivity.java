package rs.ac.uns.ftn.sportly.ui.event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.event.application_list.ApplicationListActivity;
import rs.ac.uns.ftn.sportly.ui.event.edit_event.EditEventActivity;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Event Overview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent myIntent = getIntent();
        String location = myIntent.getStringExtra("location");
        String name = myIntent.getStringExtra("name");
        String time = myIntent.getStringExtra("time");
        String people = myIntent.getStringExtra("people");
        String date = myIntent.getStringExtra("date");
        String price = myIntent.getStringExtra("price");
        String creator = myIntent.getStringExtra("creator");
        String description = myIntent.getStringExtra("description");
        Long eventId = myIntent.getLongExtra("eventId",-1);
        int image = myIntent.getIntExtra("imageView",R.drawable.ic_location_on_black_24dp);
        boolean isCreator = myIntent.getBooleanExtra("isCreator",false);

        if(isCreator){
            LinearLayout creatorButtons = findViewById(R.id.creatorButtons);
            creatorButtons.setVisibility(View.VISIBLE);
        }else{
            LinearLayout applierButtons = findViewById(R.id.applierButtons);
            applierButtons.setVisibility(View.VISIBLE);
        }

        TextView tvLocation = findViewById(R.id.location);
        TextView tvName = findViewById(R.id.name);
        TextView tvTime = findViewById(R.id.time);
        TextView tvPeople = findViewById(R.id.people);
        TextView tvDate = findViewById(R.id.date);
        TextView tvPrice = findViewById(R.id.price);
        TextView tvCreator = findViewById(R.id.creator);
        TextView tvDescription = findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);

        tvLocation.setText(location);
        tvName.setText(name);
        tvTime.setText(time);
        tvPeople.setText(people);
        tvDate.setText(date);
        tvPrice.setText(price);
        tvCreator.setText(creator);
        tvDescription.setText(description);
        imageView.setImageResource(image);

        Button applicationListButton = findViewById(R.id.applicationListButton);
        applicationListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ApplicationListActivity.class);
                intent.putExtra("eventId",eventId);
                intent.putExtra("eventName", name);
                startActivity(intent);
            }
        });

        Button editEventButton = findViewById(R.id.editEventButton);
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), EditEventActivity.class);
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
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
