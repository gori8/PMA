package rs.ac.uns.ftn.sportly.ui.event.application_list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.messages.chat.ChatActivity;
import rs.ac.uns.ftn.sportly.ui.user_profile.UserProfileActivity;

public class QueueAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;

    QueueAdapter (Context c, List<String> n, List<Integer> i){
        super(c, R.layout.queue_item, R.id.name, n);
        this.context = c;
        this.names = n;
        this.images = i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.queue_item, parent, false);
        ImageView imageView = row.findViewById(R.id.image);
        TextView textViewName = row.findViewById(R.id.name);

        imageView.setImageResource(images.get(position));
        textViewName.setText(names.get(position));


        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String name = "";
                String surname = "";
                String username = "";
                String email = "";
                int photoUrl = 0;

                String nameFromList = names.get(position);
                if(nameFromList.equals("Milan Škrbić")){
                    name = "Milan";
                    surname = "Skrbić";
                    username = "shekrba";
                    email = "milan@gmail.com";
                    photoUrl = R.drawable.milan_skrbic;
                } else if(nameFromList.equals("Igor Antolović")){
                    name = "Igor";
                    surname = "Antolović";
                    username = "gori8";
                    email = "igor@gmail.com";
                    photoUrl = R.drawable.igor_antolovic;
                } else if(nameFromList.equals("Stevan Vulić")){
                    name = "Stevan";
                    surname = "Vulić";
                    username = "Vul4";
                    email = "stevan@gmail.com";
                    photoUrl = R.drawable.stevan_vulic;
                }

                goToUserProfileActivity(name, surname, username, email, photoUrl);
            }
        });

        return row;
    }

    public void goToUserProfileActivity(String name, String surname, String username, String email, int photoUrl){
        ApplicationListActivity appListActivity = (ApplicationListActivity) context;
        Intent intent = new Intent(appListActivity, UserProfileActivity.class);

        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("email", email);
        intent.putExtra("photoUrl", photoUrl);

        appListActivity.startActivity(intent);
    }
}
