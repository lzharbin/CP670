package com.example.androidassignment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    private static final String TAG = "ChatWindow";

    private ListView chatView;
    private EditText messageText;
    private Button sendButton;
    private ArrayList<String> messages;
    private ChatAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        // Show back arrow in the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind UI components
        chatView = findViewById(R.id.chatView);
        messageText = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.sendButton);

        // Initialize messages and adapter
        messages = new ArrayList<>();
        messageAdapter = new ChatAdapter(this);
        messageAdapter.setMessages(messages);
        chatView.setAdapter(messageAdapter);

        // Send button click event
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageText.getText().toString().trim();
                if (!text.isEmpty()) {
                    messages.add(text);
                    messageAdapter.notifyDataSetChanged(); // Refresh list
                    messageText.setText(""); // Clear input
                    Log.i(TAG, "Message sent: " + text);
                }
            }
        });
    }

    // Handle ActionBar back button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Public static inner class for adapter (testable)
    public static class ChatAdapter extends ArrayAdapter<String> {
        private Context context;
        private ArrayList<String> messages;

        // Constructor for production use (with Context)
        public ChatAdapter(Context ctx) {
            super(ctx, 0);
            this.context = ctx;
            this.messages = new ArrayList<>();
        }

        // Constructor for unit testing (no Context required)
        public ChatAdapter() {
            super(null, 0);
            this.context = null;
            this.messages = new ArrayList<>();
        }

        public void setMessages(ArrayList<String> messages) {
            this.messages = messages;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (context == null) return null; // Skip rendering in unit test

            LayoutInflater inflater = LayoutInflater.from(context);
            View result;

            // Alternate row layout by position
            if (position % 2 == 0) {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            } else {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }

            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));

            return result;
        }
    }
}
