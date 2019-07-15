package com.example.sppb_tfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

public class User {
    long id;
    String name;
    String score;
    String testDate;
    /*String[] scores;
    String[] testDates;*/

    private User(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        name = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.NAME));
        score = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.SCORE));
        testDate = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.TEST_DATE));
/*      scores = cursor.getString(cursor.getColumnIndex("scores")).split(";");
        testDates = cursor.getString(cursor.getColumnIndex("test_dates")).split(";")*/
    };

    public User(String name) {
        setName(name);
        setScore("0");
        setTestDate("11/07/2019");
    }

    public User(String name, String score, String testDate) {
        setName(name);
        this.score = score;
        this.testDate = testDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setTestDate (String testDate) {
        this.testDate = testDate;
    }

    public String getLastDate() {
        return "10/07/2019";
    }



    public static ArrayList<User> getUsersList(Context context) {
        ArrayList<User> listUser = new ArrayList<>();
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(true, "USERS", new String[]{"_id", UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.SCORE, UsersDB.UserEntry.TEST_DATE},
                null, null, null, null, "name", null);


        while (cursor.moveToNext()) {
            listUser.add(new User(cursor));
        }

        cursor.close();
        db.close();
        return listUser;
    }

    public static int getUsersCount(Context context) {
        int count = 0;
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(true, "USERS", new String[]{"_id", UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.SCORE, UsersDB.UserEntry.TEST_DATE},
                null, null, null, null, "name", null);


        if(cursor.moveToFirst()){
            while (cursor.moveToNext()) {
                count++;
            }
        }

        cursor.close();
        db.close();
        return count;
    }

    public static User getUser(Context context, long id){
        User user = null;
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String where ="id = " + String.valueOf(id);
        Cursor cursor = db.query(true, "USERS", new String[]{"id", "name", "scores", "test_dates"},
                where, null, null, null, "name", null);

        if(cursor.moveToFirst())
            user = new User(cursor);

        cursor.close();
        db.close();
        return user;
    }

    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(UsersDB.UserEntry.NAME,this.name);
        values.put(UsersDB.UserEntry.SCORE, this.score);
        values.put(UsersDB.UserEntry.TEST_DATE, this.testDate);

/*        if(this.scores!=null) {
            StringBuilder listScores = new StringBuilder();
            StringBuilder listTestDates = new StringBuilder();
            for(int i =0;i<this.scores.length;i++) {
                listScores.append(this.scores[i]);
                listTestDates.append(this.testDates[i]);

                if(i < this.scores.length-1){
                    listScores.append(";");
                    listTestDates.append(";");
                }
            }
            values.put("listScores", listScores.toString());
            values.put("listTestDates", listTestDates.toString());
        }*/

        LocalSQLiteOpenHelper helper = new
                LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        this.id=db.insert("USERS", null, values);
        db.close();
    }

    public void update(Context context) {
        ContentValues values = new ContentValues();
        values.put("name", this.name);

       /* if(this.scores!=null) {
            StringBuilder listScores = new StringBuilder();
            StringBuilder listTestDates = new StringBuilder();
            for(int i =0;i<this.scores.length;i++) {
                listScores.append(this.scores[i]);
                listTestDates.append(this.testDates[i]);

                if(i < this.scores.length-1){
                    listScores.append(";");
                    listTestDates.append(";");
                }
            }
            values.put("listScores", listScores.toString());
            values.put("listTestDates", listTestDates.toString());
        }*/

        /*String whereClause = "id=" + String.valueOf(this.id);
        LocalSQLiteOpenHelper helper = new
                LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();*/
        /*db.update("USERS", values, whereClause, null);
        db.close();*/
    }

    public void delete(Context context) {


        String whereClause = "_id=?" ;
        String[] whereArgs = new String[1];
        whereArgs[0] = String.valueOf(this.id);
        LocalSQLiteOpenHelper helper = new
                LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("USERS", whereClause,whereArgs);
        db.close();
    }
}
