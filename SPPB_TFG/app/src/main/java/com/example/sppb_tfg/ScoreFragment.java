package com.example.sppb_tfg;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import static com.example.sppb_tfg.Constants.SELECTED_USER;

public class ScoreFragment extends Fragment {
    ConstraintLayout score_layout;
    ConstraintLayout constraint_explaining;
    ConstraintLayout constraint_history;

    ProgressBar progressBar;
    LinearLayout btn_save;
    LinearLayout btn_download;
    SharedPreferences sharedPreferences;

    TextView tv_scorename;
    TextView tv_save_as;
    TextView tv_score;
    TextView tv_explaining_label;

    ImageView iv_balance_color;
    TextView tv_balance_score_label;
    TextView tv_balance_score;

    ImageView iv_gait_color;
    TextView tv_gait_score_label;
    TextView tv_gait_score;
    TextView tv_average_speed;
    TextView tv_average_label;

    ImageView iv_chair_color;
    TextView tv_chair_score;
    TextView tv_chair_score_label;

    User selectedUser;
    int score = 0;
    int mCurrentTest = 0;
    int mBalanceScore = 0;
    int mGaitScore = 0;
    int mChairScore = 0;
    double mAverageSpeed = 0;

    TestActivity testActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        score_layout = view.findViewById(R.id.fragment_score);
        constraint_explaining = view.findViewById(R.id.constraing_explaining);
        constraint_history = view.findViewById(R.id.constraint_history);

        progressBar = (ProgressBar) view.findViewById(R.id.score_progressbar);

        tv_scorename = (TextView) view.findViewById(R.id.tv_scorename);
        btn_save = view.findViewById(R.id.btn_save_score);
        tv_save_as = (TextView) view.findViewById(R.id.tv_save_as);
        btn_download = view.findViewById(R.id.btn_save_csv);

        tv_score = (TextView) view.findViewById(R.id.tv_final_score);
        tv_explaining_label = (TextView) view.findViewById(R.id.tv_explaining_label);

        iv_balance_color = (ImageView) view.findViewById(R.id.iv_balance_color);
        tv_balance_score_label = (TextView) view.findViewById(R.id.tv_balance_score_label);
        tv_balance_score = (TextView) view.findViewById(R.id.tv_balance_score);

        iv_gait_color = (ImageView) view.findViewById(R.id.iv_gait_color);
        tv_gait_score_label = (TextView) view.findViewById(R.id.tv_gait_score_label);
        tv_gait_score = (TextView) view.findViewById(R.id.tv_gait_score);
        tv_average_label = (TextView) view.findViewById(R.id.tv_average_label);
        tv_average_speed = (TextView) view.findViewById(R.id.tv_average_speed);

        iv_chair_color = (ImageView) view.findViewById(R.id.iv_chair_color);
        tv_chair_score_label = (TextView) view.findViewById(R.id.tv_chair_score_label);
        tv_chair_score = (TextView) view.findViewById(R.id.tv_chair_score);

        Long currentUserID = -1L;

        // Determine if the fragment is called from UserFragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentUserID = bundle.getLong(SELECTED_USER);
        }

        // If is called from TestActivity, get data from it
        if (currentUserID == -1) {
            testActivity = ((TestActivity)getActivity());

            score = testActivity.getScore(mCurrentTest);
            mCurrentTest = testActivity.getmCurrentTest();

            mBalanceScore = testActivity.getScore(1);
            mGaitScore = testActivity.getScore(2);
            mChairScore = testActivity.getScore(3);
            mAverageSpeed = testActivity.getAverageSpeed();
        } else { // else, get data from local data base
            User user = User.getUser(getActivity(), currentUserID);

            score = user.getScore();
            mBalanceScore = user.getBalanceScore();
            mGaitScore = user.getSpeedScore();
            mChairScore = user.getChairScore();
            mAverageSpeed = user.getAverageSpeed();
        }

        final int pb_score;

        // Calculate how much progression bar have to advance and print explaining label if full test.
        if(mCurrentTest == 0){
            pb_score = 9*score - (9*score)/18;
            constraint_explaining.setVisibility(View.VISIBLE);

            if(score == 0) {
                constraint_explaining.setVisibility(View.GONE);
            } else if(score <= 3){
                tv_explaining_label.setText(getString(R.string.severe));
            } else if (score <= 6) {
                tv_explaining_label.setText(getString(R.string.moderate));
            } else if (score <= 9) {
                tv_explaining_label.setText(getString(R.string.slight));
            } else {
                tv_explaining_label.setText(getString(R.string.minimum));
            }
        } else { // if not full test, show only the corresponding items.
            pb_score = 25*score;
            constraint_explaining.setVisibility(View.GONE);

            if (mCurrentTest == 1){
                iv_gait_color.setVisibility(View.GONE);
                tv_gait_score_label.setVisibility(View.GONE);
                tv_gait_score.setVisibility(View.GONE);
                tv_average_label.setVisibility(View.GONE);
                tv_average_speed.setVisibility(View.GONE);

                iv_chair_color.setVisibility(View.GONE);
                tv_chair_score_label.setVisibility(View.GONE);
                tv_chair_score.setVisibility(View.GONE);
            } else if (mCurrentTest == 2){
                iv_balance_color.setVisibility(View.GONE);
                tv_balance_score_label.setVisibility(View.GONE);
                tv_balance_score.setVisibility(View.GONE);

                iv_chair_color.setVisibility(View.GONE);
                tv_chair_score_label.setVisibility(View.GONE);
                tv_chair_score.setVisibility(View.GONE);
            } else if (mCurrentTest == 3){
                iv_balance_color.setVisibility(View.GONE);
                tv_balance_score_label.setVisibility(View.GONE);
                tv_balance_score.setVisibility(View.GONE);
                tv_average_label.setVisibility(View.GONE);
                tv_average_speed.setVisibility(View.GONE);

                iv_gait_color.setVisibility(View.GONE);
                tv_gait_score_label.setVisibility(View.GONE);
                tv_gait_score.setVisibility(View.GONE);
            }
        }

        // After 0.5s, animate progressBar (circle) and score number
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = new ValueAnimator();
                animator.setObjectValues(0, score);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        tv_score.setText(String.valueOf(animation.getAnimatedValue()));
                    }
                });
                animator.setDuration(2000);
                animator.start();

                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", pb_score);
                animation.setDuration(2000);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }
        }, 500); //will start animation in 0.5 seconds

        // Show items score
        tv_balance_score.setText(Integer.toString(mBalanceScore));
        tv_gait_score.setText(Integer.toString(mGaitScore));
        tv_chair_score.setText(Integer.toString(mChairScore));
        tv_average_speed.setText(String.format("%.1f", mAverageSpeed));

        // Get selected user if any
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        long selectedUserID = sharedPreferences.getLong(SELECTED_USER, -1);

        // Show save button if there is a selected user
        if(selectedUserID != -1 && currentUserID == -1) {
            btn_save.setVisibility(View.VISIBLE);
            btn_download.setVisibility(View.VISIBLE);
            selectedUser = User.getUser(getActivity(), selectedUserID);

            tv_scorename.setText(selectedUser.getName());
        } else if (currentUserID != -1){ // else, if the user is not selected but comes from UserFragment, hide it.
            User currentUser = User.getUser(getActivity(), currentUserID);
            tv_scorename.setText(currentUser.getName());
            btn_save.setVisibility(View.INVISIBLE);
            btn_download.setVisibility(View.INVISIBLE);
        } else {
            tv_scorename.setVisibility(View.GONE);
            btn_save.setVisibility(View.GONE);
            constraint_history.setVisibility(View.GONE);
        }

        // Save data in local db
        btn_save.setOnClickListener(v -> {
            tv_save_as.setText(getString(R.string.saved));
            btn_save.setEnabled(false);

            if (tv_balance_score.getVisibility() == View.VISIBLE)
                selectedUser.setBalanceScore(mBalanceScore);
            if (tv_gait_score.getVisibility() == View.VISIBLE){
                selectedUser.setSpeedScore(mGaitScore);
                selectedUser.setAverageSpeed(mAverageSpeed);
            }
            if (tv_chair_score.getVisibility() == View.VISIBLE)
                selectedUser.setChairScore(mChairScore);
            selectedUser.update(getActivity());
        });

        btn_download.setOnClickListener(v -> {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(testActivity,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(testActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(testActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            30);
                }
            }

            // Make CSV file and share it
            try {
                testActivity.excelData.makeCSV("accelerometer_data");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


        return view;
    }
}