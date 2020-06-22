package rs.ac.uns.ftn.sportly.ui.messages;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.database.DataBaseTables;

public class FriendsCursorRecyclerAdapter extends RecyclerView.Adapter<FriendsCursorRecyclerAdapter.FriendsViewHolder> {


    // Because RecyclerView.Adapter in its current form doesn't natively
        // support cursors, we wrap a CursorAdapter that will do all the job
        // for us.
        CursorAdapter mCursorAdapter;

        Context mContext;

        public FriendsCursorRecyclerAdapter(Context context, Cursor c) {

            mContext = context;

            mCursorAdapter = new CursorAdapter(mContext, c, 0) {

                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout, parent, false);


                    String date = "22.06.2020.";

                    TextView userStatusView = (TextView) view.findViewById(R.id.user_single_status);
                    userStatusView.setText(date);

                    String name = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_FIRST_NAME));

                    System.out.println("FRIEND NAME AKA IME: "+name);

                    TextView userNameView = (TextView) view.findViewById(R.id.user_single_name);
                    userNameView.setText(name);


                    CircleImageView userImageView = (CircleImageView) view.findViewById(R.id.user_single_image);
                    String thumb_image = "wtv";
                    Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);


                    ImageView userOnlineView = (ImageView) view.findViewById(R.id.user_single_online_icon);


                    userOnlineView.setVisibility(View.VISIBLE);


                    //TODO :  have to check in Firebase Database
                    /*if (online_status.equals("true")) {

                        userOnlineView.setVisibility(View.VISIBLE);

                    } else {

                        userOnlineView.setVisibility(View.INVISIBLE);

                    }*/


                    return view;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    String date = "22.06.2020.";

                    TextView userStatusView = (TextView) view.findViewById(R.id.user_single_status);
                    userStatusView.setText(date);

                    String name = cursor.getString(cursor.getColumnIndex(DataBaseTables.FRIENDS_FIRST_NAME));

                    System.out.println("FRIEND NAME AKA IME: "+name);

                    TextView userNameView = (TextView) view.findViewById(R.id.user_single_name);
                    userNameView.setText(name);


                    CircleImageView userImageView = (CircleImageView) view.findViewById(R.id.user_single_image);
                    String thumb_image = "wtv";
                    Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);


                    ImageView userOnlineView = (ImageView) view.findViewById(R.id.user_single_online_icon);


                    userOnlineView.setVisibility(View.VISIBLE);


                    //TODO :  have to check in Firebase Database
                    /*if (online_status.equals("true")) {

                        userOnlineView.setVisibility(View.VISIBLE);

                    } else {

                        userOnlineView.setVisibility(View.INVISIBLE);

                    }*/
                }
            };
        }


        @Override
        public int getItemCount() {
            return mCursorAdapter.getCount();
        }

        @Override
        public void onBindViewHolder(FriendsCursorRecyclerAdapter.FriendsViewHolder holder, int position) {
            // Passing the binding operation to cursor loader
            mCursorAdapter.getCursor().moveToPosition(position); //EDITED: added this line as suggested in the comments below, thanks :)
            mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

        }

        @Override
        public FriendsCursorRecyclerAdapter.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Passing the inflater job to the cursor-adapter
            View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
            return new FriendsCursorRecyclerAdapter.FriendsViewHolder(v);
        }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }




    }
}
