package com.example.sppb_tfg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    int mCurrentTest;
    LayoutInflater layoutInflater;
    SliderResources sliderResources = new SliderResources();

    public SliderAdapter(Context context, int test) {
        this.context = context;
        mCurrentTest = test;
    }

    @Override
    public int getCount() {
        return sliderResources.slide_headings[mCurrentTest].length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (ScrollView) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int currentSlide) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.iv_slides);
        TextView slideHeading = (TextView) view.findViewById(R.id.tv_slides_header);
        TextView slideDescription = (TextView) view.findViewById(R.id.tv_slides_description);

        // Set slide image, heading and description
        slideImageView.setImageResource(sliderResources.slide_images[mCurrentTest][currentSlide]);
        slideHeading.setText(context.getResources().getString(sliderResources.slide_headings[mCurrentTest][currentSlide]));
        slideDescription.setText(context.getResources().getString(sliderResources.slide_descs[mCurrentTest][currentSlide]));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ScrollView) object);
    }
}
