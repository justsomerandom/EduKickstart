package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LanguagePlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_play);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lang_play_fragment_container, LangPlayMenuFragment.newInstance())
                .commit();
    }
}