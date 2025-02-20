package com.example.rgbthermalapp;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassificationActivity extends AppCompatActivity {

    ImageView imageView;
    TextView resultTextView, confidenceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classification);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.result);
        confidenceTextView = findViewById(R.id.confidenceTextView);

        String result = getIntent().getStringExtra("result");
        String confidence = getIntent().getStringExtra("confidence");

        Bitmap image = ImageStorage.getImage(); // Retrieve the original image
        if (image != null) {
            imageView.setImageBitmap(image);
        }

        resultTextView.setText(result);
        confidenceTextView.setText("Confidence: " + confidence);
    }



    public void onBackButtonClicked(android.view.View view) {
        finish(); // Closes ClassificationActivity and returns to MainActivity
    }
}
