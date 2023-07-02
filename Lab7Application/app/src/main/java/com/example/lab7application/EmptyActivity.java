package com.example.lab7application;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        // Retrieve the character from the intent
        Character character = getIntent().getParcelableExtra("character");

        // Create a new instance of DetailsFragment with the character and replace the FrameLayout
        DetailsFragment fragment = DetailsFragment.newInstance(character);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }
}
