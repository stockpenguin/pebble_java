package com.stockpenguin.pebble.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.adapters.ChatAdapter;
import com.stockpenguin.pebble.fragments.SearchDialog;
import com.stockpenguin.pebble.models.Chat;
import com.stockpenguin.pebble.models.Conversation;
import com.stockpenguin.pebble.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PebbleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private DatabaseReference database;

    private ImageButton searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pebble);

        /* set color of status bar and navigation bar */
        getWindow().setStatusBarColor(getColor(R.color.pastel_blue));
        getWindow().setNavigationBarColor(getColor(R.color.white));

        /* FirebaseDatabase */
        database = FirebaseDatabase.getInstance().getReference();

        /* initialize views */
        recyclerView = findViewById(R.id.recyclerView);
        searchButton = findViewById(R.id.searchImageButton);

        LinkedList<Chat> chats = new LinkedList<>();

        ChatAdapter adapter = new ChatAdapter(this, chats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (chats.size() > 0) chats.clear();
                    HashMap<String, HashMap<String, Object>> conversations = (HashMap<String, HashMap<String, Object>>) snapshot.getValue();

                    for (Map.Entry<String, HashMap<String, Object>> entry : conversations.entrySet()) {
                        System.out.println(entry.getKey());
                        System.out.println(entry.getValue());
                        HashMap<String, Object> map = entry.getValue();
                        Conversation c = new Conversation();
                        c.setConversationId((String) map.get("conversationId"));
                        c.setConversationType(Conversation.ConversationType.valueOf((String) map.get("conversationType")));

                        ArrayList<HashMap<String, Object>> messagesMap = (ArrayList<HashMap<String, Object>>) map.get("messages");
                        ArrayList<Message> downloadedMessages = new ArrayList<>();

                        if (messagesMap == null) {
                            break;
                        }

                        for (int i = 0; i < messagesMap.size(); i++) {
                            Message m = new Message();
                            m.setSenderUid((String) messagesMap.get(i).get("senderUid"));
                            m.setText((String) messagesMap.get(i).get("text"));
                            m.setTimestamp((Long) messagesMap.get(i).get("timestamp"));
                            downloadedMessages.add(m);
                        }

                        c.setMessages(downloadedMessages);
                        c.setUser1Username((String) map.get("user1Username"));
                        c.setUser1PhotoUrl((String) map.get("user1PhotoUrl"));
                        c.setUser2Username((String) map.get("user2Username"));
                        c.setUser2PhotoUrl((String) map.get("user2PhotoUrl"));

                        String photoUrl;
                        String sender;
                        String message;

                        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        if (c.getUser1Username().equals(displayName)) {
                            /* current user is user1 */
                            photoUrl = c.getUser2PhotoUrl();
                            sender = c.getUser2Username();
                            message = c.getMessages().get(c.getMessages().size() - 1).getText();
                        } else {
                            photoUrl = c.getUser1PhotoUrl();
                            sender = c.getUser1Username();
                            message = c.getMessages().get(c.getMessages().size() - 1).getText();
                        }

                        Chat chat = new Chat(photoUrl, sender, message);
                        chats.add(chat);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database.child("conversations").orderByKey().startAt(getCurrentUserGeneratedId()).addValueEventListener(listener);

        /* set onClickListener for the search button, will open the BottomModalSheet */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private String getCurrentUserGeneratedId() {
        String uid = FirebaseAuth.getInstance().getUid();
        String generatedId = "";
        for (int i = 0; i < uid.length(); i++) {
            if (Character.isDigit(uid.charAt(i))) {
                generatedId += uid.charAt(i);
            }
        }
        return generatedId;
    }

    private void search() {
        SearchDialog dialog = new SearchDialog();
        dialog.show(getSupportFragmentManager(), "SearchDialog");
    }
}