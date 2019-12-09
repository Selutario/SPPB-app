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

import static com.example.sppb_tfg.Constants.ACCE_FILTER_DATA_MIN_TIME;

/*
 * The objective of this algorithm is to check if the user changes his original position.
 *
 * To do this, the user is asked to remain still in the original position, and the accelerometer
 * samples are used to calculate the average position of the three axes during that time.
 * Once calibrated, if any of the axes measures a significantly different value from the average,
 * it indicates that the user has lost balance.
 *
 * Made by José Luis López Sánchez.*/
public class BalanceFragment extends Fragment implements SensorEventListener {
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

    // Calibration variables
    private boolean ready_to_calibrate = false;
    private boolean calibrated = false;
    private int sample_index = 0;
    private final int MAX_INDEX = 20;
    private float measured_x[] = new float [MAX_INDEX];
    private float measured_y[] = new float [MAX_INDEX];
    private float measured_z[] = new float [MAX_INDEX];

    // Average of each axis.
    private float mean_x = 0;
    private float mean_y = 0;
    private float mean_z = 0;
    private final float move_allowed = 3;

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
        tv_result = (TextView) view.findViewById(R.id.tv_score_history);
        tv_result_label = (TextView) view.findViewById(R.id.tv_result_label);
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
        chronometer = view.findViewById(R.id.chronometer);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_mute = (ImageButton) view.findViewById(R.id.btn_mute);
        btn_info = (ImageButton) view.findViewById(R.id.imageButton5);
        btn_replay = (ImageButton) view.findViewById(R.id.btn_replay);

        testActivity = ((TestActivity)getActivity());

        // Set test name and test color on the interface
        test_name.setText(getString(R.string.balance_name));
        drawable = (GradientDrawable)cl_info.getBackground();
        drawable.setColor(ContextCompat.getColor(getActivity(), R.color.colorBalance));

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
                testActivity.slider_activity(Constants.BALANCE_TEST);
            }
        });

        // Restore all variables and views to their original state
        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testActivity.tts.stop();

                if(calibrated && currentStep != 3 && currentStep != 5 && currentStep != 7){
                    currentStep = 0;

                    sample_index = 0;
                    inProgress = false;
                    calibrated = false;
                    ready_to_calibrate = false;

                    onClickWholeScreen(false);
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());

                    iv_person.setImageResource(R.drawable.ic_person);

                    btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                    TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));
                    chronometer.setVisibility(View.GONE);
                    btn_play.setImageResource(R.drawable.ic_round_play_arrow);
                    btn_play.setVisibility(View.VISIBLE);
                    btn_play.setEnabled(true);
                    tv_result.setVisibility(View.GONE);
                    tv_result_label.setVisibility(View.GONE);
                } else {
                    if(currentStep == 5) {
                        testActivity.balanceScore = 1;
                    } else if (currentStep == 7) {
                        testActivity.balanceScore = 2;
                    } else {
                        testActivity.balanceScore = 0;
                    }
                    testActivity.fragmentTestCompleted();
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

    // Execute the steps sequentially.
    private void continueTest() {
        switch (currentStep) {
            case 0:
                testActivity.readText(getString(R.string.balance_step1));
                onClickWholeScreen(true);
                btn_play.setImageResource(R.drawable.ic_compass_symbol);
                break;

            case 1:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.balance_step2));
                ready_to_calibrate = true;
                onClickWholeScreen(false);
                btn_play.setEnabled(false);
                btn_play.animate().rotation(360).setDuration(2000).start();

                break;

            case 2:
                testActivity.tts.stop();
                test_name.setText(getString(R.string.balance_name1));
                testActivity.readText(getString(R.string.balance_step3));
                onClickWholeScreen(true);
                btn_play.setVisibility(View.GONE);
                chronometer.setVisibility(View.VISIBLE);
                break;

            case 3:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.start));
                onClickWholeScreen(false);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.repeat));
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 4:
                testActivity.tts.stop();
                test_name.setText(getString(R.string.balance_name2));
                testActivity.readText(getString(R.string.balance_step4));
                btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));
                onClickWholeScreen(true);
                inProgress = false;
                break;

            case 5:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_balan_2);
                onClickWholeScreen(false);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.repeat));
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 6:
                testActivity.tts.stop();
                test_name.setText(getString(R.string.balance_name3));
                testActivity.readText(getString(R.string.balance_step5));
                onClickWholeScreen(true);
                btn_replay.setImageResource(R.drawable.ic_round_cancel_24px);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.unable));
                inProgress = false;
                break;

            case 7:
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.start));
                iv_person.setImageResource(R.drawable.ic_person_balan_3);
                onClickWholeScreen(false);
                btn_replay.setImageResource(R.drawable.ic_round_replay);
                TooltipCompat.setTooltipText(btn_replay, getString(R.string.repeat));
                inProgress = true;
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case 8:
                showResult(4);
                testActivity.tts.stop();
                testActivity.readText(getString(R.string.balance_step6));
                onClickWholeScreen(true);
                inProgress = false;
                currentStep = 9;
                break;

            case 9:
                onClickWholeScreen(true);
                inProgress = false;
                break;

            case 10:
                testActivity.tts.stop();
                testActivity.fragmentTestCompleted();
                break;

        }

        currentStep = currentStep +1;
    }

    // Used to set click listener in a whole screen layout
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
        if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME) {
            lastSaved = System.currentTimeMillis();
            // Values measured on each axis.
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calibration process
            if(ready_to_calibrate) {
                if (!calibrated) {
                    // If there are enough samples, calculate average.
                    if (sample_index >= MAX_INDEX) {
                        for (int i = 0; i < MAX_INDEX; i++) {
                            mean_x += measured_x[i];
                            mean_y += measured_y[i];
                            mean_z += measured_z[i];
                        }
                        mean_x = mean_x / sample_index;
                        mean_y = mean_y / sample_index;
                        mean_z = mean_z / sample_index;

                        calibrated = true;
                        ready_to_calibrate = false;
                        testActivity.beep.start();
                        continueTest();
                    } else {
                        // If there are not, save the sample value.
                        measured_x[sample_index % MAX_INDEX] = x;
                        measured_y[sample_index % MAX_INDEX] = y;
                        measured_z[sample_index % MAX_INDEX] = z;
                        sample_index++;
                    }
                }
            }

            if (calibrated && inProgress) {
                // How much the current value changes with respect to the average or
                // normal position.
                float change_x, change_y, change_z;
                change_x = Math.abs(x - mean_x);
                change_y = Math.abs(y - mean_y);
                change_z = Math.abs(z - mean_z);

                // Store accelerometer data in csv file
                testActivity.excelData.storeData(Constants.BALANCE_TEST, System.currentTimeMillis(),
                        x, y, z);

                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                if(change_x > move_allowed || change_z > move_allowed || change_y > 1) {
                    desbalanced(elapsedMillis);

                } else if (elapsedMillis > 10100) {
                    chronometer.stop();
                    iv_person.setImageResource(R.drawable.ic_test_done);
                    continueTest();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // To show result on test layout after completing the test.
    public void showResult(int score) {
        testActivity.balanceScore = score;

        test_name.setText(getString(R.string.score));
        tv_result.setText(Integer.toString(score));
        chronometer.setVisibility(View.GONE);
        tv_result_label.setVisibility(View.VISIBLE);
        tv_result.setVisibility(View.VISIBLE);
    }

    // If user is desbalanced, this function is called to calculate score and store it on TestActivity
    public void desbalanced(long elapsedTime) {
        testActivity.readText(getString(R.string.desbalanced));
        /*try {
            testActivity.excelData.makeCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        chronometer.stop();
        inProgress = false;
        int score = 0;

        if (currentStep < 5) {
            iv_person.setImageResource(R.drawable.ic_person_desbalanced_1);
        } else if (currentStep < 7) {
            iv_person.setImageResource(R.drawable.ic_person_desbalan_2);
            score = 1;
        } else {
            iv_person.setImageResource(R.drawable.ic_person_desbalan_3);

            if(elapsedTime < 3000) {
                score = 2;
            } else if (elapsedTime <= 9000) {
                score = 3;
            } else {
                score = 4;
            }
        }

        showResult(score);
        currentStep = 9;
        continueTest();
    }
}
