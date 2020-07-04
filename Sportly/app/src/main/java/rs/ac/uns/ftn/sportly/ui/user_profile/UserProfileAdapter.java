package rs.ac.uns.ftn.sportly.ui.user_profile;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.UserRatingDTO;

@Setter
@Getter
public class UserProfileAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<UserRatingDTO> ratings;
    private DatabaseReference mUserDatabase;

    public UserProfileAdapter (Context c, List<UserRatingDTO> r, List<String> names){
        super(c, R.layout.ratings_item,R.id.ratings_name,names);
        this.context = c;
        this.ratings = r;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.ratings_item, parent, false);

        ImageView imageView = row.findViewById(R.id.ratings_image);
        TextView textViewName = row.findViewById(R.id.ratings_name);
        TextView textViewDescription = row.findViewById(R.id.ratings_description);
        RatingBar ratingBar = row.findViewById(R.id.ratings_ratingBar);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ratings.get(position).getCreatorId().toString());

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        textViewName.setText(ratings.get(position).getCreatorFirstName() + " " +ratings.get(position).getCreatorLastName());
        textViewDescription.setText(ratings.get(position).getComment());
        ratingBar.setRating(ratings.get(position).getValue());

        return row;
    }
}
