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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.sppb_tfg.Constants.ACCE_FILTER_DATA_MIN_TIME;

public class ChairThighFragment extends Fragment implements SensorEventListener {

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
    private String last_direction = "DOWN";

    private int n_standUp = 0;
    private double totalTime = 0;

    private SensorManager sensorManager;
    private Sensor sensorAcc;
    private long lastSaved = System.currentTimeMillis();

    private TestActivity testActivity;

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
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
        chronometer = view.findViewById(R.id.chronometer);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_mute = (ImageButton) view.findViewById(R.id.btn_mute);
        btn_info = (ImageButton) view.findViewById(R.id.imageButton5);
        btn_replay = (ImageButton) view.findViewById(R.id.btn_replay);

        testActivity = ((TestActivity)getActivity());

        test_name.setText(getString(R.string.chair_name));
        iv_person.setImageResource(R.drawable.ic_person_sitting);

        drawable = (GradientDrawable)cl_info.getBackground();
        drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorChairStand));


        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTest();
            }
        });

        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testActivity.switchMute()) {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_off);
                } else {
                    btn_mute.setImageResource(R.drawable.ic_round_volume_up);
                }
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.slider_activity(Constants.CHAIR_TEST);
            }
        });

        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.tts.stop();
                if (currentStep < 2) {
                    testActivity.fragmentTestCompleted();
                } else {
                    currentStep = 0;
                    inProgress = false;
                    last_direction = "DOWN";
                    n_standUp = 0;
                    totalTime = 0;

                    iv_person.setImageResource(R.drawable.ic_person_sitting);
                    tv_result.setText("0");
                    btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                    tv_result.setVisibility(View.GONE);
                    tv_result_label.setVisibility(View.GONE);
                    btn_play.setVisibility(View.VISIBLE);

                    SelectPositionFragment selectPositionFragment = new SelectPositionFragment();
                    testActivity.openFragment(selectPositionFragment, true);
                }
            }
        });

        // Sensor declaration.
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null){
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

    private void continueTest() {
        switch (currentStep) {
            case 0:
                testActivity.readText(getString(R.string.chair_thigh_step0));
                btn_play.setVisibility(View.GONE);
                tv_result.setVisibility(View.VISIBLE);
                onClickWholeScreen(true);
                break;

            case 1:
                onClickWholeScreen(false);
                testActivity.readText(getString(R.string.chair_thigh_step1));
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                inProgress = true;
                break;

            case 2:
                testActivity.readText(getString(R.string.chair_chest_step2));
                showResult(totalTime);
                onClickWholeScreen(true);
                break;

            case 3:
                testActivity.tts.stop();
                testActivity.fragmentTestCompleted();
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
        if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
            lastSaved = System.currentTimeMillis();

            if (inProgress) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (Math.abs(z) > Math.abs(y)) {

                    if (last_direction != "DOWN") {

                        tv_result.setText(Integer.toString(n_standUp));

                        if (n_standUp == 5){
                            chronometer.stop();
                            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                            totalTime = (double)elapsedMillis/1000;

                            inProgress = false;
                            iv_person.setImageResource(R.drawable.ic_test_done);
                            continueTest();
                        } else {
                            iv_person.setImageResource(R.drawable.ic_person_sitting);
                            testActivity.readText(Integer.toString(n_standUp));
                        }

                        last_direction = "DOWN";
                    }
                } else if (last_direction != "UP"){
                    if(n_standUp == 0) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        testActivity.readText(getString(R.string.start));
                    }

                    n_standUp++;
                    iv_person.setImageResource(R.drawable.ic_person_stand_up);
                    last_direction = "UP";
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void showResult(double time) {
        test_name.setText(getString(R.string.score));

        int score = 0;

        if (time < 11.19) {
            score = 4;
        } else if (time <= 13.69) {
            score = 3;
        } else if (time <= 16.69) {
            score = 2;
        } else if (time <= 59) {
            score = 1;
        }

        testActivity.chairScore = score;
        tv_result.setText(Integer.toString(score));
        tv_result.setVisibility(View.VISIBLE);
        tv_result_label.setVisibility(View.VISIBLE);
    }
}
