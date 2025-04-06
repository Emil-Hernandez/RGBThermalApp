package com.example.rgbthermalapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ClassInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_info); // Link to your XML
    }

    public void onBackButtonClicked(android.view.View view) {
        finish(); // Closes AboutActivity and returns to MainActivity
    }
}
