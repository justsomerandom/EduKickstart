package com.example.edukickstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    public void timeMenu(View view) {
        Intent i = new Intent(this, TimePlayActivity.class);
        startActivity(i);
    }

    public void memoryMenu(View view) {
        Intent i = new Intent(this, MemoryPlayActivity.class);
        startActivity(i);
    }

    public void langMenu(View view) {
        Intent i = new Intent(this, LanguagePlayActivity.class);
        startActivity(i);
    }

    public void mathsMenu(View view) {
        Intent i = new Intent(this, MathPlayActivity.class);
        startActivity(i);
    }

    public void visualMenu(View view) {
        Intent i = new Intent(this, VisualPlayActivity.class);
        startActivity(i);
    }

}