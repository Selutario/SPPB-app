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

public class BalanceFragment extends Fragment {
    private ConstraintLayout cl_info;
    private TextView test_name;
    private ImageButton imageButton4;
    private ImageButton btn_info;
    private ImageButton imageButton6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test, null);
        test_name = (TextView) view.findViewById(R.id.tv_test_name);
        imageButton4 = (ImageButton) view.findViewById(R.id.imageButton4);
        btn_info = (ImageButton) view.findViewById(R.id.imageButton5);
        imageButton6 = (ImageButton) view.findViewById(R.id.imageButton6);
        cl_info = view.findViewById(R.id.cl_info);

        test_name.setText(getActivity().getResources().getText(R.string.balance_test));
        cl_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBalance));


        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TestActivity)getActivity()).slider_activity(Constants.BALANCE_TEST);
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

        if (settings.getBoolean("FirstUseBalance", true)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("FirstUseBalance", false);
            editor.commit();

            ((TestActivity)getActivity()).slider_activity(Constants.BALANCE_TEST);
        }*/

        return view;
    }

}
