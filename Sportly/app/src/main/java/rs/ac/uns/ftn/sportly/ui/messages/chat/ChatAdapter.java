package rs.ac.uns.ftn.sportly.ui.messages.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rs.ac.uns.ftn.sportly.R;
import rs.ac.uns.ftn.sportly.model.Message;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;

    private Context context;
    private Integer image;
    private List<Message> messages;

    ChatAdapter (Context c, Integer i, List<Message> m){
        this.context = c;
        this.image = i;
        this.messages = m;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ChatViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        holder.show_message.setText(messages.get(position).getText());
        holder.profile_image.setImageResource(image);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;

        public ChatViewHolder(View v) {
            super(v);

            show_message = v.findViewById(R.id.show_message);
            profile_image = v.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).isMine()){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
