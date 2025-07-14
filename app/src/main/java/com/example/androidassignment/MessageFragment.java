package com.example.androidassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment for displaying message and delete option
 */
public class MessageFragment extends Fragment {

    private String message;
    private long messageId;

    // Optional reference to ChatWindow (used only on tablet)
    private ChatWindow chatWindow;

    // Custom constructor with ChatWindow reference
    public MessageFragment(ChatWindow activity) {
        this.chatWindow = activity;
    }

    // Default constructor (used by phone)
    public MessageFragment() {
        this.chatWindow = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message_detail, container, false);

        // Get arguments
        Bundle args = getArguments();
        if (args != null) {
            message = args.getString("message");
            messageId = args.getLong("id");
        }

        // Bind views
        TextView messageText = view.findViewById(R.id.messageTextView);
        TextView idText = view.findViewById(R.id.messageIdTextView);
        Button deleteButton = view.findViewById(R.id.deleteMessageButton);

        messageText.setText(message);
        idText.setText("Message ID: " + messageId);

        // Handle delete button
        deleteButton.setOnClickListener(v -> {
            if (chatWindow != null) {
                // Running on tablet
                chatWindow.deleteMessageById(messageId);

                // Remove fragment from tablet layout
                getParentFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commit();
            } else {
                // Running on phone
                if (getActivity() != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("deleteId", messageId);
                    getActivity().setResult(getActivity().RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}
