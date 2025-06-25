package com.example.androidassignment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatWindowInstrumentedTest {

    @Test
    public void testSendMessage() {
        ActivityScenario.launch(ChatWindow.class);

        onView(withId(R.id.messageText)).perform(typeText("Hello Test!"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

    }
}
