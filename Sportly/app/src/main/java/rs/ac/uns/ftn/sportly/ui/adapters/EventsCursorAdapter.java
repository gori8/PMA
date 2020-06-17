package rs.ac.uns.ftn.sportly.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Date;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;

public class EventsCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;
    private final LayoutInflater inflater;

    public EventsCursorAdapter(Context context,int layout, Cursor c,String[] from,int[] to) {
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
        TextView descriptionText = (TextView)view.findViewById(R.id.list_event_description);

        int nameIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NAME);
        int timeFromIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_FROM);
        int timeToIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_TIME_TO);
        int pplIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_NUMB_OF_PPL);
        int descriptionIndex=cursor.getColumnIndexOrThrow(DataBaseTables.EVENTS_DESCRIPTION);

        eventNameText.setText(cursor.getString(nameIndex));
        pplText.setText(cursor.getString(pplIndex));
        descriptionText.setText(cursor.getString(descriptionIndex));
        timeFromText.setText(cursor.getString(timeFromIndex));
        timeToText.setText(cursor.getString(timeToIndex));
    }
}
