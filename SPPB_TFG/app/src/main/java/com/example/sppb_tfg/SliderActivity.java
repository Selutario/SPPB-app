package com.example.sppb_tfg;

import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SliderActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private SliderAdapter sliderAdapter;
    private LinearLayout mDotLayout;
    private ConstraintLayout mSliderLayout;

    private TextView[] mDots;

    private Button mNextButton;
    private Button mBackButton;

    private int mCurrentPage;
    private int mCurrentTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        mCurrentTest = getIntent().getIntExtra("test", mCurrentTest)-1;

        mSliderLayout = (ConstraintLayout) findViewById(R.id.slider_layout);
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dots_layout);
        mNextButton = (Button) findViewById(R.id.btn_next);
        mBackButton = (Button) findViewById(R.id.btn_back);

        setStatusBarColor(mCurrentTest);

        sliderAdapter = new SliderAdapter(this, mCurrentTest);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPage < mDots.length -1){
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                } else {
                    finish();
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentPage > 0){
                    mSlideViewPager.setCurrentItem(mCurrentPage - 1);
                } else {
                    finish();
                }
            }
        });
    }

    public void setStatusBarColor(int test) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            switch (test) {
                case 0:
                    mSliderLayout.setBackgroundColor(getResources().getColor(R.color.colorBalance));
                    window.setStatusBarColor(getResources().getColor(R.color.colorBalanceDark));
                    break;
                case 1:
                    mSliderLayout.setBackgroundColor(getResources().getColor(R.color.colorGaitSpeed));
                    window.setStatusBarColor(getResources().getColor(R.color.colorGaitSpeedDark));
                    break;
                case 2:
                    mSliderLayout.setBackgroundColor(getResources().getColor(R.color.colorChairStand));
                    window.setStatusBarColor(getResources().getColor(R.color.colorChairStandDark));
                    break;
            }
        }
    }

    public void addDotsIndicator(int position) {
        SliderResources sliderResources = new SliderResources();

        mDots = new TextView[sliderResources.slide_headings[mCurrentTest].length];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTextOverBlue));
            mDots[i].setAlpha(0.5f);

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setAlpha(1);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            mCurrentPage = i;

            if(i == 0) {

                mNextButton.setEnabled(true);
/*                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.INVISIBLE);*/

                mNextButton.setText(getResources().getText(R.string.next));
                mBackButton.setText(getString(R.string.skip));

            } else if (i == mDots.length -1) {

                mNextButton.setEnabled(true);
                mBackButton.setEnabled(true);
                /*mBackButton.setVisibility(View.VISIBLE);*/

                mNextButton.setText(getResources().getText(R.string.finish));
                mBackButton.setText(getResources().getText(R.string.back));

            } else {
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                mNextButton.setText(getResources().getText(R.string.next));
                mBackButton.setText(getResources().getText(R.string.back));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

}
