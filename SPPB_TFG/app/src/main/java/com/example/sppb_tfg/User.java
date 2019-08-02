package com.example.sppb_tfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/*
* Class of the user object, to save data such as the name and score obtained in each test.
*/
public class User {
    long id;
    String name;
    int balanceScore;
    int speedScore;
    int chairScore;
    String testDate;
    /*String[] scores;
    String[] testDates;*/

    private User(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        name = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.NAME));
        balanceScore = cursor.getInt(cursor.getColumnIndex(UsersDB.UserEntry.BALANCE_SCORE));
        speedScore = cursor.getInt(cursor.getColumnIndex(UsersDB.UserEntry.SPEED_SCORE));
        chairScore = cursor.getInt(cursor.getColumnIndex(UsersDB.UserEntry.CHAIR_SCORE));
        testDate = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.TEST_DATE));
/*      scores = cursor.getString(cursor.getColumnIndex("scores")).split(";");
        testDates = cursor.getString(cursor.getColumnIndex("test_dates")).split(";")*/
    };

    public User(String name) {
        setName(name);
        setBalanceScore(0);
        setSpeedScore(0);
        setChairScore(0);
        setTestDate("11/07/2019");
    }

    public User(String name, int balanceScore, int speedScore, int chairScore, String testDate) {
        setName(name);
        setBalanceScore(balanceScore);
        setSpeedScore(speedScore);
        setChairScore(chairScore);
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

    public int getBalanceScore() {
        return balanceScore;
    }

    public void setBalanceScore(int balanceScore) {
        this.balanceScore = balanceScore;
    }

    public int getSpeedScore() {
        return speedScore;
    }

    public void setSpeedScore(int speedScore) {
        this.speedScore = speedScore;
    }

    public int getChairScore() {
        return chairScore;
    }

    public void setChairScore(int chairScore) {
        this.chairScore = chairScore;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public int getScore() {
        return balanceScore + speedScore + chairScore;
    }

    // Get full list with all saved users
    public static ArrayList<User> getUsersList(Context context) {
        ArrayList<User> listUser = new ArrayList<>();
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(true, "USERS", new String[]{"_id",
                        UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE},
                null, null, null, null, "name", null);


        while (cursor.moveToNext()) {
            listUser.add(new User(cursor));
        }

        cursor.close();
        db.close();
        return listUser;
    }

    // Get number of saved users
    public static int getUsersCount(Context context) {
        int count = 0;
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(true, "USERS", new String[]{"_id",
                        UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE},
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

    // Get an user by its ID number
    public static User getUser(Context context, long id){
        User user = null;
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String where ="_id = " + String.valueOf(id);
        Cursor cursor = db.query(true, "USERS", new String[]{"_id",
                        UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE},
                where, null, null, null, "name", null);

        if(cursor.moveToFirst())
            user = new User(cursor);

        cursor.close();
        db.close();
        return user;
    }

    // Insert new user
    public void insert(Context context) {
        ContentValues values = new ContentValues();
        values.put(UsersDB.UserEntry.NAME,this.name);
        values.put(UsersDB.UserEntry.BALANCE_SCORE, this.balanceScore);
        values.put(UsersDB.UserEntry.SPEED_SCORE, this.speedScore);
        values.put(UsersDB.UserEntry.CHAIR_SCORE, this.chairScore);
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

    // Update user
    public void update(Context context) {
        ContentValues values = new ContentValues();
        values.put(UsersDB.UserEntry.NAME,this.name);
        values.put(UsersDB.UserEntry.BALANCE_SCORE, this.balanceScore);
        values.put(UsersDB.UserEntry.SPEED_SCORE, this.speedScore);
        values.put(UsersDB.UserEntry.CHAIR_SCORE, this.chairScore);
        values.put(UsersDB.UserEntry.TEST_DATE, this.testDate);

        String whereClause = "_id=?" ;
        String[] whereArgs = new String[1];
        whereArgs[0] = String.valueOf(this.id);
        LocalSQLiteOpenHelper helper = new
                LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update("USERS", values, whereClause, whereArgs);
        db.close();
    }

    // Delete user
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
