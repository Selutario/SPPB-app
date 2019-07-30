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

public class ScoreFragment extends Fragment {
    ConstraintLayout score_layout;
    ProgressBar progressBar;
    ConstraintLayout btn_save;
    TextView tv_save_as;
    SharedPreferences sharedPreferences;

    TextView tv_score;

    ImageView iv_balance_color;
    TextView tv_balance_score_label;
    TextView tv_balance_score;

    ImageView iv_gait_color;
    TextView tv_gait_score_label;
    TextView tv_gait_score;

    ImageView iv_chair_color;
    TextView tv_chair_score;
    TextView tv_chair_score_label;

    User user;
    int mCurrentTest;

    TestActivity testActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_score, null);
        score_layout = view.findViewById(R.id.fragment_score);
        progressBar = (ProgressBar) view.findViewById(R.id.score_progressbar);
        btn_save = view.findViewById(R.id.btn_save);
        tv_save_as = (TextView) view.findViewById(R.id.tv_save_as);

        tv_score = (TextView) view.findViewById(R.id.tv_final_score);
        iv_balance_color = (ImageView) view.findViewById(R.id.iv_balance_color);
        tv_balance_score_label = (TextView) view.findViewById(R.id.tv_balance_score_label);
        tv_balance_score = (TextView) view.findViewById(R.id.tv_balance_score);

        iv_gait_color = (ImageView) view.findViewById(R.id.iv_gait_color);
        tv_gait_score_label = (TextView) view.findViewById(R.id.tv_gait_score_label);
        tv_gait_score = (TextView) view.findViewById(R.id.tv_gait_score);

        iv_chair_color = (ImageView) view.findViewById(R.id.iv_chair_color);
        tv_chair_score_label = (TextView) view.findViewById(R.id.tv_chair_score_label);
        tv_chair_score = (TextView) view.findViewById(R.id.tv_chair_score);

        testActivity = ((TestActivity)getActivity());

        mCurrentTest = testActivity.getmCurrentTest();
        final int score = testActivity.getScore(mCurrentTest);
        final int pb_score;

        if(mCurrentTest == 0){
            pb_score = 9*score - (9*score)/18;
        } else {
            pb_score = 25*score;

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
                btn_save.setEnabled(false);

/*                Date c = Calendar.getInstance().getTime();
                Toast.makeText(getActivity(), "Current time => " + c, Toast.LENGTH_LONG);

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);*/

                if (tv_balance_score.getVisibility() == View.VISIBLE)
                    user.setBalanceScore(testActivity.getScore(1));
                if (tv_gait_score.getVisibility() == View.VISIBLE)
                    user.setSpeedScore(testActivity.getScore(2));
                if (tv_chair_score.getVisibility() == View.VISIBLE)
                    user.setChairScore(testActivity.getScore(3));
                user.update(getActivity());
            }
        });


        return view;
    }

}
