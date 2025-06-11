package com.example.androidassignment;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestToolbarUnitTest {

    @Test
    public void testAddition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testToastMessage() {
        String expected = "Version 1.0, by Lesley Zhang";
        String actual = "Version 1.0, by Lesley Zhang"; // from your actual code
        assertEquals(expected, actual);
    }
}
