package com.example.androidassignment;

import com.example.androidassignment.ChatWindow.ChatAdapter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ChatAdapterTest {

    private ChatAdapter adapter;

    @Before
    public void setUp() {
        adapter = new ChatAdapter();

        ArrayList<String> mockMessages = new ArrayList<>();
        mockMessages.add("Hello");
        mockMessages.add("World");
        mockMessages.add("!");

        adapter.setMessages(mockMessages);
    }

    @Test
    public void testGetCount() {
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("Hello", adapter.getItem(0));
        assertEquals("World", adapter.getItem(1));
        assertEquals("!", adapter.getItem(2));
    }
}
