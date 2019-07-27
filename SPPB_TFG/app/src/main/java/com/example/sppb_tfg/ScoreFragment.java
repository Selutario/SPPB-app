package com.example.sppb_tfg;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScoreFragment extends Fragment {
    ConstraintLayout score_layout;
    TextView tv_score;
    ProgressBar progressBar;

    int mCurrentTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        score_layout = view.findViewById(R.id.fragment_score);
        tv_score = (TextView) view.findViewById(R.id.tv_final_score);
        progressBar = (ProgressBar) view.findViewById(R.id.score_progressbar);

        mCurrentTest = ((TestActivity)getActivity()).getmCurrentTest();
        final int score = ((TestActivity)getActivity()).getScore(mCurrentTest);

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
        }, 500); //will start animation in 2 seconds




        return view;
    }

}
