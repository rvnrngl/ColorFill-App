package com.midnightraven.colorfill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ColorblindTestActivity extends AppCompatActivity {

    //initialized progressbar
    EditText inputText;
    ImageView imageTest;
    TextView txtProgress;
    ProgressBar progressBar;
    AppCompatButton btn_confirm, btn_skip, btn_back;
    RelativeLayout rView;

    //create an ArrayList object
    private ArrayList<Integer> imageRG = new ArrayList<>(Arrays.asList(R.drawable.img_rg_10, R.drawable.img_rg_18, R.drawable.img_rg_26, R.drawable.img_rg_29, R.drawable.img_rg_33, R.drawable.img_rg_44, R.drawable.img_rg_50, R.drawable.img_rg_63, R.drawable.img_rg_69, R.drawable.img_rg_75));
    private ArrayList<Integer> imageBY = new ArrayList<>(Arrays.asList(R.drawable.img_by_12, R.drawable.img_by_13, R.drawable.img_by_19, R.drawable.img_by_25, R.drawable.img_by_35, R.drawable.img_by_36, R.drawable.img_by_38, R.drawable.img_by_40, R.drawable.img_by_46, R.drawable.img_by_62));
    private ArrayList<String> imageNames = new ArrayList<>(Arrays.asList("img_rg_10", "img_rg_18", "img_rg_26", "img_rg_29", "img_rg_33", "img_rg_44", "img_rg_50", "img_rg_63", "img_rg_69", "img_rg_75", "img_by_12", "img_by_13", "img_by_19", "img_by_25", "img_by_35", "img_by_36", "img_by_38", "img_by_40", "img_by_46", "img_by_62"));
    private ArrayList<Integer> imageIds = new ArrayList<>();

    //test results
    int res_RG,res_BY,res_MONO,res_NORMAL,res_SKIP, res_INC;

    private int currentIndexes = -1;
    int ctr,store;
    int progress = 0;

    boolean firstExecute = true;
    boolean skipped = false;

    private String[] arr_imgType = {"","","","","","","","","","","","","","","","","","","",""}; // store 20
    private int[] arr_imgNumber = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};  //store 20

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorblind_test);

        //initialize
        rView = findViewById(R.id.rView);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_skip = findViewById(R.id.btn_skip);
        btn_back = findViewById(R.id.btn_back);
        inputText = findViewById(R.id.editText);
        imageTest = findViewById(R.id.imageTest);
        txtProgress = findViewById(R.id.txtProgress);
        progressBar = findViewById(R.id.progressBar);



        inputText.setInputType(InputType.TYPE_CLASS_NUMBER);

        //combine both arraylist
        imageIds.addAll(imageRG);
        imageIds.addAll(imageBY);

        if(firstExecute == true) {
            imageTestTask task = new imageTestTask(inputText,imageTest, txtProgress, progressBar,imageIds,imageNames,currentIndexes, firstExecute);
            task.execute();
            firstExecute = false;
            Log.d(String.valueOf(this),"First execute is equal to " + firstExecute);
        }
        //confirm answer
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputText.getText())) {
                    Log.d(String.valueOf(this),"Enter a number or skip!");
                    Toast.makeText(ColorblindTestActivity.this, "Enter a number or skip!", Toast.LENGTH_SHORT).show();
                } else if(!inputText.getText().toString().matches("[0-9]+")) {
                    Toast.makeText(ColorblindTestActivity.this, "Enter only number!", Toast.LENGTH_SHORT).show();
                } else {
                    imageTestTask task = new imageTestTask(inputText, imageTest, txtProgress, progressBar, imageIds, imageNames, currentIndexes, firstExecute);
                    task.execute();
                }

            }
        });

        // skipped the image
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipped = true;
                Log.d(String.valueOf(this),"skipped!");
                imageTestTask task = new imageTestTask(inputText, imageTest, txtProgress, progressBar, imageIds, imageNames, currentIndexes, firstExecute);
                task.execute();
            }
        });

        // back to home
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

    }

    private class imageTestTask extends AsyncTask<Void, Void, Integer> {

        private boolean firstExecute;
        private int currentIndex;
        private ArrayList<String> imageName;
        private ArrayList<Integer> imageId;
        private EditText inputText;
        private ImageView imageView;
        private TextView txtProgress;
        private ProgressBar progressBar;
        private Random random;
        private int resourceId;
        private int randomIndex;
        private int imgNumber;
        private String imgName;
        private String imgType;
        private View view;

        public imageTestTask(EditText inputText, ImageView imageView, TextView txtProgress, ProgressBar progressBar, ArrayList<Integer> imageIds, ArrayList<String> imageNames, int currentIndexes, boolean firstExecute) {
            this.firstExecute = firstExecute;
            this.inputText = inputText;
            this.imageView = imageView;
            this.txtProgress = txtProgress;
            this.progressBar = progressBar;
            this.imageId = new ArrayList<>(imageIds);
            this.imageName = new ArrayList<>(imageNames);
            this.currentIndex = currentIndexes;
        }

        @Override
        protected void onPreExecute() {
            txtProgress.setText("Progress " + progressBar.getProgress() + "%");
            progressBar.setProgress(progressBar.getProgress());
            random = new Random();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            // This method simulates loading a bitmap from a remote source
            if (imageIds.isEmpty()) {
                return null;
            } else {
                randomIndex = random.nextInt(imageId.size());
                while (randomIndex == currentIndex) {
                    randomIndex = random.nextInt(imageId.size());
                }

                return randomIndex;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result == null) {
                //for (int len = 0; len < arr_imgNumber.length; len++) {
                //    Log.d(String.valueOf(this), "Array Image Number["+ len +"]: " + arr_imgNumber[len] + "Array Image Type["+ len +"]: " + arr_imgType[len]);
                //}
                //last result to store
                if(skipped) {
                    // if user answer skipped
                    res_INC = res_INC + 1;
                    Log.d(String.valueOf(this), "Skip Answer ");
                    if(arr_imgType[store-1].equals("rg")) {
                        // if user answer incorrectly
                        res_RG = res_RG + 1;
                        Log.d(String.valueOf(this), "Store Image in res_RG ");
                    } else if(arr_imgType[store-1].equals("by")) {
                        // if user answer incorrectly
                        res_BY = res_BY + 1;
                        Log.d(String.valueOf(this), "Store Image in res_BY ");
                    } else if(arr_imgType[store-1].equals("mo")) {
                        // if user answer incorrectly
                        res_MONO = res_MONO + 1;
                        Log.d(String.valueOf(this), "Store Image in res_MONO ");
                    }
                    skipped = false;
                } else if(Integer.parseInt(inputText.getText().toString())  == arr_imgNumber[store-1]) {
                    Log.d(String.valueOf(this), "Correct Answer ");
                    // if user answer correctly
                    res_NORMAL = res_NORMAL + 1;
                    Log.d(String.valueOf(this), "Store Image in res_NORMAL ");
                } else {
                    res_INC = res_INC + 1;
                    Log.d(String.valueOf(this), "Incorrect Answer ");
                    if(arr_imgType[store-1].equals("rg")) {
                        // if user answer incorrectly
                        res_RG = res_RG + 1;
                        Log.d(String.valueOf(this), "Store Image in res_RG ");
                    } else if(arr_imgType[store-1].equals("by")) {
                        // if user answer incorrectly
                        res_BY = res_BY + 1;
                        Log.d(String.valueOf(this), "Store Image in res_BY ");
                    } else if(arr_imgType[store-1].equals("mo")) {
                        // if user answer incorrectly
                        res_MONO = res_MONO + 1;
                        Log.d(String.valueOf(this), "Store Image in res_MONO ");
                    }

                }

                Log.d(String.valueOf(this), "res_NORMAL: " + res_NORMAL + " res_RG: " + res_RG + " res_BY: " + res_BY + " res_MONO: " + res_MONO + " res_SKIP: " + res_SKIP + " res_INC: " + res_INC);

                Log.d(String.valueOf(this),"No image remaining");
                Log.d(String.valueOf(this), "Test Done");

                resultPage();

            } else {
                currentIndex = result;
                // get the image
                resourceId = imageIds.get(currentIndex);
                // get the image name
                imgName = imageNames.get(currentIndex);
                // get the image type
                imgType = imgName.substring(imgName.length() - 5);
                imgType = imgType.substring(0,2);
                // get the image correct answer/number
                imgNumber = Integer.parseInt(imgName.substring(imgName.length() - 2));
                // store images type and number
                ctr = ctr + 1;
                for (int x = store; x < ctr; x++) {
                    arr_imgType[store] = imgType;
                    arr_imgNumber[store] = imgNumber;
                }
                store++;
                Log.d(String.valueOf(this), "Ctr = " + ctr);
                Log.d(String.valueOf(this), "Image appeared: " + imgName + " Image Type: " + imgType + " Image Number: " + imgNumber);
                // first execute is true
                if(firstExecute == false) {
                    //store the results
                    if(skipped) {
                        res_INC = res_INC + 1;
                        // if user answer skipped
                        Log.d(String.valueOf(this), "Skip Answer ");
                        if(arr_imgType[store-2].equals("rg")) {
                            // if user answer incorrectly
                            res_RG = res_RG + 1;
                            Log.d(String.valueOf(this), "Store Image in res_RG ");
                        } else if(arr_imgType[store-2].equals("by")) {
                            // if user answer incorrectly
                            res_BY = res_BY + 1;
                            Log.d(String.valueOf(this), "Store Image in res_BY ");
                        } else if(arr_imgType[store-1].equals("mo")) {
                            // if user answer incorrectly
                            res_MONO = res_MONO + 1;
                            Log.d(String.valueOf(this), "Store Image in res_MONO ");
                        }
                        skipped = false;
                    }else if(Integer.parseInt(inputText.getText().toString())  == arr_imgNumber[store-2]) {
                        Log.d(String.valueOf(this), "Correct Answer ");
                        // if user answer correctly
                        res_NORMAL = res_NORMAL + 1;
                        Log.d(String.valueOf(this), "Store Image in res_NORMAL ");
                    } else {
                        res_INC = res_INC + 1;
                        Log.d(String.valueOf(this), "Incorrect Answer ");
                        if(arr_imgType[store-2].equals("rg")) {
                            // if user answer incorrectly
                            res_RG = res_RG + 1;
                            Log.d(String.valueOf(this), "Store Image in res_RG ");
                        } else if(arr_imgType[store-2].equals("by")) {
                            // if user answer incorrectly
                            res_BY = res_BY + 1;
                            Log.d(String.valueOf(this), "Store Image in res_BY ");
                        } else if(arr_imgType[store-1].equals("mo")) {
                            // if user answer incorrectly
                            res_MONO = res_MONO + 1;
                            Log.d(String.valueOf(this), "Store Image in res_MONO ");
                        }

                    }

                    Log.d(String.valueOf(this), "res_NORMAL: " + res_NORMAL + " res_RG: " + res_RG + " res_BY: " + res_BY + " res_MONO: " + res_MONO + " res_SKIP: " + res_SKIP + " res_INC: " + res_INC);

                }

                // remove the displayed image
                imageIds.remove(currentIndex);
                imageNames.remove(currentIndex);
                // set progress bar
                Integer s_progress = 5;
                if(firstExecute){
                    progressBar.setProgress((int) (progressBar.getProgress() + s_progress * 0));
                } else {
                    progressBar.setProgress((int) (progressBar.getProgress() + s_progress));
                }
                txtProgress.setText("Progress " + progressBar.getProgress() + "%");
                imageTest.setImageResource(resourceId);
                inputText.getText().clear();
            }

        }


    }

    private void resultPage() {
        Dialog dialog;
        dialog = new Dialog(ColorblindTestActivity.this);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.result_page, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;
                //boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);

                // set the background of the popup window and its content view to transparent
                //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //((ViewGroup) popupView).setBackgroundResource(android.R.color.transparent);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // show the popup window
                popupWindow.showAtLocation(rView, Gravity.CENTER, 0, 0);

                TextView txtLikely = (TextView) popupView.findViewById(R.id.txtLikely);
                TextView resultType = (TextView) popupView.findViewById(R.id.resultType);
                TextView rgPercent = (TextView) popupView.findViewById(R.id.rgPercent);
                TextView byPercent = (TextView) popupView.findViewById(R.id.byPercent);
                TextView monoPercent = (TextView) popupView.findViewById(R.id.monoPercent);
                TextView normalPercent = (TextView) popupView.findViewById(R.id.normalPercent);

                int total = res_NORMAL + res_RG + res_BY + res_MONO; // res_SKIP not yet modified if skip store the data to the type of image, then remove the res_SKIP
                int normal_percent = 100 * res_NORMAL / total;
                int rg_percent = 100 * res_RG / total;
                int by_percent = 100 * res_BY / total;
                int mono_percent = 100 * res_MONO / total;
                int inc_percent = 100 * res_INC / total;
                // Display percent
                if(res_INC >= 0.90 * total) { // if incorrect answer is greater than 90 percent then monochromatic
                    txtLikely.setText("Most Likely");
                    resultType.setText("MONOCHROMATIC");
                    // set percentage
                    normalPercent.setText(normal_percent + "%");
                    rgPercent.setText(0 * rg_percent + "%");
                    byPercent.setText(0 * by_percent + "%");
                    monoPercent.setText(inc_percent + "%");
                } else if(res_NORMAL >= 0.80 * total) { //res_NORMAL >= res_RG && res_NORMAL >= res_BY && res_NORMAL >= res_MONO
                    if(res_NORMAL <= 0.90 * total) {
                        txtLikely.setText("Likely");
                        resultType.setText("NORMAL");
                    } else {
                        txtLikely.setText("Most Likely");
                        resultType.setText("NORMAL");
                    }
                    // set percentage
                    normalPercent.setText(normal_percent + "%");
                    rgPercent.setText(rg_percent + "%");
                    byPercent.setText(by_percent + "%");
                    monoPercent.setText(mono_percent + "%");
                }else if(res_BY > res_RG) {
                    if(res_BY <= 0.80 * res_BY) {
                        txtLikely.setText("Likely");
                        //resultType.setText("BLUE/YELLOW BLINDNESS");
                        String text = "<font color=#0A1172>BlUE</font>/<font color=#FDD128>YELLOW</font> BlINDNESS";
                        resultType.setText(Html.fromHtml(text));
                    } else {
                        txtLikely.setText("Most Likely");
                        //resultType.setText("BLUE/YELLOW BLINDNESS");
                        String text = "<font color=#0A1172>BlUE</font>/<font color=#FDD128>YELLOW</font> BlINDNESS";
                        resultType.setText(Html.fromHtml(text));
                    }
                    // set percentage
                    normalPercent.setText(normal_percent + "%");
                    rgPercent.setText(rg_percent + "%");
                    byPercent.setText(by_percent + "%");
                    monoPercent.setText(mono_percent + "%");
                } else {
                    if(res_RG <= 0.80 * res_RG) {
                        txtLikely.setText("Likely");
                        //resultType.setText("RED/GREEN BLINDNESS");
                        String text = "<font color=#D0312D>RED</font>/<font color=#028A0F>GREEN</font> BlINDNESS";
                        resultType.setText(Html.fromHtml(text));
                    } else {
                        txtLikely.setText("Most Likely");
                        //resultType.setText("RED/GREEN BLINDNESS");
                        String text = "<font color=#D0312D>RED</font>/<font color=#028A0F>GREEN</font> BlINDNESS";
                        resultType.setText(Html.fromHtml(text));
                    }
                    // set percentage
                    normalPercent.setText(normal_percent + "%");
                    rgPercent.setText(rg_percent + "%");
                    byPercent.setText(by_percent + "%");
                    monoPercent.setText(mono_percent + "%");
                }

                AppCompatButton btn_home = (AppCompatButton) popupView.findViewById(R.id.btn_home);
                btn_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ColorblindTestActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.dismiss();
            }
        }, 1000); // 1000 milliseconds delay

    }

    //back to main page
    public void backToMain() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}