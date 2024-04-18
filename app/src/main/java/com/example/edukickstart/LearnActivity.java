package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
    }

    public void timeMenu(View view) {
        Intent i = new Intent(this, TimeLearnActivity.class);
        startActivity(i);
    }

    public void memoryMenu(View view) {
        Intent i = new Intent(this, MemoryLearnActivity.class);
        startActivity(i);
    }

    public void langMenu(View view) {
        Intent i = new Intent(this, LanguageLearnActivity.class);
        startActivity(i);
    }

    public void mathsMenu(View view) {
        Intent i = new Intent(this, MathLearnActivity.class);
        startActivity(i);
    }

    public void visualMenu(View view) {
        Intent i = new Intent(this, VisualLearnActivity.class);
        startActivity(i);
    }
}