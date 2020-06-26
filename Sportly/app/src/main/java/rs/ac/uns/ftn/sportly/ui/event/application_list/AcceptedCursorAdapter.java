package rs.ac.uns.ftn.sportly.ui.event.application_list;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;

public class AcceptedCursorAdapter  extends SimpleCursorAdapter implements Filterable {

    private Context mContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;
    private DatabaseReference mUserDatabase;

    public AcceptedCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
        super(context,layout,c,new String[]{from[0]},to);
        this.layout=layout;
        this.mContext = context;
        this.inflater=LayoutInflater.from(context);
        this.cr=c;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView nameText = (TextView)view.findViewById(R.id.name);
        ImageView imageView = (ImageView)view.findViewById(R.id.image);

        int firstNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_FIRST_NAME);
        int lastNameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_LAST_NAME);
        int applierIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.APPLICATION_LIST_APPLIER_SERVER_ID);

        nameText.setText(cursor.getString(firstNameIndex)+" "+cursor.getString(lastNameIndex));

        String applierId = cursor.getString(applierIdIndex);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(applierId);

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
    }
}
