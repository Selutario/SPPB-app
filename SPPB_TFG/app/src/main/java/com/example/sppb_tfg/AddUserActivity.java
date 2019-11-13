package com.example.sppb_tfg;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddUserActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_age;
    private EditText et_weight;
    private EditText et_height;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btn_add_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        et_username = (EditText) findViewById(R.id.et_username);
        et_age = (EditText) findViewById(R.id.et_age);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btn_add_user = (Button) findViewById(R.id.btn_add_user);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorGaitSpeed));


        btn_add_user.setOnClickListener(v -> {

        });
        /*btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

    }




}
