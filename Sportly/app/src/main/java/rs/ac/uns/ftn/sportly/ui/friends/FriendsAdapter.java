package rs.ac.uns.ftn.sportly.ui.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import rs.ac.uns.ftn.sportly.R;

public class FriendsAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;

    FriendsAdapter (Context c, List<String> n, List<Integer> i){
        super(c, R.layout.friend_item, R.id.friend_name, n);
        this.context = c;
        this.names = n;
        this.images = i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.friend_item, parent, false);
        ImageView imageView = row.findViewById(R.id.friend_image);
        TextView textView = row.findViewById(R.id.friend_name);

        imageView.setImageResource(images.get(position));
        textView.setText(names.get(position));

        return row;
    }
}

