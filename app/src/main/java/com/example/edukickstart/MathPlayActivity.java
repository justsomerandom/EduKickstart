package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MathPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_play);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.math_play_fragment_container, MathPlayMenuFragment.newInstance())
                .commit();
    }
}