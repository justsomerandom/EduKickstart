package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TimePlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_play);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.time_play_fragment_container, TimePlayMenuFragment.newInstance())
                .commit();
    }
}