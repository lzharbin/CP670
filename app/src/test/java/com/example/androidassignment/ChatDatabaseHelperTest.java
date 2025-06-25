package com.example.androidassignment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChatDatabaseHelperTest {

    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = new ChatDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @Test
    public void testDatabaseCreatedAndTableExists() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{ChatDatabaseHelper.TABLE_NAME});
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        cursor.close();
    }

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(ChatDatabaseHelper.DATABASE_NAME);
    }
}
