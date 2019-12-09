package com.example.sppb_tfg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
* Class of the user object, to save data such as the name and score obtained in each test.
*/
public class User {
    long id;

    String name;
    int age;
    double weight;
    double height;

    ArrayList<String> balanceScore = new ArrayList<>();
    ArrayList<String>  speedScore = new ArrayList<>();
    ArrayList<String>  chairScore = new ArrayList<>();
    ArrayList<String>  testDate = new ArrayList<>();
    ArrayList<String>  averageSpeed = new ArrayList<>();


    private User(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));

        name = cursor.getString(cursor.getColumnIndex(UsersDB.UserEntry.NAME));
        age = cursor.getInt(cursor.getColumnIndex(UsersDB.UserEntry.AGE));
        weight = cursor.getDouble(cursor.getColumnIndex(UsersDB.UserEntry.WEIGHT));
        height = cursor.getInt(cursor.getColumnIndex(UsersDB.UserEntry.HEIGHT));

        String prueba = cursor.getString(cursor.getColumnIndex(
                UsersDB.UserEntry.BALANCE_SCORE));
        String[] b_scores = prueba.split(";");
        String[] s_scores = cursor.getString(cursor.getColumnIndex(
                UsersDB.UserEntry.SPEED_SCORE)).split(";");
        String[] c_scores = cursor.getString(cursor.getColumnIndex(
                UsersDB.UserEntry.CHAIR_SCORE)).split(";");
        String[] d_scores = cursor.getString(cursor.getColumnIndex(
                UsersDB.UserEntry.TEST_DATE)).split(";");
        String[] as_scores = cursor.getString(cursor.getColumnIndex(
                UsersDB.UserEntry.AVERAGE_SPEED)).split(";");

        for(int i=0; i<b_scores.length; i++){
            balanceScore.add(b_scores[i]);
            speedScore.add(s_scores[i]);
            chairScore.add(c_scores[i]);
            testDate.add(d_scores[i]);
            averageSpeed.add(as_scores[i]);
        }
    };

    public User(String name) {
        setName(name);
        setBalanceScore(0);
        setSpeedScore(0);
        setChairScore(0);
        setAverageSpeed(0);

        setTestDateToday();
    }

    public User(String name, int balanceScore, int speedScore, int chairScore, String testDate) {
        setName(name);
        setBalanceScore(balanceScore);
        setSpeedScore(speedScore);
        setChairScore(chairScore);
        setAverageSpeed(0);

        setTestDateToday();
    }

    public User(String name, String age, String weight, String height){
        setName(name);
        setAge(age);
        setWeight(weight);
        setHeight(height);

        setBalanceScore(0);
        setSpeedScore(0);
        setChairScore(0);
        setAverageSpeed(0);

        setTestDateToday();
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

    public int getAge() {
        return age;
    }

    public void setAge(String age) {
        if (age.isEmpty()){
            this.age = 0;
        } else {
            this.age = Integer.valueOf(age);
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        if (weight.isEmpty()) {
            this.weight = 0;
        } else {
            this.weight = Double.valueOf(weight);
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(String height) {
        if (height.isEmpty()) {
            this.weight = 0;
        } else {
            this.height = Integer.valueOf(height);
        }
    }

    public int getBalanceScore() {
        return getBalanceScore(0);
    }

    public int getBalanceScore(int pos) {
        return Integer.valueOf(balanceScore.get(pos));
    }

    public void setBalanceScore(int balanceScore) {
        int score = 0;
        if(balanceScore >= 0) {
            score = balanceScore;
        } else {
            score = getBalanceScore();
        }

        if (testNotPerformed()) {
            this.balanceScore.remove(0);
        }
        this.balanceScore.add(0, String.valueOf(score));
    }

    public int getSpeedScore() {
        return getSpeedScore(0);
    }

    public int getSpeedScore(int pos) {
        return Integer.valueOf(speedScore.get(pos));
    }

    public void setSpeedScore(int speedScore) {
        int score = 0;
        if(speedScore >= 0) {
            score = speedScore;
        } else {
            score = getSpeedScore();
        }

        if (testNotPerformed()) {
            this.speedScore.remove(0);
        }
        this.speedScore.add(0, String.valueOf(score));
    }

    public int getChairScore() {
        return getChairScore(0);
    }

    public int getChairScore(int pos) {
        return Integer.valueOf(chairScore.get(pos));
    }

    public void setChairScore(int chairScore) {
        int score = 0;
        if(chairScore >= 0) {
            score = chairScore;
        } else {
            score = getChairScore();
        }

        if (testNotPerformed()) {
            this.chairScore.remove(0);
        }
        this.chairScore.add(0, String.valueOf(score));
    }

    public double getAverageSpeed() {
        return getAverageSpeed(0);
    }

    public double getAverageSpeed(int pos) {
        return Double.valueOf(averageSpeed.get(pos));
    }

    public void setAverageSpeed(double averageSpeed) {
        double score = 0;
        if(averageSpeed >= 0) {
            score = averageSpeed;
        } else {
            score = getAverageSpeed();
        }

        if (testNotPerformed()) {
            this.averageSpeed.remove(0);
        }
        this.averageSpeed.add(0, String.valueOf(score));
    }

    public String getTestDate() {
        return getTestDate(0);
    }

    public String getTestDate(int pos) {
        return testDate.get(pos);
    }

    public void setTestDate(String testDate) {
        this.testDate.add(0, testDate);
    }

    public void setTestDateToday() {
        String dateInString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        setTestDate(dateInString);
    }

    public int getScore() {
        return getBalanceScore() + getSpeedScore() + getChairScore();
    }

    public int getScore(int pos) {
        return getBalanceScore(pos) + getSpeedScore(pos) + getChairScore(pos);
    }

    public int getHistorySize() {
        Log.d("ADAPTER", "TAMAÑO: " + averageSpeed.size());
        return averageSpeed.size();
    }

    public boolean testNotPerformed() {
        if (getHistorySize() == 1) {
            if (getScore() == 0) {
                return true;
            }
        }
        return false;
    }

    // Get full list with all saved users
    public static ArrayList<User> getUsersList(Context context) {
        ArrayList<User> listUser = new ArrayList<>();
        LocalSQLiteOpenHelper helper = new LocalSQLiteOpenHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(true, "USERS", new String[]{"_id",
                        UsersDB.UserEntry.NAME,
                        UsersDB.UserEntry.AGE,
                        UsersDB.UserEntry.WEIGHT,
                        UsersDB.UserEntry.HEIGHT,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE,
                        UsersDB.UserEntry.AVERAGE_SPEED},
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
                        UsersDB.UserEntry.AGE,
                        UsersDB.UserEntry.WEIGHT,
                        UsersDB.UserEntry.HEIGHT,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE,
                        UsersDB.UserEntry.AVERAGE_SPEED},
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
                        UsersDB.UserEntry.AGE,
                        UsersDB.UserEntry.WEIGHT,
                        UsersDB.UserEntry.HEIGHT,
                        UsersDB.UserEntry.BALANCE_SCORE,
                        UsersDB.UserEntry.SPEED_SCORE,
                        UsersDB.UserEntry.CHAIR_SCORE,
                        UsersDB.UserEntry.TEST_DATE,
                        UsersDB.UserEntry.AVERAGE_SPEED},
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
        values.put(UsersDB.UserEntry.AGE, this.age);
        values.put(UsersDB.UserEntry.WEIGHT, this.weight);
        values.put(UsersDB.UserEntry.HEIGHT, this.height);

        if(this.balanceScore != null){
            StringBuilder listBalance = new StringBuilder();
            StringBuilder listSpeed = new StringBuilder();
            StringBuilder listChair = new StringBuilder();
            StringBuilder listAverage = new StringBuilder();
            StringBuilder listDates = new StringBuilder();

            for(int i=0; i < this.balanceScore.size(); i++){
                listBalance.append(this.balanceScore.get(i));
                listBalance.append(";");
                listSpeed.append(this.speedScore.get(i));
                listSpeed.append(";");
                listChair.append(this.chairScore.get(i));
                listChair.append(";");
                listAverage.append(this.averageSpeed.get(i));
                listAverage.append(";");
                listDates.append(this.testDate.get(i));
                listDates.append(";");
            }

            values.put(UsersDB.UserEntry.BALANCE_SCORE, listBalance.toString());
            values.put(UsersDB.UserEntry.SPEED_SCORE, listSpeed.toString());
            values.put(UsersDB.UserEntry.CHAIR_SCORE, listChair.toString());
            values.put(UsersDB.UserEntry.TEST_DATE, listDates.toString());
            values.put(UsersDB.UserEntry.AVERAGE_SPEED, listAverage.toString());
        }

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
        values.put(UsersDB.UserEntry.AGE, this.age);
        values.put(UsersDB.UserEntry.WEIGHT, this.weight);
        values.put(UsersDB.UserEntry.HEIGHT, this.height);

        if(this.balanceScore != null){
            StringBuilder listBalance = new StringBuilder();
            StringBuilder listSpeed = new StringBuilder();
            StringBuilder listChair = new StringBuilder();
            StringBuilder listAverage = new StringBuilder();
            StringBuilder listDates = new StringBuilder();

            for(int i=0; i < this.balanceScore.size(); i++){
                listBalance.append(this.balanceScore.get(i));
                listBalance.append(";");
                listSpeed.append(this.speedScore.get(i));
                listSpeed.append(";");
                listChair.append(this.chairScore.get(i));
                listChair.append(";");
                listAverage.append(this.averageSpeed.get(i));
                listAverage.append(";");
                listDates.append(this.testDate.get(i));
                listDates.append(";");
            }

            values.put(UsersDB.UserEntry.BALANCE_SCORE, listBalance.toString());
            values.put(UsersDB.UserEntry.SPEED_SCORE, listSpeed.toString());
            values.put(UsersDB.UserEntry.CHAIR_SCORE, listChair.toString());
            values.put(UsersDB.UserEntry.TEST_DATE, listDates.toString());
            values.put(UsersDB.UserEntry.AVERAGE_SPEED, listAverage.toString());
        }

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
