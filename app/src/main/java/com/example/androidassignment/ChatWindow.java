package com.example.androidassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

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

        // Open the database
        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Query existing messages from the database
        Cursor cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE},
                null, null, null, null, null
        );

        Log.i(TAG, "Cursor’s column count = " + cursor.getColumnCount());
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            Log.i(TAG, "Column " + i + ": " + cursor.getColumnName(i));
        }

        if (cursor.moveToFirst()) {
            int msgColIndex = cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE);
            while (!cursor.isAfterLast()) {
                String msg = cursor.getString(msgColIndex);
                messages.add(msg);
                Log.i(TAG, "SQL MESSAGE: " + msg);
                cursor.moveToNext();
            }
        }

        cursor.close();

        // Send button click event
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageText.getText().toString().trim();
                if (!text.isEmpty()) {
                    messages.add(text);
                    messageAdapter.notifyDataSetChanged();
                    messageText.setText("");
                    Log.i(TAG, "Message sent: " + text);

                    // Insert the message into the database
                    ContentValues values = new ContentValues();
                    values.put(ChatDatabaseHelper.KEY_MESSAGE, text);
                    db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);
                }
            }
        });
    }

    // Close database when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
            Log.i(TAG, "Database closed in onDestroy()");
        }
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
            if (context == null) return null;

            LayoutInflater inflater = LayoutInflater.from(context);
            View result;

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
