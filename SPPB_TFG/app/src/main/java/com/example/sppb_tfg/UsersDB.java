package com.example.sppb_tfg;

import android.provider.BaseColumns;

public class UsersDB {


    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME ="USERS";

        public static final String NAME = "name";
        public static final String SCORE = "score";
        public static final String TEST_DATE = "test_date";
    }
}
