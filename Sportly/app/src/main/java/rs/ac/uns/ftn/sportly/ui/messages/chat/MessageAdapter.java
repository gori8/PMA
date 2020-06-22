package rs.ac.uns.ftn.sportly.ui.messages.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.model.firebase.Messages;
import rs.ac.uns.ftn.sportly.utils.JwtTokenUtils;

/**
 * Created by AkshayeJH on 24/07/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context context;

    public MessageAdapter(List<Messages> mMessageList, Context context) {

        this.mMessageList = mMessageList;
        this.context = context;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if(viewType == MSG_TYPE_LEFT) {
            v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_left, parent, false);
        }else{
            v=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false);
        }


        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        //public TextView displayName;
        public TextView timeText;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            //displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = (TextView) view.findViewById(R.id.time_text_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                //viewHolder.displayName.setText(name);

                Picasso.get().load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }

        Date date = new Date(c.getTime());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",
                Locale.getDefault());
        String dateText = sfd.format(date);

        viewHolder.timeText.setText(dateText);


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }




    @Override
    public int getItemViewType(int position) {
        if(mMessageList.get(position).getFrom().equals(JwtTokenUtils.getUserId(context).toString())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }


}
