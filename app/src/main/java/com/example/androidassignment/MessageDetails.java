package com.example.androidassignment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * Activity to display MessageFragment on phones
 */
public class MessageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        // Get data from intent
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        long id = intent.getLongExtra("id", -1);

        // Pass to fragment
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putLong("id", id);

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.messageDetailContainer, fragment);
        ft.commit();
    }
}
