package com.example.sppb_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private TextView mTextMessage;
    private Button btn_full;
    private Button btn_balance;
    private Button btn_gait;
    private Button btn_chair;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,
                null);

        mTextMessage = (TextView) view.findViewById(R.id.title_users);
        btn_full = (Button) view.findViewById(R.id.btn_full);
        btn_balance = (Button) view.findViewById(R.id.btn_balance);
        btn_gait = (Button) view.findViewById(R.id.btn_gait);
        btn_chair = (Button) view.findViewById(R.id.btn_chair);


        btn_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_activity(0);
            }
        });

        btn_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_activity(1);
            }
        });

        btn_gait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_activity(2);
            }
        });

        btn_chair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test_activity(3);
            }
        });

        return view;
    }

    private void test_activity(int test){
        Intent intent = new Intent(getActivity(), TestActivity.class);
        intent.putExtra("test_number", test);
        startActivity(intent);
    }
}
