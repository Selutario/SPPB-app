package com.example.sppb_tfg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    int currentFragment = 0;

    // Bottom navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentFragment = 0;
                    MainFragment mainFragment = new MainFragment();
                    openFragment(mainFragment);
                    return true;
                case R.id.navigation_dashboard:
                    currentFragment = 1;
                    FragmentUsers fragmentUsers = new FragmentUsers();
                    openFragment(fragmentUsers);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    // Reopen the fragment which was opened before closing
    @Override
    protected void onResume() {
        super.onResume();
        switch (currentFragment){
            case 0:
                MainFragment mainFragment = new MainFragment();
                openFragment(mainFragment);
                break;
            case 1:
                FragmentUsers fragmentUsers = new FragmentUsers();
                openFragment(fragmentUsers);
                break;
        }
    }

    // Store current open fragment before closing
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragment", currentFragment);
    }

    // Restore current fragment
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragment = savedInstanceState.getInt("currentFragment");
    }


    private  void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_placeHolder, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    // Close app if backpressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
