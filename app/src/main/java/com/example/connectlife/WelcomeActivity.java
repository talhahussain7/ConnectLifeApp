package com.example.connectlife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.connectlife.fragments.WelcomeFragments.LoginFragment;

public class WelcomeActivity extends AppCompatActivity {
public static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        fragmentManager=getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction().replace(R.id.welcome_container,new LoginFragment(),"Login Fragment").commit();
    }



    @Override
    public void onBackPressed() {

    }
}