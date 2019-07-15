package com.example.sppb_tfg;

import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    private ConstraintLayout cl_info;
    private int test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        cl_info = findViewById(R.id.cl_info);
        int test_number = getIntent().getIntExtra("test_number", test);

        switch (test_number){
            case 0:
                cl_info.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                break;
            case 1:
                cl_info.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBalance));
                break;
            case 2:
                cl_info.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGaitSpeed));
                break;
            case 3:
                cl_info.setBackgroundColor(ContextCompat.getColor(this, R.color.colorChairStand));
                break;
        }



    }
}
