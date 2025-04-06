package com.example.rgbthermalapp;

import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutTextView);

        // Create a string with the content
        String aboutContent = "MildewScan\n\n" +
                "How to Use?\n\n" +
                "MildewScan does not require internet connectivity to function, making it accessible even in remote agricultural areas. On the home page, you can upload pre-captured thermal or RGB images, or use the camera directly from the app, of corn plants suspected of being infected with Philippine Downy Mildew to analyze their classification. The results will provide information about the severity of the infection." +
                "\n\nAbout Us\n\n" +
                "This app was created by CPE students from De La Salle University Dasmarinas: Mohammad Abou Shaban, Shunsuke De Asis, Emil Joaquin Hernandez, and Hans Valenciano. It aims to simplify the user's lives by enabling early detection of Philippine Downy Mildew in corn. This allows farmers and researchers to promptly remove infected plants, preventing the disease from spreading to other corn crops.";

        // Create SpannableString to apply styles
        SpannableString spannableString = new SpannableString(aboutContent);

        // Apply bold to specific parts
        spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 10, 0); // "MildewScan"
        spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 12, 25, 0); // "How to Use?"
        spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 434, 444, 0); // "About Us"

        // Set the styled text to the TextView
        aboutText.setText(spannableString);

        // Change text size programmatically
        aboutText.setTextSize(13); // You can set the size in SP (scaled pixels)

        // Optional: Make sure the text is centered
        aboutText.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
    }

    public void onBackButtonClicked(android.view.View view) {
        finish(); // Closes AboutActivity and returns to MainActivity
    }
}
