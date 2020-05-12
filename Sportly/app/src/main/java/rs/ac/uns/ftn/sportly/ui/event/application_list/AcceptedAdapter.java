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

public class AcceptedAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;

    AcceptedAdapter (Context c, List<String> n, List<Integer> i){
        super(c, R.layout.queue_item, R.id.name, n);
        this.context = c;
        this.names = n;
        this.images = i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.accepted_item, parent, false);
        ImageView imageView = row.findViewById(R.id.image);
        TextView textViewName = row.findViewById(R.id.name);
        ImageButton imageButton = row.findViewById(R.id.messageButton);

        imageView.setImageResource(images.get(position));
        textViewName.setText(names.get(position));

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ApplicationListActivity appListActivity = (ApplicationListActivity) context;

                Intent intent = new Intent(appListActivity, ChatActivity.class);
                intent.putExtra("person_username", names.get(position));
                appListActivity.startActivity(intent);
            }
        });

        return row;
    }
}
