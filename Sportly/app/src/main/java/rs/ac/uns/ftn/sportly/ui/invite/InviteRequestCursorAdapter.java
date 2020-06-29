package rs.ac.uns.ftn.sportly.ui.invite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;
import rs.ac.uns.ftn.sportly.database.SportlyContentProvider;

public class InviteRequestCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public InviteRequestCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
        super(context,layout,c,from,to);
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

        TextView eventNameText = (TextView)view.findViewById(R.id.list_event_name);
        TextView timeFromText = (TextView)view.findViewById(R.id.list_event_time_from);
        TextView timeToText = (TextView)view.findViewById(R.id.list_event_time_to);
        TextView pplText = (TextView)view.findViewById(R.id.list_event_ppl);
        TextView participantsText = (TextView)view.findViewById(R.id.list_event_participants);
        ImageView imageView = view.findViewById(R.id.list_event_image);

        int eventIdIndex=cursor.getColumnIndexOrThrow(DataBaseTables.SERVER_ID);
        int nameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NAME);
        int timeFromIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_FROM);
        int timeToIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_TO);
        int pplIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PPL);
        int participantsIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PARTICIPANTS);

        Long eventId = cursor.getLong(eventIdIndex);
        Long sportsFieldId = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_SPORTS_FILED_ID));

        Cursor spCursor = context.getContentResolver().query(
                Uri.parse(SportlyContentProvider.CONTENT_URI+DataBaseTables.TABLE_SPORTSFIELDS),
                new String[]{DataBaseTables.SPORTSFIELDS_CATEGORY},
                DataBaseTables.SERVER_ID + " = " + sportsFieldId,
                null,
                null
        );
        spCursor.moveToFirst();
        String category = spCursor.getString(spCursor.getColumnIndexOrThrow(DataBaseTables.SPORTSFIELDS_CATEGORY));

        if(category.equals("basketball")){
            imageView.setImageResource(R.drawable.ic_basketball_event);
        }else if(category.equals("football")){
            imageView.setImageResource(R.drawable.ic_football_event);
        }else if(category.equals("tennis")){
            imageView.setImageResource(R.drawable.ic_tennis_event);
        }

        eventNameText.setText(cursor.getString(nameIndex));
        pplText.setText(cursor.getString(pplIndex));
        timeFromText.setText(cursor.getString(timeFromIndex));
        timeToText.setText(cursor.getString(timeToIndex));
        participantsText.setText(cursor.getString(participantsIndex));
    }
}