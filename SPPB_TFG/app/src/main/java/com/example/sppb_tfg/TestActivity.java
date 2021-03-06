package com.example.sppb_tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class TestActivity extends FragmentActivity {

    public int balanceScore = -1;
    public int gaitScore = -1;
    public int chairScore = -1;
    public double averageSpeed = -1;
    public DownloadAccData excelData;
    public String markedUserName = "";
    public TextToSpeech tts;
    public boolean isMuted = false;
    public MediaPlayer beep;
    public boolean ttsReady = false;
    public HashMap<String, String> params = new HashMap<>();
    SharedPreferences settings;
    private int mCurrentTest;
    private boolean full_test = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Keep screen on while performing test
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Open selected test in app shortcut
        if ("android.intent.action.full_shortcut".equals(getIntent().getAction())) {
            mCurrentTest = 0;
        } else if ("android.intent.action.balance_shortcut".equals(getIntent().getAction())) {
            mCurrentTest = Constants.BALANCE_TEST;
        } else if ("android.intent.action.gait_shortcut".equals(getIntent().getAction())) {
            mCurrentTest = Constants.GAIT_TEST;
        } else if ("android.intent.action.chair_shortcut".equals(getIntent().getAction())) {
            mCurrentTest = Constants.CHAIR_TEST;
        } else {
            mCurrentTest = getIntent().getIntExtra("test_number", mCurrentTest);

            // Create a matrix of required size depending on the number of tests to perform.
            excelData = new DownloadAccData(TestActivity.this, mCurrentTest == 0);
        }

        settings = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        Long id = settings.getLong(Constants.SELECTED_USER, -1);

        if (id != -1) {
            markedUserName = " (" + User.getUser(this, id).getName() + ")";
        }

        initTTS();
        startTest();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tts.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
        beep.stop();
        beep.release();
    }

    // Open chosen test or all sequentially if full test is chosen.
    public void startTest() {
        switch (mCurrentTest) {
            case 0:
                full_test = true;
                mCurrentTest = 1;
            case Constants.BALANCE_TEST:
                // Show info/instructions if first time user open this test
                if (settings.getBoolean("FirstUseBalance", true)) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseBalance", false);
                    editor.apply();
                    slider_activity(Constants.BALANCE_TEST);
                }

                BalanceFragment balanceFragment = new BalanceFragment();
                openFragment(balanceFragment, false);
                break;

            case Constants.GAIT_TEST:
                // Show info/instructions if first time user open this test
                if (settings.getBoolean("FirstUseGait", true)) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseGait", false);
                    editor.apply();
                    slider_activity(Constants.GAIT_TEST);
                }

                GaitFragment gaitFragment = new GaitFragment();
                openFragment(gaitFragment, full_test);
                break;

            case Constants.CHAIR_TEST:
                // Show info/instructions if first time user open this test
                if (settings.getBoolean("FirstUseChair", true)) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseChair", false);
                    editor.apply();
                    slider_activity(Constants.CHAIR_TEST);
                }

                SelectPositionFragment selectPositionFragment = new SelectPositionFragment();
                openFragment(selectPositionFragment, full_test);
                break;
        }

    }

    // Go to the next test if full test is chosen, or show result if there is no test to be done.
    public void fragmentTestCompleted() {
        mCurrentTest = mCurrentTest + 1;

        if (full_test) {
            if (mCurrentTest < 4) {
                startTest();
            } else if (mCurrentTest < 5) {
                ScoreFragment scoreFragment = new ScoreFragment();
                openFragment(scoreFragment, true);
            } else {
                onBackPressed();
            }
        } else {
            ScoreFragment scoreFragment = new ScoreFragment();
            openFragment(scoreFragment, true);
        }
    }

    // Open fragment with or without animation
    public void openFragment(Fragment fragment, boolean animate) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (animate) {
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        }

        transaction.replace(R.id.test_placeHolder, fragment);
        transaction.commit();
    }

    // Open info/instructions activity
    public void slider_activity(int test) {
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("test", test);
        startActivity(intent);
    }

    // Start Text to speech engine, to voice instructions.
    public void initTTS() {
        // Beep sound
        beep = MediaPlayer.create(this, R.raw.beep);

        // Text to speech
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0) {
                    Toast.makeText(TestActivity.this, "No Engines Installed",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (status == TextToSpeech.SUCCESS) {
                        Locale locale;
                        switch (Locale.getDefault().getCountry()) {
                            case "US":
                                locale = Locale.US;
                                break;
                            case "ES":
                                locale = new Locale("spa", "ES");
                                tts.setSpeechRate(1.5f);
                                break;
                            default:
                                locale = Locale.UK;
                                break;
                        }

                        tts.setLanguage(locale);
                        ttsReady = true;
                    }

                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    protected void readText(String text) {
        if (!isMuted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        }
    }


    public boolean switchMute() {
        tts.stop();
        isMuted = !isMuted;
        return isMuted;
    }

    public int getmCurrentTest() {
        if (full_test) {
            return 0;
        } else {
            return mCurrentTest - 1;
        }
    }

    // Get score of selected test
    public int getScore(int test) {
        int score = 0;

        switch (test) {
            case 1:
                score = balanceScore;
                break;
            case 2:
                score = gaitScore;
                break;
            case 3:
                score = chairScore;
                break;
            default:
                score = balanceScore + gaitScore + chairScore;
                break;
        }

        return score;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }
}
