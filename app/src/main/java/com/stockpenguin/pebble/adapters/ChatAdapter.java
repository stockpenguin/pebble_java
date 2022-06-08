package com.stockpenguin.pebble.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.activities.MessagingActivity;
import com.stockpenguin.pebble.fragments.SearchDialog;
import com.stockpenguin.pebble.models.Chat;
import com.stockpenguin.pebble.utils.SearchDialogDataHolder;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public LinkedList<Chat> chats;
    private LayoutInflater inflater;
    private AdapterView.OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context, LinkedList<Chat> chats) {
        this.chats = chats;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        String photoUrl = chats.get(position).getPhotoUrl();
        String sender = chats.get(position).getSender();
        String message = chats.get(position).getMessage();

        Glide.with(holder.itemView)
                .load(photoUrl)
                .into(holder.chatInfoPfp);

        holder.chatSenderTextView.setText(sender);
        holder.chatMessageTextView.setText(message);
        System.out.println("onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView chatInfoPfp;
        TextView chatSenderTextView;
        TextView chatMessageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatInfoPfp = itemView.findViewById(R.id.chatInfoPfp);
            chatSenderTextView = itemView.findViewById(R.id.chatSenderTextView);
            chatMessageTextView = itemView.findViewById(R.id.chatMessageTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("onClick");
            SearchDialogDataHolder dataHolder = new SearchDialogDataHolder();
            dataHolder.getPhotoUrls().addOnListChangedListener(() -> {});
            dataHolder.getUsernames().addOnListChangedListener(() -> {});
            dataHolder.getUids().addOnListChangedListener(() -> {});

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            Query query = usersRef.orderByChild("displayName").equalTo(chats.getLast().getSender());
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        dataHolder.addPhotoUrl((String) ds.child("photoUrl").getValue());
                        dataHolder.addUid(ds.getKey());
                        dataHolder.addUsername((String) ds.child("displayName").getValue());
                        System.out.println("username added");
                        System.out.println((String)ds.child("displayName").getValue());
                    }

                    openMessagingActivity(view.getContext(), dataHolder);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            };

            query.addListenerForSingleValueEvent(valueEventListener);


            System.out.println(dataHolder.getUsername());
        }

        private void openMessagingActivity(Context context, SearchDialogDataHolder dataHolder) {
            Intent i = new Intent(itemView.getContext(), MessagingActivity.class);
            i.putExtra("data", dataHolder);
            context.startActivity(i);
        }
    }
}
