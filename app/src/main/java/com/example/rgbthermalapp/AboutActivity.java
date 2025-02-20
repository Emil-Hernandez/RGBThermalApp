package com.example.rgbthermalapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutTextView);
        aboutText.setText("[App Name (pending)]\n\nOffline mobile application that classifies the severity level of Downy Mildew in Corn plants using CNN Deep Learning.\n\n It takes in both RGB and Thermal images, having models built for both. \n\nDeveloped by \n\n Abou Shaban, Mohammad \n De Asis, Shunsuke \n Hernandez, Emil \n Valenciano, Hans \n\n Special thanks to \n UPLB - IPB");
    }

    public void onBackButtonClicked(android.view.View view) {
        finish(); // Closes AboutActivity and returns to MainActivity
    }

}
