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
        aboutText.setText("MildewScan\nOffline mobile application that classifies the severity level of Downy Mildew in Corn plants using CNN Deep Learning.\n It takes in both RGB and Thermal images, having models built for both. \n\n Developed by \n Abou Shaban, Mohammad \n De Asis, Shunsuke \n Hernandez, Emil \n Valenciano, Hans \n\n Special thanks to \n UPLB - IPB \n Mark Montances \n  Kathleen Ann Villanueva  \nEarl Jayson Alvaran \n Ron Anthony Delos Santos \n Joshua Isaguirre");
    }

    public void onBackButtonClicked(android.view.View view) {
        finish(); // Closes AboutActivity and returns to MainActivity
    }

}
