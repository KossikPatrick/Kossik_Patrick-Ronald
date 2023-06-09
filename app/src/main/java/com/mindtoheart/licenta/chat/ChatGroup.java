package com.mindtoheart.licenta.chat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mindtoheart.licenta.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatGroup extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private ListView messageListView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Conversații cu utilizatorii");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messageListView = findViewById(R.id.messageListView);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up the chat adapter and message listener
        ChatAdapter chatAdapter = new ChatAdapter(this);
        messageListView.setAdapter(chatAdapter);
        setChatListener(chatAdapter);

        // Set the click listener for the send button
        sendButton.setOnClickListener(v -> sendMessage());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void setChatListener(ChatAdapter chatAdapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference chatCollectionRef = db.collection("chat");

        Query query = chatCollectionRef.orderBy("timestamp", Query.Direction.ASCENDING);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            List<ChatMessage> chatMessages = new ArrayList<>();

            if (value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot document : value) {
                    String sender = document.getString("sender");
                    String message = document.getString("message");
                    String timestamp = document.getString("timestamp");

                    ChatMessage chatMessage = new ChatMessage(sender, message, timestamp);
                    chatMessages.add(chatMessage);
                }
            }

            chatAdapter.clear();
            chatAdapter.addAll(chatMessages);
            chatAdapter.notifyDataSetChanged();


            messageListView.post(() -> messageListView.setSelection(chatAdapter.getCount() - 1));
        });
    }
    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        String sender = auth.getCurrentUser().getDisplayName();
        String timestamp = getCurrentTimestamp();

        if (!messageText.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(sender, messageText, timestamp);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference chatCollectionRef = db.collection("chat");

            chatCollectionRef.add(chatMessage)
                    .addOnSuccessListener(documentReference -> {
                        messageEditText.setText("");
                    });
        }
    }
    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}