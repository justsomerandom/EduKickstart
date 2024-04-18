package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LanguageLearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_learn);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lang_learn_fragment_container, LangLearnMenuFragment.newInstance())
                .commit();
    }
}