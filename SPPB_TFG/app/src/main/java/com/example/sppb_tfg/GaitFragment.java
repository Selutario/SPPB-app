package com.example.sppb_tfg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class GaitFragment extends Fragment {

    private ConstraintLayout cl_info;
    private TextView test_name;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_test, null);
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
        imageButton4 = (ImageButton) view.findViewById(R.id.imageButton4);
        imageButton5 = (ImageButton) view.findViewById(R.id.imageButton5);
        imageButton6 = (ImageButton) view.findViewById(R.id.imageButton6);
        cl_info = view.findViewById(R.id.cl_info);

        test_name.setText(getActivity().getResources().getText(R.string.gait_test));
        cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));

        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).slider_activity(Constants.GAIT_TEST);
            }
        });

        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).fragmentTestCompleted();
            }
        });


/*        // SHOW INSTRUCTIONS FIRST TIME
        SharedPreferences settings =
                getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.getBoolean("FirstUseGait", true)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FirstUseGait", false);
            editor.commit();

            ((TestActivity)getActivity()).slider_activity(Constants.GAIT_TEST);
        }*/

        return view;
    }
}