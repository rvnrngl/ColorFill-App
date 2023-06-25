package com.midnightraven.colorfill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    AppCompatButton btnEyeTest;
    AppCompatButton btnSimulate;
    TextView showTutorial;
    RelativeLayout homeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        }

        // request permission to write external storage
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        showTutorial = findViewById(R.id.showTutorial);
        showTutorial.setPaintFlags(showTutorial.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnSimulate = findViewById(R.id.btn_Simulate);
        btnEyeTest = findViewById(R.id.btn_EyeTest);
        homeView = findViewById(R.id.homeView);

        showTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShowTutorial();
            }
        });

        //go to simulate page
        btnSimulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        //go to eye test page
        btnEyeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEyeTest();
            }
        });

    }

    private void onClickShowTutorial() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_tutorial, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setAnimationStyle(R.style.animation);
        // set the background of the popup window and its content view to transparent
        //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //((ViewGroup) popupView).setBackgroundResource(android.R.color.transparent);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the popup window
        popupWindow.showAtLocation(homeView, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                popupWindow.setAnimationStyle(R.style.animation);
                return true;
            }
        });

        ImageButton exit = (ImageButton) popupView.findViewById(R.id.img_btn_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.setAnimationStyle(R.style.animation);
                popupWindow.dismiss();
            }
        });

    }

    public void openMainActivity(){
        Intent mainAct = new Intent(this, MainActivity.class);
        startActivity(mainAct);
    }

    public void openEyeTest(){

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_select_test, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        //boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);
        popupWindow.setAnimationStyle(R.style.animation);
        //popupWindow.setAnimationStyle(R.style.animation);
        // set the background of the popup window and its content view to transparent
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the popup window
        popupWindow.showAtLocation(homeView, Gravity.CENTER, 0, 0);

        TextView how2play1 = (TextView) popupView.findViewById(R.id.how2play1);
        TextView how2play2 = (TextView) popupView.findViewById(R.id.how2play2);

        how2play1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.how_to_play_1);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.create();
                dialog.show();
            }
        });

        how2play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.how_to_play_2);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.create();
                dialog.show();
            }
        });

        LinearLayout ishihara_test = (LinearLayout) popupView.findViewById(R.id.ishihara_test);
        ishihara_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ishihara = new Intent(HomeActivity.this, ColorblindTestActivity.class);
                startActivity(ishihara);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.setAnimationStyle(R.style.animation);
                        popupWindow.dismiss();
                    }
                }, 500);

            }
        });

        LinearLayout tapColor_test = (LinearLayout) popupView.findViewById(R.id.tapTheColor);
        tapColor_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tapColor = new Intent(HomeActivity.this, TapColorTestActivity.class);
                startActivity(tapColor);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        popupWindow.setAnimationStyle(R.style.animation);
                        popupWindow.dismiss();
                    }
                }, 500);

            }
        });


        AppCompatButton back = (AppCompatButton) popupView.findViewById(R.id.select_btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.setAnimationStyle(R.style.animation);
                popupWindow.dismiss();
            }
        });


        //Intent eyeTest = new Intent(this, ColorblindTestActivity.class);
        //startActivity(eyeTest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If write external storage permission is granted or not
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, save the image
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show an error message
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Note")
                .setMessage("It is best advice to use this app without using any glasses with colored lenses and any device present screen filter that may affect the accuracy of the app.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

}