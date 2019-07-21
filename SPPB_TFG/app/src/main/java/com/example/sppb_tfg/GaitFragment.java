package com.example.sppb_tfg;

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
    private TextView tv_time;
    private ImageButton btn_play;
    private ImageButton btn_mute;
    private ImageButton btn_info;
    private ImageButton btn_replay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, null);
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
//        tv_time = (TextView) view.findViewById(R.id.tv_time);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        btn_mute = (ImageButton) view.findViewById(R.id.btn_mute);
        btn_info = (ImageButton) view.findViewById(R.id.imageButton5);
        btn_replay = (ImageButton) view.findViewById(R.id.btn_replay);
        cl_info = view.findViewById(R.id.cl_info);

        test_name.setText(getActivity().getResources().getText(R.string.gait_test));
        cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorGaitSpeed));

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).slider_activity(Constants.GAIT_TEST);
            }
        });

        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).fragmentTestCompleted();
            }
        });


        return view;
    }
}