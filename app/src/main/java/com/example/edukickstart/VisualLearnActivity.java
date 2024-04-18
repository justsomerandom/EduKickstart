package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class VisualLearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_learn);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.visual_learn_fragment_container, VisualLearnMenuFragment.newInstance())
                .commit();
    }
}