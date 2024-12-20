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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rgbthermalapp.ml.Rgbmodel;
import com.example.rgbthermalapp.ml.Thermalmodel;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.DataType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    Button RGBCam, RGBGal, ThermalGal;
    ImageView imageView;
    TextView result;

    int RGBImageSize = 64, ThermalImageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RGBCam = findViewById(R.id.RGBCamBtn);
        RGBGal = findViewById(R.id.RGBGalBtn);
        ThermalGal = findViewById(R.id.ThermalGalBtn);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap image = null;

            if (requestCode == 3) {
                image = (Bitmap) data.getExtras().get("data");
                processImage(image, RGBImageSize, "rgb");
            } else if (requestCode == 4 || requestCode == 5) {
                Uri dat = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                    processImage(image, requestCode == 4 ? RGBImageSize : ThermalImageSize, requestCode == 4 ? "rgb" : "thermal");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processImage(Bitmap image, int size, String type) {
        if (image != null) {
            image = Bitmap.createScaledBitmap(image, size, size, false);
            imageView.setImageBitmap(image);

            try {
                if (type.equals("rgb")) {
                    classifyImage(image, size, Rgbmodel.newInstance(this));
                } else {
                    classifyImage(image, size, Thermalmodel.newInstance(this));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void classifyImage(Bitmap image, int size, Object modelInstance) {
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, size, size, 3}, DataType.FLOAT32);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * size * size * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[size * size];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

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

        if (modelInstance instanceof Rgbmodel) {
            TensorBuffer outputBuffer = ((Rgbmodel) modelInstance).process(inputBuffer).getOutputFeature0AsTensorBuffer();
            float[] confidences = outputBuffer.getFloatArray();
            result.setText(getRGBClassLabel(confidences));
            ((Rgbmodel) modelInstance).close();
        } else if (modelInstance instanceof Thermalmodel) {
            TensorBuffer outputBuffer = ((Thermalmodel) modelInstance).process(inputBuffer).getOutputFeature0AsTensorBuffer();
            float[] confidences = outputBuffer.getFloatArray();
            result.setText(getClassLabel(confidences));
            ((Thermalmodel) modelInstance).close();
        }

    }

    private String getClassLabel(float[] confidences) {
        String[] labels = {"Highly Resistant","Resistant", "Moderately Resistant", "Moderately Susceptible", "Susceptible", "Highly Susceptible"};
        int maxIndex = 0;
        for (int i = 1; i < confidences.length; i++) {
            if (confidences[i] > confidences[maxIndex]) {
                maxIndex = i;
            }
        }
        return labels[maxIndex];
    }

    private String getRGBClassLabel(float[] confidences) {
        String[] labels = {"Healthy Leaves", "Leaf Rust", "Leaf Blight", "Leaf Spot"};
        int maxIndex = 0;
        for (int i = 1; i < confidences.length; i++) {
            if (confidences[i] > confidences[maxIndex]) {
                maxIndex = i;
            }
        }
        return labels[maxIndex];
    }
}
