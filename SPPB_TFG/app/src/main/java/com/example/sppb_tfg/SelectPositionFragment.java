package com.example.sppb_tfg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/*
* Show two options, so the user can choose to perform Chair test with device on the chest or leg.
*/
public class SelectPositionFragment extends Fragment {

    ImageButton btn_chest;
    ImageButton btn_thigh;

    TestActivity testActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_select_position, null);
        btn_chest = (ImageButton) view.findViewById(R.id.btn_chest);
        btn_thigh = (ImageButton) view.findViewById(R.id.btn_thigh);

        testActivity = ((TestActivity)getActivity());


        btn_chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChairChestFragment chairChestFragment = new ChairChestFragment();
                testActivity.openFragment(chairChestFragment, true);
            }
        });

        btn_thigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChairThighFragment chairThighFragment = new ChairThighFragment();
                testActivity.openFragment(chairThighFragment, true);
            }
        });

        return view;
    }

}