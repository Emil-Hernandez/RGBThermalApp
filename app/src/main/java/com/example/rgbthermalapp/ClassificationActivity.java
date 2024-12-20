package com.example.rgbthermalapp;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassificationActivity extends AppCompatActivity {

    ImageView imageView;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.result);

        // Receive the data passed from MainActivity
        Bitmap image = getIntent().getParcelableExtra("image");
        String result = getIntent().getStringExtra("result");

        // Set the image and result text
        if (image != null) {
            imageView.setImageBitmap(image);  // Display the image in ImageView
        }
        resultTextView.setText(result);  // Display the result
    }
}
