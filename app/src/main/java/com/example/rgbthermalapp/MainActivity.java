package com.example.rgbthermalapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import com.example.rgbthermalapp.ml.Rgbmodel;
import com.example.rgbthermalapp.ml.Thermalmodel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.DataType;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Button RGBCam, RGBGal, ThermalGal;
    int RGBImageSize = 224, ThermalImageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        RGBCam = findViewById(R.id.phoneCameraButton);
        RGBGal = findViewById(R.id.browseGalleryButton);
        ThermalGal = findViewById(R.id.thermalGalleryButton);

        RGBCam.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camIntent, 3);
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        });

        RGBGal.setOnClickListener(v -> {
            Intent RGBGalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(RGBGalIntent, 4);
        });

        ThermalGal.setOnClickListener(v -> {
            Intent ThermalGalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(ThermalGalIntent, 5);
        });

        Button classInfoButton = findViewById(R.id.classificationButton);
        classInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassInfoActivity.class);
            startActivity(intent);
        });

        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap originalImage; // Store the original image

            if (requestCode == 3) {
                originalImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                processImage(originalImage, RGBImageSize, "rgb");
            } else if (requestCode == 4 || requestCode == 5) {
                Uri dat = data.getData();
                try {
                    originalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                    processImage(originalImage, requestCode == 4 ? RGBImageSize : ThermalImageSize, requestCode == 4 ? "rgb" : "thermal");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void processImage(Bitmap originalImage, int size, String type) {
        if (originalImage != null) {
            Bitmap resizedImage = Bitmap.createScaledBitmap(originalImage, size, size, false);

            try {
                if (type.equals("rgb")) {
                    classifyImage(originalImage, resizedImage, size, Rgbmodel.newInstance(this));
                } else {
                    classifyImage(originalImage, resizedImage, size, Thermalmodel.newInstance(this));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void classifyImage(Bitmap originalImage, Bitmap resizedImage, int size, Object modelInstance) {
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, size, size, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * size * size * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[size * size];
        resizedImage.getPixels(intValues, 0, resizedImage.getWidth(), 0, 0, resizedImage.getWidth(), resizedImage.getHeight());

        int pixel = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int val = intValues[pixel++];
                byteBuffer.putFloat(((val >> 16) & 0xFF) / 255.0f);
                byteBuffer.putFloat(((val >> 8) & 0xFF) / 255.0f);
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }

        inputBuffer.loadBuffer(byteBuffer);

        String result;
        String confidenceValue = "";

        if (modelInstance instanceof Rgbmodel) {
            TensorBuffer outputBuffer = ((Rgbmodel) modelInstance).process(inputBuffer).getOutputFeature0AsTensorBuffer();
            float[] confidences = outputBuffer.getFloatArray();
            result = getClassLabel(confidences);
            confidenceValue = String.format("%.2f", confidences[getMaxIndex(confidences)] * 100) + "%";
            ((Rgbmodel) modelInstance).close();
        } else {
            TensorBuffer outputBuffer = ((Thermalmodel) modelInstance).process(inputBuffer).getOutputFeature0AsTensorBuffer();
            float[] confidences = outputBuffer.getFloatArray();
            result = getClassLabel(confidences);
            confidenceValue = String.format("%.2f", confidences[getMaxIndex(confidences)] * 100) + "%";
            ((Thermalmodel) modelInstance).close();
        }

        // Store the original image before starting ClassificationActivity
        ImageStorage.setImage(originalImage);

        Intent intent = new Intent(MainActivity.this, ClassificationActivity.class);
        intent.putExtra("result", result);
        intent.putExtra("confidence", confidenceValue);
        startActivity(intent);
    }


    private String getClassLabel(float[] confidences) {
        String[] labels = {"Highly Resistant", "Resistant", "Moderately Resistant", "Moderately Susceptible", "Susceptible", "Highly Susceptible"};
        return labels[getMaxIndex(confidences)];
    }

    private int getMaxIndex(float[] confidences) {
        int maxIndex = 0;
        for (int i = 1; i < confidences.length; i++) {
            if (confidences[i] > confidences[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }


}
