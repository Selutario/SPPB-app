package com.example.sppb_tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class TestActivity extends FragmentActivity {

    private ConstraintLayout cl_info;
    private int mCurrentTest;
    private boolean full_test = false;
    public boolean firstUse = false;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mCurrentTest = getIntent().getIntExtra("test_number", mCurrentTest);

        settings = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.getBoolean("FirstUse", true)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FirstUse", false);
            editor.commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startTest();
    }

    public void startTest() {

        switch (mCurrentTest) {
            case 0:
                boolean animate;
                full_test = animate = true;
                mCurrentTest = 1;
            case Constants.BALANCE_TEST:
                if (settings.getBoolean("FirstUseBalance", true)){
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseBalance", false);
                    editor.commit();
                    slider_activity(Constants.BALANCE_TEST);
                }
                BalanceFragment balanceFragment = new BalanceFragment();
                openFragment(balanceFragment, false);
                break;

            case Constants.GAIT_TEST:
                if (settings.getBoolean("FirstUseGait", true)){
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseGait", false);
                    editor.commit();
                    slider_activity(Constants.GAIT_TEST);
                }
                GaitFragment gaitFragment = new GaitFragment();
                openFragment(gaitFragment, full_test);
                break;

            case Constants.CHAIR_TEST:
                if (settings.getBoolean("FirstUseChair", true)){
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("FirstUseChair", false);
                    editor.commit();
                    slider_activity(Constants.CHAIR_TEST);
                }
                ChairFragment chairFragment= new ChairFragment();
                openFragment(chairFragment, full_test);
                break;
        }
    }

    private  void openFragment(Fragment fragment, boolean animate) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(animate) {
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);

        }
        transaction.replace(R.id.test_placeHolder, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    public void fragmentTestCompleted() {
        mCurrentTest = mCurrentTest +1;

        if (full_test) {
            if (mCurrentTest < 4) {
                startTest();
            } else {
                onBackPressed();
            }
        } else {
            onBackPressed();
        }
    }

    public void slider_activity(int test){
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra("test", test);
        startActivity(intent);
    }

}
