package com.example.androidassignment;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TestToolbarInstrumentedTest {

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.androidassignment", appContext.getPackageName());
    }

    @Test
    public void testToolbarLaunch() {
        ActivityScenario.launch(TestToolbar.class);
        assertTrue(true); // placeholder; real UI assertions use Espresso
    }
}
