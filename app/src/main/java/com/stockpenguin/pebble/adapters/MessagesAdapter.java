package com.stockpenguin.pebble.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.models.Conversation;
import com.stockpenguin.pebble.models.Message;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public LayoutInflater inflater;
    private Conversation conversation;
    private ArrayList<Message> messages;
    private String otherUserPhotoUrl;

    public MessagesAdapter(Context context, Conversation conversation, String otherPhotoUrl) {
        this.inflater = LayoutInflater.from(context);
        this.conversation = conversation;
        if (conversation == null) {
            messages = new ArrayList<>();
        } else {
            messages = conversation.getMessages();
        }
        this.otherUserPhotoUrl = otherPhotoUrl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.receiver_message, parent, false);
                return new ReceivedMessage(view);
            case 1:
                view = inflater.inflate(R.layout.sender_message, parent, false);
                return new SentMessage(view);
        }
        /* should literally never reach here idk what to make it return */
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messages.get(position);

        System.out.println(m);
        switch (holder.getItemViewType()) {
            case 0:
                ReceivedMessage receivedMessage = (ReceivedMessage)holder;

                receivedMessage.setPfp(otherUserPhotoUrl);
                receivedMessage.setText(m.getText());
                break;
            case 1:
                SentMessage sentMessage = (SentMessage)holder;

                sentMessage.setText(m.getText());
        }
    }

    @Override
    public int getItemCount() {
        if (messages == null) return 0;
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!messages.get(position).getSenderUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return 0;
        }
        return 1;
    }

    class ReceivedMessage extends RecyclerView.ViewHolder {
        private CircleImageView pfp;
        private TextView textView;

        public ReceivedMessage(@NonNull View itemView) {
            super(itemView);
            pfp = itemView.findViewById(R.id.receiverPfp);
            textView = itemView.findViewById(R.id.receiverMessageBubble);
        }

        public void setPfp(String url) {
            Glide.with(itemView.getContext())
                    .load(url)
                    .into(pfp);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }

    class SentMessage extends RecyclerView.ViewHolder {
        private TextView textView;

        public SentMessage(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.senderMessageBubble);
        }

       public void setText(String text) {
            textView.setText(text);
       }
    }
}
