package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class VisualPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_play);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.visual_play_fragment_container, VisualPlayMenuFragment.newInstance())
                .commit();
    }
}