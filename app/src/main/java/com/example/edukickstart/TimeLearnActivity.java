package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TimeLearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_learn);
        getSupportFragmentManager().beginTransaction().add(R.id.time_learn_fragment_container, TimeLearnMenuFragment.newInstance()).commit();
    }
}