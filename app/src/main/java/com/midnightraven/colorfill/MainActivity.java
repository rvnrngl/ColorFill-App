package com.midnightraven.colorfill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GAllERY_REQUEST_CODE = 105;

    AppCompatButton btnGallery, btnCamera, btnSave;
    ImageView selectedImage, img_normal, img_deuteranomaly, img_protanomaly, img_tritanomaly,img_deuteranopia, img_protanopia, img_tritanopia, img_monochromacy;
    LinearLayout bg_normal,bg_deuteranomaly,bg_protanomaly,bg_tritanomaly,bg_deuteranopia,bg_protanopia,bg_tritanopia, bg_monochromacy;
    String currentPhotoPath;
    Drawable drawable;
    BitmapDrawable imgdrawable;
    Bitmap cur_image, imgbitmap;
    Bitmap replaced_image = null; // remove when using the File f //fix the code assosiated

    String imgString, clicked, sType;

    // Result Images
    Bitmap res_deuteranomaly,res_protanomaly,res_tritanomaly;
    Bitmap res_deuteranopia,res_protanopia,res_tritanopia,res_monochromacy;

    OutputStream outputStream;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        // request permission to write external storage
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        // bgs
        bg_normal = findViewById(R.id.bg_normal);
        bg_deuteranomaly = findViewById(R.id.bg_deuteranomaly);
        bg_protanomaly = findViewById(R.id.bg_protanomaly);
        bg_tritanomaly = findViewById(R.id.bg_tritanomaly);
        bg_deuteranopia = findViewById(R.id.bg_deuteranopia);
        bg_protanopia = findViewById(R.id.bg_protanopia);
        bg_tritanopia = findViewById(R.id.bg_tritanopia);
        bg_monochromacy = findViewById(R.id.bg_monochromacy);

        // image processing initialize start
        img_normal = findViewById(R.id.img_normal);
        img_deuteranomaly = findViewById(R.id.img_deuteranomaly);
        img_protanomaly = findViewById(R.id.img_protanomaly);
        img_tritanomaly = findViewById(R.id.img_tritanomaly);
        img_deuteranopia = findViewById(R.id.img_deuteranopia);
        img_protanopia = findViewById(R.id.img_protanopia);
        img_tritanopia = findViewById(R.id.img_tritanopia);
        img_monochromacy = findViewById(R.id.img_monochromacy);

        // image processing initialize end
        selectedImage = findViewById(R.id.image_display);
        btnGallery = findViewById(R.id.btn_gallery);
        btnCamera = findViewById(R.id.btn_Camera);
        btnSave = findViewById(R.id.btn_save);

        bg_normal.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_linear_border));

        //Clicking the Eye test button
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GAllERY_REQUEST_CODE);
            }
        });
        
        //Clicking the Camera button
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        // Clicking the Save Button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request the permission
                    Toast.makeText(MainActivity.this, "Permission is required", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission is granted, save the image
                    Toast.makeText(MainActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
                    //saveImageToGallery();
                    selectedImage.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(selectedImage.getDrawingCache());
                    selectedImage.setDrawingCacheEnabled(false);

                    if (clicked.equals("normal")) {
                        sType = "normal_";
                    } else if (clicked.equals("deuteranomaly")) {
                        sType = "deuteranomaly_";
                    } else if (clicked.equals("protanomaly")) {
                        sType = "protanomaly_";
                    } else if (clicked.equals("tritanomaly")) {
                        sType = "tritanomaly_";
                    } else if (clicked.equals("deuteranopia")) {
                        sType = "deuteranopia_";
                    } else if (clicked.equals("protanopia")) {
                        sType = "protanopia_";
                    } else if (clicked.equals("tritanopia")) {
                        sType = "tritanopia_";
                    } else if (clicked.equals("monochromacy")) {
                        sType = "monochromacy_";
                    }

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String imageFileName = "ColorFill_" + sType + timeStamp + ".jpg";

                    try {
                        // Call the saveImage method to save the bitmap
                        saveImage(bitmap, imageFileName);
                        Toast.makeText(MainActivity.this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        //Output the image process
        initializeOnclickListeners();


        //initial image processing for image preview
        new imageProcessTask().execute();
    }

    private class imageProcessTask extends AsyncTask<Void, Void, Void> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.create();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //getting drawables
            imgdrawable = (BitmapDrawable)selectedImage.getDrawable();
            //convert drawables to bitmap
            imgbitmap = imgdrawable.getBitmap();
            imgString = getStringImage(imgbitmap);
            final Python py = Python.getInstance();
            PyObject pyobj = py.getModule("simulate");
            //call the main function in python file and pass the variable imageString as parameter
            List<PyObject> obj = pyobj.callAttr("main",imgString).asList();
            //obj will return the process image in python file
            String res_0 = obj.get(0).toString();
            String res_1 = obj.get(1).toString();
            String res_2 = obj.get(2).toString();
            String res_3 = obj.get(3).toString();
            String res_4 = obj.get(4).toString();
            String res_5 = obj.get(5).toString();
            String res_6 = obj.get(6).toString();

            byte data_0[] = android.util.Base64.decode(res_0, Base64.DEFAULT);
            byte data_1[] = android.util.Base64.decode(res_1, Base64.DEFAULT);
            byte data_2[] = android.util.Base64.decode(res_2, Base64.DEFAULT);
            byte data_3[] = android.util.Base64.decode(res_3, Base64.DEFAULT);
            byte data_4[] = android.util.Base64.decode(res_4, Base64.DEFAULT);
            byte data_5[] = android.util.Base64.decode(res_5, Base64.DEFAULT);
            byte data_6[] = android.util.Base64.decode(res_6, Base64.DEFAULT);

            res_deuteranopia = BitmapFactory.decodeByteArray(data_0, 0, data_0.length);
            res_protanopia = BitmapFactory.decodeByteArray(data_1, 0, data_1.length);
            res_tritanopia = BitmapFactory.decodeByteArray(data_2, 0, data_2.length);
            res_monochromacy = BitmapFactory.decodeByteArray(data_3, 0, data_3.length);
            res_deuteranomaly = BitmapFactory.decodeByteArray(data_4, 0, data_4.length);
            res_protanomaly = BitmapFactory.decodeByteArray(data_5, 0, data_5.length);
            res_tritanomaly = BitmapFactory.decodeByteArray(data_6, 0, data_6.length);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            dialog.dismiss();
        }
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        //store in byte array
        byte[] imageBytes = baos.toByteArray();
        //then encode to string
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    // Image processing method start

    private void initializeOnclickListeners(){
        //set to normal image
        img_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replaced_image != null) {
                    clicked = "normal";
                    // highlight selected colorblind type
                    bg_normal.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                    bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                    selectedImage.setImageBitmap(replaced_image);
                    TextView title_Textview = findViewById(R.id.title_Textview);
                    TextView description_Textview = findViewById(R.id.description_Textview);
                    // set visibility
                    title_Textview.setVisibility(View.VISIBLE);
                    description_Textview.setVisibility(View.VISIBLE);
                    title_Textview.setText("Normal");
                    description_Textview.setText("(Normal view)");
                    //Handler handler = new Handler();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            title_Textview.setVisibility(View.INVISIBLE);
                            description_Textview.setVisibility(View.INVISIBLE);
                        }
                    },4000); // remove after 5 seconds

                } else {
                    clicked = "normal";
                    // highlight selected colorblind type
                    bg_normal.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                    bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                    bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                    bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                    drawable = getDrawable(R.drawable.image_temp);
                    cur_image = ((BitmapDrawable) drawable).getBitmap();
                    selectedImage.setImageBitmap(cur_image);
                    TextView title_Textview = findViewById(R.id.title_Textview);
                    TextView description_Textview = findViewById(R.id.description_Textview);
                    title_Textview.setVisibility(View.VISIBLE);
                    description_Textview.setVisibility(View.VISIBLE);
                    title_Textview.setText("Normal");
                    description_Textview.setText("(Normal view)");
                    //Handler handler = new Handler();
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            title_Textview.setVisibility(View.INVISIBLE);
                            description_Textview.setVisibility(View.INVISIBLE);
                        }
                    },4000); // remove after 5 seconds
                }
            }
        });

        //set to Deutaranomaly image
        img_deuteranomaly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "deuteranomaly";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_deuteranomaly);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Deuteranomaly");
                description_Textview.setText("(Simulates reduce in Medium cones(green) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });

        //set to Protanomaly image
        img_protanomaly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "protanomaly";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_protanomaly);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Protanomaly");
                description_Textview.setText("(Simulates reduce in Long cones(red) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });

        //set to Tritanomaly image
        img_tritanomaly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "tritanomaly";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_tritanomaly);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Tritanomaly");
                description_Textview.setText("(Simulates reduce in Short cones(blue) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });

        //set to Deuteranopia image
        img_deuteranopia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "deuteranopia";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_deuteranopia);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Deuteranopia");
                description_Textview.setText("(Simulates missing Medium cones(green) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });

        img_protanopia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "protanopia";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_protanopia);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Protanopia");
                description_Textview.setText("(Simulates missing Long cones(red) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });

        img_tritanopia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "tritanopia";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));
                bg_monochromacy.setBackgroundColor(Color.TRANSPARENT);

                selectedImage.setImageBitmap(res_tritanopia);
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                // set Visibility
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Tritanopia");
                description_Textview.setText("(Simulates missing Short cones(blue) vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 2 seconds

            }
        });

        //set to monochromacy image
        img_monochromacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = "monochromacy";
                // highlight selected colorblind type
                bg_normal.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_protanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanomaly.setBackgroundColor(Color.TRANSPARENT);
                bg_deuteranopia.setBackgroundColor(Color.TRANSPARENT);
                bg_protanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_tritanopia.setBackgroundColor(Color.TRANSPARENT);
                bg_monochromacy.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.custom_linear_border));

                selectedImage.setImageBitmap(res_monochromacy);
                // set visibility
                TextView title_Textview = findViewById(R.id.title_Textview);
                TextView description_Textview = findViewById(R.id.description_Textview);
                title_Textview.setVisibility(View.VISIBLE);
                description_Textview.setVisibility(View.VISIBLE);
                title_Textview.setText("Monochromacy");
                description_Textview.setText("(Simulates grayscale vision)");
                //Handler handler = new Handler();
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        title_Textview.setVisibility(View.INVISIBLE);
                        description_Textview.setVisibility(View.INVISIBLE);
                    }
                },4000); // remove after 3 seconds

            }
        });
    }


    // Image processing method end

    private void askCameraPermission() {
        //check if camera permission is granted after clicking the camera button
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            //openCamera(); // just a test
            dispatchTakePictureIntent(); // important
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If camera permission is granted or not
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //openCamera(); // just a test
                dispatchTakePictureIntent(); //important
            } else {
                Toast.makeText(this, "Camera Permission is required to use the app!", Toast.LENGTH_SHORT).show();
            }
        }
        // If write external storage permission is granted or not
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, save the image
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                //saveImageToGallery();
            } else {
                // Permission denied, show an error message
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            //replaced_image = (Bitmap) data.getExtras().get("data");
            //selectedImage.setImageBitmap(replaced_image);
            //imageProcessFunction();

            //use this code below to physical cp, not working in emulator
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                String filePath = f.getPath();
                replaced_image = BitmapFactory.decodeFile(filePath);
                selectedImage.setImageBitmap(replaced_image);
                Toast.makeText(MainActivity.this, "This may take a couple of seconds. Please be patient.", Toast.LENGTH_LONG).show();
                new imageProcessTask().execute();
                //imageProcessFunction();
                //selectedImage.setImageURI(Uri.fromFile(f));
            }
        }

        if (requestCode == GAllERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFilename = "COLORFILL_" + timeStamp +"."+getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFilename);
                // change later below
                try {
                    replaced_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(replaced_image);
                Toast.makeText(MainActivity.this, "This may take a couple of seconds. Please be patient.", Toast.LENGTH_LONG).show();
                new imageProcessTask().execute();
            }
        }

    }

    //file extension
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "COLORFILL_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //save a File
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure that there's a camera activity to handle intent
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            //Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "ColorFill");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "ColorFill";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name);
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    private void saveImageToGallery() {

        selectedImage.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(selectedImage.getDrawingCache());
        selectedImage.setDrawingCacheEnabled(false);
        SaveImageTask task = new SaveImageTask(bitmap, "ColorFill");
        task.execute();

    }

    private class SaveImageTask extends AsyncTask<Void, Void, Uri> {
        private Bitmap bitmap;
        private String folderName;
        private Dialog dialog;

        public SaveImageTask(Bitmap bitmap, String folderName) {
            this.bitmap = bitmap;
            this.folderName = folderName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.create();
            dialog.show();

        }

        @Override
        protected Uri doInBackground(Void... voids) {
            //Log.d("SaveImage", "DoInBackground Working ");
            //String folderPath = Environment.getExternalStorageDirectory() + File.separator + folderName;
            String folderPath;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                folderPath = Environment.getExternalStorageDirectory() + File.separator + folderName;
            } else {
                folderPath = getFilesDir() + File.separator + folderName;
            }


            File folder = new File(folderPath);
            folder.mkdirs();
            if (!folder.exists()) {
                boolean success = folder.mkdirs();
                Log.e("SaveImage", "Boolean: " + success);
                if (!success) {
                    Log.e("SaveImage", "Name of folder: " + folder);
                    Log.e("SaveImage", "Error creating folder: " + folderPath);
                    return null;
                }
            }

            Log.d("SaveImage", "DoInBackground Working again again ");

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "ColorFill_" + timeStamp + ".jpg";

            String filePath = folderPath + File.separator + imageFileName;

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, folderName);
            } else {
                values.put(MediaStore.Images.Media.DATA, filePath);
            }

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.d("SaveImage", "URI: " + uri.toString());
            Log.d("SaveImage", "File path: " + uri.getPath());

            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                return uri;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (uri != null) {

                Toast.makeText(MainActivity.this, "Save image successfully", Toast.LENGTH_SHORT).show();
                String[] paths = {uri.getPath()};
                String[] mimeTypes = {"image/jpeg", "image/png"};
                MediaScannerConnection.scanFile(MainActivity.this, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("SaveImage", "Image saved successfully");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        //startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                Log.d("SaveImage", "Failed to save image");
            }
        }
    }

}