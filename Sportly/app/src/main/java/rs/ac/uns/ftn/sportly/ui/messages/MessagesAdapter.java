package rs.ac.uns.ftn.sportly.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import rs.ac.uns.ftn.sportly.MainActivity;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.ui.messages.chat.ChatActivity;

public class MessagesAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;
    private List<String> info;
    private List<String> time;

    MessagesAdapter (Context c, List<String> n, List<Integer> i, List<String> in, List<String> t){
        super(c, R.layout.messages_item, R.id.messages_name, n);
        this.context = c;
        this.names = n;
        this.images = i;
        this.info = in;
        this.time = t;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.messages_item, parent, false);
        ImageView imageView = row.findViewById(R.id.messages_image);
        TextView textViewName = row.findViewById(R.id.messages_name);
        TextView textViewInfo = row.findViewById(R.id.messages_last_sent);
        TextView textViewTime = row.findViewById(R.id.messages_time);

        imageView.setImageResource(images.get(position));
        textViewName.setText(names.get(position));
        textViewInfo.setText(info.get(position));
        textViewTime.setText(time.get(position));

        final int i = position;

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) context;

                Intent intent = new Intent(mainActivity, ChatActivity.class);
                intent.putExtra("person_username", names.get(i));
                mainActivity.startActivity(intent);
            }
        });

        return row;
    }
}
