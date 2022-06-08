package com.stockpenguin.pebble.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.adapters.MessagesAdapter;
import com.stockpenguin.pebble.models.Conversation;
import com.stockpenguin.pebble.models.Message;
import com.stockpenguin.pebble.utils.SearchDialogDataHolder;

import java.math.BigInteger;
import java.util.ArrayList;

public class MessagingActivity extends AppCompatActivity {

    private SearchDialogDataHolder dataHolder;

    /* database reference */
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;

    /* adapter data */
    private Conversation conversation;
    private String firebaseUserUid;

    private ImageButton sendMessageButton;
    private EditText messageEditText;

    public String conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        getWindow().setStatusBarColor(getColor(R.color.pastel_blue));
        recyclerView = findViewById(R.id.messagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageImageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(messageEditText.getText().toString());
                messagesAdapter.notifyDataSetChanged();
            }
        });

        dataHolder = getIntent().getParcelableExtra("data");

        /* initialize reference */
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /* get current user and the user uid */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUserUid = firebaseUser.getUid();

        conversationId = createConversationId(firebaseUserUid, dataHolder.getUid());

        /* check if conversation exists */
        databaseReference.child("conversations").child(conversationId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        createConversation(conversationId);
                        readConversation(conversationId);
                    }
                }
            }
        });

        readConversation(conversationId);
        loadToolbar();
    }
//
    public void createConversation(String conversationId) {

        String photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Conversation conversation =
                new Conversation(
                        conversationId,
                        Conversation.ConversationType.DUO,
                        username,
                        dataHolder.getUsername(),
                        photoUrl,
                        dataHolder.getPhotoUrl());
        conversation.setMessages(new ArrayList<>());

        databaseReference.child("conversations").child(conversationId).setValue(conversation);
    }

    public void sendMessage(String message) {
        if (conversation != null && !message.trim().equals("")) {
            conversation.getMessages().add(new Message(
                    System.currentTimeMillis(),
                    firebaseUserUid,
                    message
            ));
            databaseReference
                    .child("conversations")
                    .child(conversationId)
                    .setValue(conversation);
            messageEditText.setText("");
        }
    }

    public void readConversation(String conversationId) {
        ValueEventListener messagesListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /* conversation data from the database */
                conversation = snapshot.getValue(Conversation.class);
                /* create the messagesAdapter from the constructor */
                messagesAdapter = new MessagesAdapter(getApplicationContext(), conversation, dataHolder.getPhotoUrl());
                /* attatch the messagesAdapter to the RecyclerView */
                recyclerView.setAdapter(messagesAdapter);
                /* notify the adapter that the data set has changed */
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        /* attatch the listener to the database reference of conversations/u1+u2 which is
        a string concatenation
         */
        databaseReference.child("conversations").child(conversationId).addValueEventListener(messagesListener);
    }

    public String createConversationId(String u1, String u2) {
        String u1Nums = "";
        String u2Nums = "";
        for (int i = 0; i < u1.length(); i++) {
            if (Character.isDigit(u1.charAt(i))) {
                u1Nums += u1.charAt(i);
            }

            if (Character.isDigit(u2.charAt(i))) {
                u2Nums += u2.charAt(i);
            }
        }

        if (Integer.parseInt(u1Nums) < Integer.parseInt(u2Nums)) {
            return u1+u2;
        }
        return u2+u1;
    }

    public void loadToolbar() {
        /* load pfp */
        Glide.with(this)
                .load(dataHolder.getPhotoUrl())
                .into((ImageView) findViewById(R.id.toolbarCircleImageView));

        /* load txt */
        TextView textView = findViewById(R.id.toolbarTextView);
        textView.setText(dataHolder.getUsername());
    }

}