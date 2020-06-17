package rs.ac.uns.ftn.sportly.ui.favorites;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import rs.ac.uns.ftn.sportly.R;

public class FavoriteAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> names;
    private List<Integer> images;
    private List<String> descriptions;
    private List<Float> ratings;

    public FavoriteAdapter (Context c, List<String> n, List<Integer> i, List<String> d, List<Float> r){
        super(c, R.layout.favorite_item, R.id.favorite_name, n);
        this.context = c;
        this.names = n;
        this.images = i;
        this.descriptions = d;
        this.ratings = r;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.favorite_item, parent, false);
        ImageView imageView = row.findViewById(R.id.favorite_image);
        TextView textViewName = row.findViewById(R.id.favorite_name);
        TextView textViewDescription = row.findViewById(R.id.favorite_description);
        RatingBar ratingBar = row.findViewById(R.id.favorite_ratingBar);

        imageView.setImageResource(images.get(position));
        textViewName.setText(names.get(position));
        textViewDescription.setText(descriptions.get(position));
        ratingBar.setRating(ratings.get(position));

        return row;
    }
}
