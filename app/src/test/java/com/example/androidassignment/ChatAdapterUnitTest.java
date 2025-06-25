package com.example.androidassignment;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ChatAdapterUnitTest {

    private ChatWindow.ChatAdapter adapter;

    @Before
    public void setUp() {
        adapter = new ChatWindow.ChatAdapter();
    }

    @Test
    public void testSetAndGetMessages() {
        ArrayList<String> testMessages = new ArrayList<>();
        testMessages.add("Hi");
        testMessages.add("Hello");
        testMessages.add("What's up?");

        adapter.setMessages(testMessages);

        assertEquals(3, adapter.getCount());
        assertEquals("Hi", adapter.getItem(0));
        assertEquals("What's up?", adapter.getItem(2));
    }

    @Test
    public void testEmptyList() {
        adapter.setMessages(new ArrayList<>());

        assertEquals(0, adapter.getCount());
    }
}
