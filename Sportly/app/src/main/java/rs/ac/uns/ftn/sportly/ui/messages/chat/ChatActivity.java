package rs.ac.uns.ftn.sportly.ui.messages.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.model.Message;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent myIntent = getIntent();
        String personUsername = myIntent.getStringExtra("person_username");

        String name;
        Integer image;
        List<Message> messages = new ArrayList<>();

        if(personUsername.equals("Milan Škrbić")) {
            name = "Milan Škrbić";
            image = R.drawable.milan_skrbic;

            messages.add(new Message("Hoces na basket u petak?", false));
            messages.add(new Message("Vazi, mozemo se prijaviti u petak.", true));
        }
        else if (personUsername.equals("Stevan Vulić")){
            name = "Stevan Vulić";
            image = R.drawable.stevan_vulic;

            messages.add(new Message("Cao :)", true));
            messages.add(new Message("Pozdrav!", false));
        }else{
            name = "Igor Antolović";
            image = R.drawable.igor_antolovic;

            messages.add(new Message("Gde si? :)", true));
            messages.add(new Message("Evo me, sta ima?", false));
        }

        TextView textViewName = toolbar.findViewById(R.id.name);
        textViewName.setText(name);

        ImageView imageView = toolbar.findViewById(R.id.profile_image);
        imageView.setImageResource(image);

        ChatAdapter adapter = new ChatAdapter(ChatActivity.this, image, messages);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
