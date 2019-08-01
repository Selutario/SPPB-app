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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.sppb_tfg.Constants.SELECTED_USER;

public class ScoreFragment extends Fragment {
    ConstraintLayout score_layout;
    ConstraintLayout constraing_explaining;

    ProgressBar progressBar;
    ConstraintLayout btn_save;
    TextView tv_save_as;
    SharedPreferences sharedPreferences;

    TextView tv_score;
    TextView tv_explaining_label;

    ImageView iv_balance_color;
    TextView tv_balance_score_label;
    TextView tv_balance_score;

    ImageView iv_gait_color;
    TextView tv_gait_score_label;
    TextView tv_gait_score;

    ImageView iv_chair_color;
    TextView tv_chair_score;
    TextView tv_chair_score_label;

    User mUser;
    int score = 0;
    int mCurrentTest = 0;
    int mBalanceScore = 0;
    int mGaitScore = 0;
    int mChairScore = 0;

    TestActivity testActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        score_layout = view.findViewById(R.id.fragment_score);
        constraing_explaining = view.findViewById(R.id.constraing_explaining);
        progressBar = (ProgressBar) view.findViewById(R.id.score_progressbar);
        btn_save = view.findViewById(R.id.btn_save);
        tv_save_as = (TextView) view.findViewById(R.id.tv_save_as);

        tv_score = (TextView) view.findViewById(R.id.tv_final_score);
        tv_explaining_label = (TextView) view.findViewById(R.id.tv_explaining_label);

        iv_balance_color = (ImageView) view.findViewById(R.id.iv_balance_color);
        tv_balance_score_label = (TextView) view.findViewById(R.id.tv_balance_score_label);
        tv_balance_score = (TextView) view.findViewById(R.id.tv_balance_score);

        iv_gait_color = (ImageView) view.findViewById(R.id.iv_gait_color);
        tv_gait_score_label = (TextView) view.findViewById(R.id.tv_gait_score_label);
        tv_gait_score = (TextView) view.findViewById(R.id.tv_gait_score);

        iv_chair_color = (ImageView) view.findViewById(R.id.iv_chair_color);
        tv_chair_score_label = (TextView) view.findViewById(R.id.tv_chair_score_label);
        tv_chair_score = (TextView) view.findViewById(R.id.tv_chair_score);

        Long id = -1L;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getLong(SELECTED_USER);
        }

        if (id == -1) {
            testActivity = ((TestActivity)getActivity());

            score = testActivity.getScore(mCurrentTest);
            mCurrentTest = testActivity.getmCurrentTest();

            mBalanceScore = testActivity.getScore(1);
            mGaitScore = testActivity.getScore(2);
            mChairScore = testActivity.getScore(3);
        } else {
            User user = User.getUser(getActivity(), id);

            score = user.getScore();
            mBalanceScore = user.getBalanceScore();
            mGaitScore = user.getSpeedScore();
            mChairScore = user.getChairScore();
        }

        final int pb_score;

        if(mCurrentTest == 0){
            pb_score = 9*score - (9*score)/18;
            constraing_explaining.setVisibility(View.VISIBLE);

            if(score <= 3){
                tv_explaining_label.setText(getString(R.string.severe));
            } else if (score <= 6) {
                tv_explaining_label.setText(getString(R.string.moderate));
            } else if (score <= 9) {
                tv_explaining_label.setText(getString(R.string.slight));
            } else {
                tv_explaining_label.setText(getString(R.string.minimum));
            }
        } else {
            pb_score = 25*score;
            constraing_explaining.setVisibility(View.GONE);

            if (mCurrentTest == 1){
                iv_gait_color.setVisibility(View.GONE);
                tv_gait_score_label.setVisibility(View.GONE);
                tv_gait_score.setVisibility(View.GONE);

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

                iv_gait_color.setVisibility(View.GONE);
                tv_gait_score_label.setVisibility(View.GONE);
                tv_gait_score.setVisibility(View.GONE);
            }
        }

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

        tv_balance_score.setText(Integer.toString(mBalanceScore));
        tv_gait_score.setText(Integer.toString(mGaitScore));
        tv_chair_score.setText(Integer.toString(mChairScore));

        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        long selectedId = sharedPreferences.getLong("SelectedUser", -1);

        if(selectedId != -1 && id == -1) {
            btn_save.setVisibility(View.VISIBLE);
            mUser = User.getUser(getActivity(), selectedId);

            Resources res = getResources();
            String text = String.format(res.getString(R.string.save_as), mUser.getName());
            tv_save_as.setText(text);
        } else {
            btn_save.setVisibility(View.GONE);

        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_save_as.setText(getString(R.string.saved));
                btn_save.setEnabled(false);

                if (tv_balance_score.getVisibility() == View.VISIBLE)
                    mUser.setBalanceScore(testActivity.getScore(1));
                if (tv_gait_score.getVisibility() == View.VISIBLE)
                    mUser.setSpeedScore(testActivity.getScore(2));
                if (tv_chair_score.getVisibility() == View.VISIBLE)
                    mUser.setChairScore(testActivity.getScore(3));
                mUser.update(getActivity());
            }
        });


        return view;
    }
}
