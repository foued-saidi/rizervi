package com.example.rizervi;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String receiverId, receiverName, senderId;
    private FirebaseFirestore db;
    private RecyclerView rvMessages;
    private MessageAdapter adapter;
    private List<ChatMessage> messageList;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiverId = getIntent().getStringExtra("RECEIVER_ID");
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");
        senderId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(receiverName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        ImageButton btnSend = findViewById(R.id.btnSendMessage);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, senderId);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        // Query messages where (sender=S and receiver=R) OR (sender=R and receiver=S)
        // Note: For simplicity, we use a single collection. In production, a composite index is needed.
        db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        messageList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            if ((msg.getSenderId().equals(senderId) && msg.getReceiverId().equals(receiverId)) ||
                                (msg.getSenderId().equals(receiverId) && msg.getReceiverId().equals(senderId))) {
                                messageList.add(msg);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        rvMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage(senderId, receiverId, text);
        db.collection("messages").add(msg).addOnSuccessListener(documentReference -> {
            etMessage.setText("");
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
