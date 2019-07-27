package com.example.sppb_tfg;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScoreFragment extends Fragment {
    ConstraintLayout score_layout;
    ProgressBar progressBar;
    TextView tv_score;
    TextView tv_balance_score;
    TextView tv_gait_score;
    TextView tv_chair_score;
    ConstraintLayout btn_save;
    TextView tv_save_as;
    SharedPreferences sharedPreferences;

    User user;
    int mCurrentTest;

    TestActivity testActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        score_layout = view.findViewById(R.id.fragment_score);
        tv_score = (TextView) view.findViewById(R.id.tv_final_score);
        tv_balance_score = (TextView) view.findViewById(R.id.tv_balance_score);
        tv_gait_score = (TextView) view.findViewById(R.id.tv_gait_score);
        tv_chair_score = (TextView) view.findViewById(R.id.tv_chair_score);
        progressBar = (ProgressBar) view.findViewById(R.id.score_progressbar);
        btn_save = view.findViewById(R.id.btn_save);
        tv_save_as = (TextView) view.findViewById(R.id.tv_save_as);

        testActivity = ((TestActivity)getActivity());

        mCurrentTest = testActivity.getmCurrentTest();
        final int score = testActivity.getScore(0);
        final int pb_score = 9*score - (9*score)/18;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = new ValueAnimator();
                animator.setObjectValues(0, score);// here you set the range, from 0 to "count" value
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        tv_score.setText(String.valueOf(animation.getAnimatedValue()));
                    }
                });
                animator.setDuration(2000); // here you set the duration of the anim
                animator.start();

                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", pb_score);
                animation.setDuration(2000);
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();
            }
        }, 500); //will start animation in 0.5 seconds

        tv_balance_score.setText(Integer.toString(testActivity.getScore(1)));
        tv_gait_score.setText(Integer.toString(testActivity.getScore(2)));
        tv_chair_score.setText(Integer.toString(testActivity.getScore(3)));

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        long selectedId = sharedPreferences.getLong("SelectedUser", -1);

        if(selectedId == -1) {
            btn_save.setVisibility(View.GONE);
        } else {
            btn_save.setVisibility(View.VISIBLE);
            user = User.getUser(getActivity(), selectedId);

            Resources res = getResources();
            String text = String.format(res.getString(R.string.save_as), user.getName());
            tv_save_as.setText(text);
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_save_as.setText(getString(R.string.saved));
                tv_save_as.setEnabled(false);

                user.setBalanceScore(testActivity.getScore(1));
                user.setSpeedScore(testActivity.getScore(2));
                user.setChairScore(testActivity.getScore(3));
                user.update(getActivity());
            }
        });


        return view;
    }

}
