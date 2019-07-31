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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.sppb_tfg.Constants.ACCE_FILTER_DATA_MIN_TIME;

public class ChairFragment extends Fragment implements SensorEventListener {

    // Interface control variables
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

    // Chair stand test variables
    private List<String> instructions;
    private float axisChanges[] = new float[8];
    private int instructionIndex = 0;
    private int lastInstruction = -1;

    private boolean movStarted = false;
    private boolean leanDown = false;
    private boolean leanUp = false;
    private boolean calibrating = true;
    private boolean beepReady = false;
    private boolean iv_standing = false;

    private int timeThreshold = 300;
    private double corrCoeff = 2.5;

    private float maxYchange = 0;
    private float maxZchange = 0;
    private float minYchange = 0;
    private float minZchange = 0;

    private float yHistory = 0;
    private float zHistory = 0;
    private long lastChangeTime = 0;
    private String direction = "DOWN_FINISHED"; // The initial position is expected to be seated.
    private String last_direction = "DOWN";
    private String last_printed_direction = "NONE";

    private int n_standUp = 0;
    private double totalTime;

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

        test_name.setText(getActivity().getResources().getText(R.string.chair_name));
        iv_person.setImageResource(R.drawable.ic_person_sitting);

        drawable = (GradientDrawable)cl_info.getBackground();
        drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorChairStand));

        testActivity = ((TestActivity)getActivity());

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

                if(currentStep >= 1){
                    onClickWholeScreen(false);

                    currentStep = 0;
                    inProgress = false;
                    instructionIndex = 0;
                    lastInstruction = -1;
                    n_standUp = 0;
                    totalTime = 0;

                    for(int i=0; i<axisChanges.length; i++)
                        axisChanges[i] = 0;

                    movStarted = false;
                    leanDown = false;
                    leanUp = false;
                    calibrating = true;
                    iv_standing = false;

                    yHistory = 0;
                    zHistory = 0;
                    lastChangeTime = 0;
                    direction = "DOWN_FINISHED";
                    last_direction = "DOWN";
                    last_printed_direction = "NONE";

                    drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorChairStand));
                    tv_result.setText("0");
                    btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                    tv_result.setVisibility(View.GONE);
                    tv_result_label.setVisibility(View.GONE);
                    btn_play.setEnabled(true);

                    iv_person.setImageResource(R.drawable.ic_person_sitting);
                    btn_play.setImageResource(R.drawable.ic_round_play_arrow);
                    btn_play.setVisibility(View.VISIBLE);
                } else {
                    testActivity.fragmentTestCompleted();
                }

            }
        });

        instructions = new ArrayList<String>();
        instructions.add(getString(R.string.chair_instructions0));
        instructions.add(getString(R.string.chair_instructions1));
        instructions.add(getString(R.string.chair_instructions2));
        instructions.add(getString(R.string.chair_instructions3));

        for(int i=0; i<axisChanges.length; i++)
            axisChanges[i] = 0;


        // Sensor declaration.
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null){
            sensorAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
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
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void continueTest() {
        switch (currentStep) {
            case 0:
                test_name.setText(getString(R.string.chair_name));
                testActivity.readText(getString(R.string.chair_step0));
                onClickWholeScreen(true);
                btn_play.setImageResource(R.drawable.ic_compass_symbol);
                break;

            case 1:
                testActivity.tts.stop();
                onClickWholeScreen(false);
                btn_play.setEnabled(false);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                inProgress = true;
                btn_play.animate().rotation(360).setDuration(2000).start();
                break;

            case 2:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.chair_step2));
                onClickWholeScreen(true);
                showResult(totalTime);
                break;

            case 3:
                testActivity.tts.stop();
                testActivity.fragmentTestCompleted();
                break;

            case 4:
                testActivity.tts.stop();
                drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorError));
                testActivity.readText(getString(R.string.chair_errorCalibrating));
                onClickWholeScreen(true);
                inProgress = false;
                break;

            case 5:
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
        long curTime = System.currentTimeMillis();

        if(inProgress && ((curTime - lastSaved) > ACCE_FILTER_DATA_MIN_TIME)) {
            lastSaved = curTime;
            long diffChanges = (curTime - lastChangeTime);

            // If first execution, history starts with current event value to avoid
            // getting a negative number as result of gravity force on yChange variable.
            if (yHistory == 0) {
                yHistory = event.values[1];
                zHistory = event.values[2];
            }

            // We obtain the difference of acceleration with respect to the previous measurement.
            float yChange = yHistory - event.values[1];
            float zChange = zHistory - event.values[2];
            yHistory = event.values[1];
            zHistory = event.values[2];

            if (!calibrating){
                // If the movement has already begun, it only accepts the
                // deceleration value that confirms the end of it.
                if (movStarted) {
                    if (diffChanges > (timeThreshold)){
                        if (direction == "UP_STARTED"){
                            if (yChange > axisChanges[3]/corrCoeff){
                                Log.i("ACC", "Ychange UP FINISH: " + yChange);
                                direction = "UP_FINISHED";
                                last_direction = "UP";
                                movStarted = false;
                                lastChangeTime = curTime;
                            }
                        } else if (direction == "DOWN_STARTED") {
                            if(!leanDown && yChange < axisChanges[6]/corrCoeff ){
                                Log.i("ACC", "Ychange DOWN FINISH: " + yChange);
                                leanDown = true;
                            }
                            if (leanDown && zChange < axisChanges[4]/(corrCoeff*1.5)){
                                Log.i("ACC", "ZZZZZchange DOWN FINISH: " + yChange);
                                direction = "DOWN_FINISHED";
                                last_direction = "DOWN";
                                movStarted = false;
                                leanDown = false;
                                lastChangeTime = curTime;
                            }
                        }
                    }
                    // If it hasn't begun, it only accepts values that indicate the
                    // beginning of the movement.
                } else {
                    if(!leanUp && zChange > axisChanges[1]/corrCoeff)
                        leanUp = true;
                    // From sitting to standing
                    if (yChange < axisChanges[2]/corrCoeff && leanUp && last_direction != "UP" && diffChanges > timeThreshold){
                        Log.i("ACC", "Ychange UP START: " + yChange);
                        direction = "UP_STARTED";
                        movStarted = true;
                        lastChangeTime = curTime;
                        leanUp = false;
                    } else if (yChange > axisChanges[7]/corrCoeff && last_direction != "DOWN" && diffChanges > timeThreshold){
                        Log.i("ACC", "Ychange DOWN START: " + yChange);
                        direction = "DOWN_STARTED";
                        movStarted = true;
                        lastChangeTime = curTime;
                    }
                }

                // Calibrate if TextToSpeech engine is ready
            } else if(testActivity.ttsReady) {
                // Read each instruction only once
                if(instructionIndex != lastInstruction){
                    lastInstruction = instructionIndex;
                    testActivity.readText(instructions.get(instructionIndex));
                    switchImageView();
                }

                int isSitting = instructionIndex%2;

                // Wait until reading is finished
                if (testActivity.tts.isSpeaking()) {
                    lastChangeTime = curTime;
                    beepReady = true;

                    if (zChange < minZchange){
                        minZchange = zChange;
//                        /**/Log.i("ACC", "MIN Z: " + zChange);
                    }
                    if (zChange > maxZchange){
                        maxZchange = zChange;
//                        Log.i("ACC", "MAX Z: " + zChange);
                    }
                    if (yChange < minYchange){
                        minYchange = yChange;
                        Log.i("ACC", "MIN Y: " + yChange);
                    }
                    if (yChange > maxYchange){
                        if(isSitting == 1){
                           if (minYchange > -0.4){
                               maxYchange = yChange;
                               Log.i("ACC", "MAX Y: " + yChange);
                           }
                        } else {
                            maxYchange = yChange;
                            Log.i("ACC", "MAX Y: " + yChange);
                        }
                    }
                } else {
                    if (beepReady){
                        /*testActivity.beep.start();*/
                        beepReady = false;
                        lastChangeTime = curTime;
                        //iniTime = curTime;
                    }
                    // Read the max and min values of Z, Y axis
                    if (zChange < minZchange){
                        minZchange = zChange;
                        lastChangeTime = curTime;
//                        Log.i("ACC", "MIN Z: " + zChange);
                    }
                    if (zChange > maxZchange){
//                        Log.i("ACC", "MAX Z: " + zChange);
                        maxZchange = zChange;
                        lastChangeTime = curTime;
                    }
                    if (yChange < minYchange){
                        Log.i("ACC", "MIN Y: " + yChange);
                        minYchange = yChange;
                        lastChangeTime = curTime;
                    }
                    if (yChange > maxYchange){
                        if(isSitting == 1){
                            if (minYchange > -0.4){
                                maxYchange = yChange;
                                Log.i("ACC", "MAX Y: " + yChange);
                            }
                        } else {
                            maxYchange = yChange;
                            Log.i("ACC", "MAX Y: " + yChange);
                        }
                    }

                    // When there is no major change in the last second
                    if(diffChanges > 2000){
                        // Save the values depending on whether the user is standing up or sitting.
                        // and calculate the average value of the two measurements.
                        // (if sitting instruction, isSitting is 1 so the measurements have to be
                        // saved on the last 4 positions of the array)

                        axisChanges[0 + 4*isSitting] = (axisChanges[0 + 4*isSitting]+minZchange)/(instructionIndex/2 + 1);
                        axisChanges[1 + 4*isSitting] = (axisChanges[1 + 4*isSitting]+maxZchange)/(instructionIndex/2 + 1);
                        axisChanges[2 + 4*isSitting] = (axisChanges[2 + 4*isSitting]+minYchange)/(instructionIndex/2 + 1);
                        axisChanges[3 + 4*isSitting] = (axisChanges[3 + 4*isSitting]+maxYchange)/(instructionIndex/2 + 1);

                        // Restore the default value
                        minYchange = 0;
                        maxYchange = 0;
                        minZchange = 0;
                        maxZchange = 0;

                        Log.i("ACC", "####################################");

                        if(instructionIndex < instructions.size()-1) {
                            instructionIndex++;
                        } else {
                            float overall = 0;

                            for(int i = 0; i < axisChanges.length; i++){
                                /*if (i%2 == 0) {
                                    overall += Math.abs(axisChanges[i]);
                                } else {
                                    overall += axisChanges[i];
                                }*/
                                overall += Math.abs(axisChanges[i]);
                            }

/*                            Log.i("ACC", "OVERALL: " + overall);
                            Log.i("ACC", "axisChanges[6]: " + axisChanges[6]);
                            Log.i("ACC", "axisChanges[4]: " + axisChanges[4]);
                            Log.i("ACC", "axisChanges[7]: " + axisChanges[7]);
                            Log.i("ACC", "axisChanges[3]: " + axisChanges[3]);
                            Log.i("ACC", "axisChanges[2]: " + axisChanges[2]);
                            Log.i("ACC", "axisChanges[1]: " + axisChanges[1]);*/

                            /*if (overall > 25.0f){
                                corrCoeff = corrCoeff + overall/10;
                            }*/

                          Toast.makeText(getActivity(), "OVERALL: " + overall, Toast.LENGTH_LONG).show();

                            if(overall > 8.0f){
                                btn_play.setVisibility(View.GONE);
                                tv_result.setVisibility(View.VISIBLE);
                                testActivity.readText(getString(R.string.chair_step1));
                            } else {
                                currentStep = 4;
                                continueTest();
                            }

                            calibrating = false;
                            instructionIndex = 0;
                            lastInstruction = -1;
                            lastChangeTime = curTime;
                        }
                    }
                }
            }

            if (last_printed_direction != direction && !calibrating){
                if (direction == "DOWN_FINISHED" && n_standUp != 0){

                    tv_result.setText(Integer.toString(n_standUp));

                    if (n_standUp == 5) {
                        chronometer.stop();
                        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        totalTime = (double)elapsedMillis/1000;

                        inProgress = false;
                        iv_person.setImageResource(R.drawable.ic_test_done);
                        continueTest();
                    } else {
                        testActivity.readText(Integer.toString(n_standUp));
                        //testActivity.readText(getString(R.string.sitting));
                        switchImageView();
                    }
                }

                else if (direction == "UP_FINISHED"){
                    if (n_standUp == 0){
                        testActivity.readText(getString(R.string.start));
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    }

                    n_standUp++;

                    //testActivity.readText(getString(R.string.standing));
                    switchImageView();

                }

                last_printed_direction = direction;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void switchImageView() {
        if (iv_standing) {
            iv_person.setImageResource(R.drawable.ic_person_sitting);
            iv_standing = false;
        } else {
            iv_person.setImageResource(R.drawable.ic_person_stand_up);
            iv_standing = true;
        }
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

        ((TestActivity) getActivity()).chairScore = score;
        tv_result.setText(Integer.toString(score));
        tv_result.setVisibility(View.VISIBLE);
        tv_result_label.setVisibility(View.VISIBLE);
    }
}
