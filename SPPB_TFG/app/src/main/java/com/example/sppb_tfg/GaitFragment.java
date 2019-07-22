package com.example.sppb_tfg;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private int currentStep = 0;
    private boolean inProgress = false;

    // Accelerometer and time variables
    private final int TIME_THRSHOLD = 3000; // Miliseconds
    private float max_y = 1f;
    private long lastChangeTime;

    private double walkingTime = 0;
    private double min_walkingTime = 100;

    SensorManager sensorManager;
    Sensor sensorAcc;


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

        test_name.setText(getActivity().getResources().getText(R.string.gait_name));
        cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTest();
            }
        });

        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TestActivity)getActivity()).switchMute()) {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_off);
                } else {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_up);
                }
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).slider_activity(Constants.GAIT_TEST);
            }
        });

        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentStep = 0;
                ((TestActivity)getActivity()).tts.stop();
                cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));

                walkingTime = 0;
                min_walkingTime = 100;
                inProgress = false;

                onClickWholeScreen(false);
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());

                iv_person.setImageResource(R.drawable.ic_person);

                chronometer.setVisibility(View.GONE);
                btn_play.setImageResource(R.drawable.ic_round_play_arrow);
                btn_play.setVisibility(View.VISIBLE);
            }
        });


        // Sensor declaration. We use 1Hz frequency to get smoother measurements.
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorAcc = sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION).get(0);
        sensorManager.registerListener(this, sensorAcc, 1000000);

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
        sensorManager.registerListener(this, sensorAcc, 1000000);
    }


    private void continueTest() {
        switch (currentStep) {
            case 0:
                test_name.setText(getString(R.string.gait_name1));
                ((TestActivity)getActivity()).readText(getString(R.string.gait_step1));
                onClickWholeScreen(true);
                chronometer.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.GONE);
                break;

            case 1:
                ((TestActivity)getActivity()).tts.stop();
                ((TestActivity)getActivity()).readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_gait);
                onClickWholeScreen(false);
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 2:
                ((TestActivity)getActivity()).tts.stop();
                test_name.setText(getString(R.string.gait_name2));
                ((TestActivity)getActivity()).readText(getString(R.string.gait_step2));
                chronometer.setBase(SystemClock.elapsedRealtime());
                onClickWholeScreen(true);
                break;

            case 3:
                ((TestActivity)getActivity()).tts.stop();
                ((TestActivity)getActivity()).readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_gait);
                onClickWholeScreen(false);
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 4:
                ((TestActivity)getActivity()).tts.stop();
                ((TestActivity)getActivity()).readText(getString(R.string.gait_step3));
                onClickWholeScreen(true);
                showResult(min_walkingTime);
                break;

            case 5:
                ((TestActivity)getActivity()).tts.stop();
                ((TestActivity)getActivity()).fragmentTestCompleted();
                break;

            case 6:
                ((TestActivity)getActivity()).tts.stop();
                cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorError));
                ((TestActivity)getActivity()).readText(getString(R.string.gait_error));
                onClickWholeScreen(true);
                break;

            case 7:
                ((TestActivity)getActivity()).tts.stop();
                ((TestActivity)getActivity()).fragmentTestCompleted();
                break;

        }

        currentStep = currentStep +1;
    }


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
        long curTime = SystemClock.elapsedRealtime();

        if(inProgress){
            // Values measured on each axis.
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            if (y > max_y /4f){
                lastChangeTime = curTime;
                if(y > max_y) {
                    max_y = y;
                }
            }

            long diffChanges = curTime - lastChangeTime;

            // If there is no update of the LastChangeTime variable in the last TIME_THRSHOLD
            // seconds, assume the user is not walking any more, and calculate the time spent walking
            if (diffChanges > TIME_THRSHOLD){
                walkingTime = lastChangeTime - chronometer.getBase();
                walkingTime = (double)walkingTime/1000;

                chronometer.stop();

                if (walkingTime < min_walkingTime) { min_walkingTime = walkingTime; }


                inProgress = false;

                if (max_y != 1f) {
                    max_y = 1f;
                    ((TestActivity) getActivity()).beep.start();
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void showResult(double time) {
        test_name.setText(getString(R.string.score));
        chronometer.setVisibility(View.GONE);
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

        ((TestActivity) getActivity()).balanceScore = score;
        tv_result.setText(Integer.toString(score));
        tv_result.setVisibility(View.VISIBLE);
        tv_result_label.setVisibility(View.VISIBLE);
    }
}