package com.example.sppb_tfg;

import android.provider.BaseColumns;

public class UsersDB {


    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME ="USERS";

        public static final String NAME = "name";
        public static final String BALANCE_SCORE = "balance_score";
        public static final String SPEED_SCORE = "speed_score";
        public static final String CHAIR_SCORE = "chair_score";
        public static final String TEST_DATE = "test_date";
    }
}
