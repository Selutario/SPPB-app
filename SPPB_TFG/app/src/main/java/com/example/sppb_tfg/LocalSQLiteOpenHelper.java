package com.example.sppb_tfg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalSQLiteOpenHelper extends SQLiteOpenHelper {

    public LocalSQLiteOpenHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + UsersDB.UserEntry.TABLE_NAME + " ("
                + UsersDB.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UsersDB.UserEntry.NAME + " TEXT NOT NULL,"
                + UsersDB.UserEntry.BALANCE_SCORE + " INTEGER,"
                + UsersDB.UserEntry.SPEED_SCORE+ " INTEGER,"
                + UsersDB.UserEntry.CHAIR_SCORE + " INTEGER,"
                + UsersDB.UserEntry.TEST_DATE + " TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
