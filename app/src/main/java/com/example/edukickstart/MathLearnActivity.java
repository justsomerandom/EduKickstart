package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MathLearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_learn);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.math_learn_fragment_container, MathLearnMenuFragment.newInstance())
                .commit();
    }
}