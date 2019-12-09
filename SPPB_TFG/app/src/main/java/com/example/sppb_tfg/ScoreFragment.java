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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    RecyclerView mRecyclerView;
    HistoryAdapter adapter;

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

    ImageView btn_download_history;

    long currentUserID = -1L;
    long markedUserID = -1L;
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

        btn_download_history = (ImageView) view.findViewById(R.id.iv_download_history);

        // Set recyclerview and link it with adapter, to show history
        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_list);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // Get selected user if any
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        markedUserID = sharedPreferences.getLong(SELECTED_USER, -1);


        // Determine if the fragment is called from UserFragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentUserID = bundle.getLong(SELECTED_USER);

            if (User.getUser(getActivity(), currentUserID).testNotPerformed()) {
                constraint_history.setVisibility(View.GONE);
            } else {
                adapter = new HistoryAdapter(User.getUser(getActivity(), currentUserID));
                mRecyclerView.setAdapter(adapter);
            }
        } else if (markedUserID != -1) {
            if (User.getUser(getActivity(), markedUserID).testNotPerformed()) {
                constraint_history.setVisibility(View.GONE);
            } else {
                adapter = new HistoryAdapter(User.getUser(getActivity(), markedUserID));
                mRecyclerView.setAdapter(adapter);
            }
        }


        // If is called from TestActivity, get data from it
        if (currentUserID == -1) {
            testActivity = ((TestActivity) getActivity());

            mCurrentTest = testActivity.getmCurrentTest();

            if (testActivity.getScore(1) >= 0) {
                mBalanceScore = testActivity.getScore(1);
                mAverageSpeed = testActivity.getAverageSpeed();
            } else {
                mBalanceScore = 0;
                mAverageSpeed = 0;
            }

            if (testActivity.getScore(2) >= 0) {
                mGaitScore = testActivity.getScore(2);
            } else {
                mGaitScore = 0;
            }

            if (testActivity.getScore(3) >= 0) {
                mChairScore = testActivity.getScore(3);
            } else {
                mChairScore = 0;
            }

            score = mBalanceScore + mGaitScore + mChairScore;
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
        if (mCurrentTest == 0) {
            pb_score = 9 * score - (9 * score) / 18;
            constraint_explaining.setVisibility(View.VISIBLE);

            if (score == 0) {
                constraint_explaining.setVisibility(View.GONE);
            } else if (score <= 3) {
                tv_explaining_label.setText(getString(R.string.severe));
            } else if (score <= 6) {
                tv_explaining_label.setText(getString(R.string.moderate));
            } else if (score <= 9) {
                tv_explaining_label.setText(getString(R.string.slight));
            } else {
                tv_explaining_label.setText(getString(R.string.minimum));
            }
        } else { // if not full test, show only the corresponding items.
            pb_score = 25 * score;
            constraint_explaining.setVisibility(View.GONE);

            if (mCurrentTest == 1) {
                iv_gait_color.setVisibility(View.GONE);
                tv_gait_score_label.setVisibility(View.GONE);
                tv_gait_score.setVisibility(View.GONE);
                tv_average_label.setVisibility(View.GONE);
                tv_average_speed.setVisibility(View.GONE);

                iv_chair_color.setVisibility(View.GONE);
                tv_chair_score_label.setVisibility(View.GONE);
                tv_chair_score.setVisibility(View.GONE);
            } else if (mCurrentTest == 2) {
                iv_balance_color.setVisibility(View.GONE);
                tv_balance_score_label.setVisibility(View.GONE);
                tv_balance_score.setVisibility(View.GONE);

                iv_chair_color.setVisibility(View.GONE);
                tv_chair_score_label.setVisibility(View.GONE);
                tv_chair_score.setVisibility(View.GONE);
            } else if (mCurrentTest == 3) {
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

        // There is a marked user
        if (markedUserID != -1 && currentUserID == -1) {
            btn_save.setVisibility(View.VISIBLE);
            btn_download.setVisibility(View.VISIBLE);
            selectedUser = User.getUser(getActivity(), markedUserID);
            tv_scorename.setText(selectedUser.getName());

        } else if (currentUserID != -1) { // else, if the user is not selected but comes from UserFragment, hide it.
            User currentUser = User.getUser(getActivity(), currentUserID);
            tv_scorename.setText(currentUser.getName());
            btn_save.setVisibility(View.INVISIBLE);
            btn_download.setVisibility(View.INVISIBLE);
        } else {
            tv_scorename.setVisibility(View.GONE);
            btn_save.setVisibility(View.GONE);
            constraint_history.setVisibility(View.GONE);
        }

        btn_download_history.setOnClickListener(v -> {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            30);
                }
            }


            Long id;
            if (currentUserID != -1) {
                id = currentUserID;
            } else {
                id = markedUserID;
            }

            DownloadHistData downloadHistData = new DownloadHistData(getContext(),
                    User.getUser(getContext(), id));

            try {
                downloadHistData.makeCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Save data in local db
        btn_save.setOnClickListener(v -> {
            tv_save_as.setText(getString(R.string.saved));
            btn_save.setEnabled(false);

            selectedUser.setBalanceScore(testActivity.getScore(1));
            selectedUser.setSpeedScore(testActivity.getScore(2));
            selectedUser.setChairScore(testActivity.getScore(3));
            selectedUser.setAverageSpeed(testActivity.getAverageSpeed());
            selectedUser.setTestDateToday();

            selectedUser.update(getActivity());
        });

        btn_download.setOnClickListener(v -> {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(getActivity(),
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