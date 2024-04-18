package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MemoryPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_play);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.memory_play_fragment_container, MemoryPlayMenuFragment.newInstance())
                .commit();
    }
}