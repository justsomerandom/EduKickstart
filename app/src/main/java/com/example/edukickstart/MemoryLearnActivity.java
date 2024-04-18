package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MemoryLearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_learn);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.memory_learn_fragment_container, MemoryLearnMenuFragment.newInstance())
                .commit();
    }
}