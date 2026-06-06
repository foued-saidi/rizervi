package com.example.rizervi;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView rvChatList;
    private ChatUserAdapter adapter;
    private List<User> chatUsers;
    private FirebaseFirestore db;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Toolbar toolbar = findViewById(R.id.toolbarChatList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mes Messages");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        rvChatList = findViewById(R.id.rvChatList);
        chatUsers = new ArrayList<>();
        adapter = new ChatUserAdapter(chatUsers, user -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("RECEIVER_ID", user.getUid());
            intent.putExtra("RECEIVER_NAME", user.getFirstName() + " " + user.getLastName());
            startActivity(intent);
        });

        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        rvChatList.setAdapter(adapter);

        loadChatUsers();
    }

    private void loadChatUsers() {
        db.collection("messages").get().addOnSuccessListener(queryDocumentSnapshots -> {
            Set<String> userIds = new HashSet<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                ChatMessage msg = doc.toObject(ChatMessage.class);
                if (msg.getSenderId().equals(currentUid)) {
                    userIds.add(msg.getReceiverId());
                } else if (msg.getReceiverId().equals(currentUid)) {
                    userIds.add(msg.getSenderId());
                }
            }

            for (String uid : userIds) {
                db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setUid(doc.getId());
                        chatUsers.add(user);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
