package com.midnightraven.colorfill;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

public class CameraActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout camera_frame;
    showCamera showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_frame = (FrameLayout) findViewById(R.id.camera_frame);

        //open camera

        camera = camera.open();

        //call showCamera class
        showCamera = new showCamera(this, camera);
        camera_frame.addView(showCamera);
    }
}