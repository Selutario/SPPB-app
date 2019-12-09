package com.example.sppb_tfg;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {
    private Button btn_full;
    private Button btn_balance;
    private Button btn_gait;
    private Button btn_chair;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,
                null);

        btn_full = (Button) view.findViewById(R.id.btn_full);
        btn_balance = (Button) view.findViewById(R.id.btn_balance);
        btn_gait = (Button) view.findViewById(R.id.btn_gait);
        btn_chair = (Button) view.findViewById(R.id.btn_chair);

        checkAccelerometer();

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

    // Open test in a new activity, send test clicked by the user.
    private void test_activity(int test) {
        Intent intent = new Intent(getActivity(), TestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("test_number", test);
        startActivity(intent);
    }

    // If the device has no accelerometer, the user won't be able to use the app in order to avoid errors
    public void checkAccelerometer() {
        PackageManager manager = getActivity().getPackageManager();
        boolean hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        if (!hasAccelerometer) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(getString(R.string.no_accelerometer_title));
            dialogBuilder.setMessage(getString(R.string.no_accelerometer_descp));
            dialogBuilder.setCancelable(false);

            AlertDialog b = dialogBuilder.create();
            b.show();
        }
    }


}
