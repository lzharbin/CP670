package com.example.androidassignment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    // Make cursor a class variable
    private Cursor cursor;

    // Boolean to indicate if tablet layout is loaded
    private boolean isTabletLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Detect tablet layout
        View frameLayout = findViewById(R.id.detailFrame);
        isTabletLayout = (frameLayout != null);

        // Bind UI
        chatView = findViewById(R.id.chatView);
        messageText = findViewById(R.id.messageText);
        sendButton = findViewById(R.id.sendButton);

        // Open DB
        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Query DB
        cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE},
                null, null, null, null, null
        );

        // Load messages
        messages = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int msgColIndex = cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE);
            while (!cursor.isAfterLast()) {
                String msg = cursor.getString(msgColIndex);
                messages.add(msg);
                cursor.moveToNext();
            }
        }

        messageAdapter = new ChatAdapter(this, cursor);
        messageAdapter.setMessages(messages);
        chatView.setAdapter(messageAdapter);

        // Message click listener
        chatView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMessage = messageAdapter.getItem(position);

            if (isTabletLayout) {
                // Tablet: use fragment with activity reference
                Bundle bundle = new Bundle();
                bundle.putString("message", selectedMessage);
                bundle.putLong("id", id);

                MessageFragment fragment = new MessageFragment(ChatWindow.this);
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detailFrame, fragment)
                        .commit();
            } else {
                // Phone: start activity for result
                Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                intent.putExtra("message", selectedMessage);
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);
            }
        });

        // Send button
        sendButton.setOnClickListener(view -> {
            String text = messageText.getText().toString().trim();
            if (!text.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, text);
                db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);

                messages.add(text);
                messageAdapter.notifyDataSetChanged();
                messageText.setText("");
            }
        });
    }

    public void deleteMessageById(long id) {
        int rows = db.delete(ChatDatabaseHelper.TABLE_NAME,
                ChatDatabaseHelper.KEY_ID + " = ?", new String[]{String.valueOf(id)});
        Log.i(TAG, "Deleted ID " + id + " rows=" + rows);

        // Reload DB
        messages.clear();
        cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            int msgColIndex = cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE);
            while (!cursor.isAfterLast()) {
                String msg = cursor.getString(msgColIndex);
                messages.add(msg);
                cursor.moveToNext();
            }
        }

        messageAdapter.setMessages(messages);
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            long deleteId = data.getLongExtra("deleteId", -1);
            if (deleteId != -1) {
                deleteMessageById(deleteId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
        if (db != null && db.isOpen()) db.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class ChatAdapter extends ArrayAdapter<String> {
        private Context context;
        private ArrayList<String> messages;
        private Cursor cursor;

        public ChatAdapter(Context ctx, Cursor cursor) {
            super(ctx, 0);
            this.context = ctx;
            this.messages = new ArrayList<>();
            this.cursor = cursor;
        }

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
        public long getItemId(int position) {
            if (cursor != null && cursor.moveToPosition(position)) {
                int idIndex = cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID);
                return cursor.getLong(idIndex);
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (context == null) return null;

            LayoutInflater inflater = LayoutInflater.from(context);
            View result = (position % 2 == 0)
                    ? inflater.inflate(R.layout.chat_row_incoming, null)
                    : inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));

            return result;
        }
    }
}
