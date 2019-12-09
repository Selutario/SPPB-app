package com.example.sppb_tfg;

public class Constants {

    // Preferences key name
    public static final String PREFS_NAME = "UserPrefsFile";
    public static final String SELECTED_USER = "selectedUser";
    // ID of each test
    public static final int BALANCE_TEST = 1;
    public static final int GAIT_TEST = 2;
    public static final int CHAIR_TEST = 3;
    // Time between each execution of onSensorChanged functions.
    public static final int ACCE_FILTER_DATA_MIN_TIME = 100; // 100 ms
    // Tags for buttons
    public static final String MUTE = "mute";
    public static final String UNMUTE = "unmute";
    public static final String UNABLE = "unable";
    public static final String REPEAT = "repeat";
    // Database name and version
    public static String DB_NAME = "sppbUsers.db";
    public static int DB_VERSION = 1;
}
