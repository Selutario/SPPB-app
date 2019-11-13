package com.example.sppb_tfg;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.TooltipCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import static com.example.sppb_tfg.Constants.ACCE_FILTER_DATA_MIN_TIME;

public class GaitFragment extends Fragment implements SensorEventListener {

    private LinearLayout whole_screen;
    private ConstraintLayout cl_info;
    private TextView test_name;
    private TextView tv_result;
    private TextView tv_result_label;
    private Chronometer chronometer;
    private ImageView iv_person;
    private ImageButton btn_play;
    private ImageButton btn_mute;
    private ImageButton btn_info;
    private ImageButton btn_replay;
    private GradientDrawable drawable;

    private int currentStep = 0;
    private boolean inProgress = false;

    // Accelerometer and time variables specific for gait test
    private final int TIME_THRSHOLD = 2000; // Miliseconds
    private final float CHANGE_THRSHOLD = 0.7f;
    private float max_change = CHANGE_THRSHOLD;
    private float yHistory = 0;
    long diffChanges;
    private long lastChangeTime;

    private double walkingTime = 0;
    private double min_walkingTime = 100;

    SensorManager sensorManager;
    Sensor sensorAcc;
    long lastSaved = System.currentTimeMillis();

    TestActivity testActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, null);
        whole_screen = view.findViewById(R.id.whole_screen);
        cl_info = view.findViewById(R.id.cl_info);
        iv_person = (ImageView) view.findViewById(R.id.iv_person);
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
        tv_result = (TextView) view.findViewById(R.id.tv_result);
        tv_result_label = (TextView) view.findViewById(R.id.tv_result_label);
        chronometer = view.findViewById(R.id.chronometer);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_mute = (ImageButton) view.findViewById(R.id.btn_mute);
        btn_info = (ImageButton) view.findViewById(R.id.imageButton5);
        btn_replay = (ImageButton) view.findViewById(R.id.btn_replay);

        // Set test name and test color on the interface
        test_name.setText(getActivity().getResources().getText(R.string.gait_name));
        drawable = (GradientDrawable)cl_info.getBackground();
        drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));

        testActivity = ((TestActivity)getActivity());

        TooltipCompat.setTooltipText(btn_info, getString(R.string.info));
        TooltipCompat.setTooltipText(btn_mute, getString(R.string.mute));
        TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));

        // Start test
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTest();
            }
        });

        // Mute sound
        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testActivity.switchMute()) {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_off);
                    TooltipCompat.setTooltipText(btn_mute, getString(R.string.unmute));
                } else {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_up);
                    TooltipCompat.setTooltipText(btn_mute, getString(R.string.mute));
                }
            }
        });

        // Open info/instruction slides
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.slider_activity(Constants.GAIT_TEST);
            }
        });

        // Restore all variables and views to their original state
        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.tts.stop();

                if(currentStep >= 1 && currentStep != 3){
                    onClickWholeScreen(false);

                    currentStep = 0;
                    walkingTime = 0;
                    min_walkingTime = 100;
                    inProgress = false;
                    max_change = CHANGE_THRSHOLD;
                    yHistory = 0;

                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());

                    drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));
                    btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                    TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));
                    tv_result.setVisibility(View.GONE);
                    tv_result_label.setVisibility(View.GONE);
                    chronometer.setVisibility(View.GONE);
                    btn_play.setImageResource(R.drawable.ic_round_play_arrow);
                    btn_play.setVisibility(View.VISIBLE);
                    iv_person.setImageResource(R.drawable.ic_person);
                } else {
                    calculateScore(min_walkingTime);
                    testActivity.fragmentTestCompleted();
                }
            }
        });


        // Sensor declaration.
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            sensorAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_GAME);
        }


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        chronometer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_GAME);
    }

    // Execute the steps sequentially.
    private void continueTest() {
        switch (currentStep) {
            case 0:
                test_name.setText(getString(R.string.gait_name1));
                testActivity.readText(getString(R.string.gait_step1));
                onClickWholeScreen(true);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.repeat));
                chronometer.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.GONE);
                break;

            case 1:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_gait);
                onClickWholeScreen(false);
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 2:
                testActivity.tts.stop();
                test_name.setText(getString(R.string.gait_name2));
                testActivity.readText(getString(R.string.gait_step2));
                onClickWholeScreen(true);
                btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));
                chronometer.setBase(SystemClock.elapsedRealtime());
                break;

            case 3:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_gait);
                onClickWholeScreen(false);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.repeat));
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 4:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.gait_step3));
                onClickWholeScreen(true);
                showResult(min_walkingTime);
                break;

            case 5:
                testActivity.tts.stop();
                testActivity.fragmentTestCompleted();
                break;

            case 6:
                testActivity.tts.stop();
                drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorError));
                testActivity.readText(getString(R.string.gait_error));
                onClickWholeScreen(true);
                break;

            case 7:
                testActivity.tts.stop();
                testActivity.fragmentTestCompleted();
                break;
        }

        currentStep = currentStep +1;
    }

    // Set click listener in a whole screen layout
    private void onClickWholeScreen(boolean activated) {
        if (activated) {
            whole_screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    continueTest();
                }
            });
        } else {
            whole_screen.setOnClickListener(null);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // This conditional makes sure that the function is never executed before it is supposed to.
        if((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME){
            long curTime = SystemClock.elapsedRealtime();

            // If test started
            if(inProgress){
                lastSaved = System.currentTimeMillis();

                float yChange = 0;

                // Store accelerometer data in csv file
                testActivity.excelData.storeData(Constants.GAIT_TEST, System.currentTimeMillis(),
                        event.values[0], event.values[1], event.values[2]);

                if (yHistory == 0) {
                    yHistory = event.values[1];
                }

                // We obtain the difference of acceleration with respect to the previous measurement.
                if(event.values[1] > 0){
                    yChange =  yHistory - event.values[1];
                    yHistory = event.values[1];
                }


                if (yChange > max_change/3f){
                    lastChangeTime = curTime;
                    if(yChange > max_change) {
                        max_change = yChange;
                    }
                }

                diffChanges = curTime - lastChangeTime;

                // If there is no update of the LastChangeTime variable in the last TIME_THRSHOLD
                // seconds, assume the user is not walking any more, and calculate the time spent walking
                if (diffChanges > TIME_THRSHOLD){
                    walkingTime = lastChangeTime - chronometer.getBase();
                    walkingTime = (double)walkingTime/1000;

                    chronometer.stop();
                    // Save best result (shortest time walking)
                    if (walkingTime < min_walkingTime) { min_walkingTime = walkingTime; }

                    inProgress = false;

                    // If max_change is not different to original value, probably the mobile has not,
                    // then show error
                    if (max_change != CHANGE_THRSHOLD) {
                        max_change = CHANGE_THRSHOLD;
                        testActivity.beep.start();
                        iv_person.setImageResource(R.drawable.ic_test_done);
                        continueTest();
                    } else {
                        currentStep = 6;
                        continueTest();
                    }

                }

            } else {
                lastChangeTime = curTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public int calculateScore(double time){
        int score = 0;

        if (time < 4.82) {
            score = 4;
        } else if (time <= 6.20) {
            score = 3;
        } else if (time <= 8.70) {
            score = 2;
        } else if (time <= 30) {
            score = 1;
        }

        testActivity.averageSpeed = 4/time;
        testActivity.gaitScore = score;

        return score;
    }


    // Calculate, show score and store score on TestActivity
    public void showResult(double time) {
        test_name.setText(getString(R.string.score));
        chronometer.setVisibility(View.GONE);

        int score = calculateScore(time);

        tv_result.setText(Integer.toString(score));
        tv_result.setVisibility(View.VISIBLE);
        tv_result_label.setVisibility(View.VISIBLE);
    }
}