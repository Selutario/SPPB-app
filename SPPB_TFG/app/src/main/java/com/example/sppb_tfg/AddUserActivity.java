package com.example.sppb_tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_age;
    private EditText et_weight;
    private EditText et_height;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btn_add_user;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        et_username = (EditText) findViewById(R.id.et_username);
        et_age = (EditText) findViewById(R.id.et_age);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);
        // UNCOMMENT IF WANT TO ADD MALE/FEMALE
        /*radioGroup = (RadioGroup) findViewById(R.id.radioGroup);*/
        btn_add_user = (Button) findViewById(R.id.btn_add_user);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        settings = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        btn_add_user.setOnClickListener(v -> {
           String username = et_username.getText().toString();
           String age = et_age.getText().toString();
           String weight = et_weight.getText().toString();
           String height = et_height.getText().toString();

           if(username.isEmpty()){
               et_username.setHintTextColor(getResources().getColor(R.color.colorError));
               Toast.makeText(AddUserActivity.this, getString(R.string.need_username), Toast.LENGTH_LONG).show();
           } else {
               // Add user to database
               User user = new User(username, age, weight, height);
               user.insert(AddUserActivity.this);
               long selectedId = user.getId();

               // Mark as selected
               editor.putLong(Constants.SELECTED_USER, selectedId);
               editor.apply();
               finish();
           }


        });
    }




}
