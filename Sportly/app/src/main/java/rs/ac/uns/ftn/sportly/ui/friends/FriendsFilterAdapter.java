package rs.ac.uns.ftn.sportly.ui.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.dto.FriendDTO;
import rs.ac.uns.ftn.sportly.dto.PeopleDTO;

@Getter
@Setter
public class FriendsFilterAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<PeopleDTO> peopleList;

    FriendsFilterAdapter(Context c, List<PeopleDTO> pl, List<String> names){
        super(c, R.layout.friend_item, R.id.name, names);
        this.context = c;
        this.peopleList = pl;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.friend_item, parent, false);
        TextView textView = row.findViewById(R.id.name);

        textView.setText(peopleList.get(position).getFirstName()+" "+peopleList.get(position).getLastName());

        if(!peopleList.get(position).isFriend()){
            AppCompatImageButton deleteButton = row.findViewById(R.id.imageButton);
            AppCompatImageButton addButton = row.findViewById(R.id.addButton);

            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        }

        return row;
    }
}

