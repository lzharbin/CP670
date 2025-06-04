package com.example.androidassignment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testEmailInputAndLoginButtonClick() {
        // Type text into the EditText
        onView(withId(R.id.editTextEmail))
                .perform(typeText("student@laurier.ca"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.buttonLogin)).perform(click());

        // Optionally: log or toast in MainActivity can confirm transition
    }
}
