package com.example.sppb_tfg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalSQLiteOpenHelper extends SQLiteOpenHelper {

    public LocalSQLiteOpenHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    // Creates local data base
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UsersDB.UserEntry.TABLE_NAME + " ("
                + UsersDB.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsersDB.UserEntry.NAME + " TEXT NOT NULL,"
                + UsersDB.UserEntry.AGE + " INTEGER,"
                + UsersDB.UserEntry.WEIGHT + " REAL,"
                + UsersDB.UserEntry.HEIGHT + " INTEGER,"
                + UsersDB.UserEntry.BALANCE_SCORE + " TEXT,"
                + UsersDB.UserEntry.SPEED_SCORE + " TEXT,"
                + UsersDB.UserEntry.CHAIR_SCORE + " TEXT,"
                + UsersDB.UserEntry.TEST_DATE + " TEXT, "
                + UsersDB.UserEntry.AVERAGE_SPEED + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
