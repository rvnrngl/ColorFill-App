package com.midnightraven.colorfill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TapColorTestActivity extends AppCompatActivity {

    AppCompatButton back;
    TextView progress, txtlvl, txtScore, txtTimer;
    RelativeLayout tView;
    LinearLayout Container_2x2,Container_3x3,Container_4x4;

    private static  final long COUNTDOWN_IN_MILLIS = 10000;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private AppCompatButton[] buttons_1 = new AppCompatButton[4];
    private AppCompatButton[] buttons_2 = new AppCompatButton[9];
    private AppCompatButton[] buttons_3 = new AppCompatButton[16];
    private int randomButtonIndex, index, colorIndex;

    // easy level 1 to 5
    private String[] easyColor = {"#32E17C","#53EA94","#F4FD3B","#EF573A","#16F5EA","#1E45F2","#DA61EC","#E9438C"};
    private String[] easyColor_d = {"#37F889","#4CCF84","#E2EA39","#ED745D","#15DDD3","#3F60F4","#D343E9","#E42D7D"};
    // normal level 6 to 10
    private String[] normalColor = {"#4590F0","#9146FA","#C97DF9","#E594F6","#FE0BFD","#E3F21A","#EC2E9D","#DB792C"};
    private String[] normalColor_d = {"#3083EE","#8438EF","#B968ED","#D28BE0","#ED0CEC","#D7E51A","#FC33A8","#EC8330"};
    // hard level 11 to 15
    private String[] hardColor = {"#F8BC23","#F88836","#F2498A","#FB1010","#C7EE1E","#28B3F3","#21C46F","#AD3E9E"};
    private String[] hardColor_d = {"#EAB52E","#D88142","#FE5C9A","#D52626","#D8FF2F","#1CA3E0","#30D981","#C956B9"};

    private List<String> easyColorList, easyColorList_d, normalColorList, normalColorList_d, hardColorList, hardColorList_d;

    private int ctr,correct,incorrect;
    private int secondsLeft = 0;
    private int end = 15;

    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_color_test);

        easyColorList = new ArrayList<String>(Arrays.asList(easyColor));
        easyColorList_d = new ArrayList<String>(Arrays.asList(easyColor_d));
        normalColorList = new ArrayList<String>(Arrays.asList(normalColor));
        normalColorList_d = new ArrayList<String>(Arrays.asList(normalColor_d));
        hardColorList = new ArrayList<String>(Arrays.asList(hardColor));
        hardColorList_d = new ArrayList<String>(Arrays.asList(hardColor_d));

        tView = findViewById(R.id.tView);
        progress = findViewById(R.id.tapColorProgress);
        txtlvl = findViewById(R.id.txtlvl);
        txtTimer = findViewById(R.id.txtTimer);
        txtScore = findViewById(R.id.txtScore);

        // 2x2 container
        Container_2x2 = findViewById(R.id.Container_2x2);
        buttons_1[0] = findViewById(R.id.l1_shape1);
        buttons_1[1] = findViewById(R.id.l1_shape2);
        buttons_1[2] = findViewById(R.id.l1_shape3);
        buttons_1[3] = findViewById(R.id.l1_shape4);

        // 3x3 container
        Container_3x3 = findViewById(R.id.Container_3x3);
        buttons_2[0] = findViewById(R.id.l2_shape1);
        buttons_2[1] = findViewById(R.id.l2_shape2);
        buttons_2[2] = findViewById(R.id.l2_shape3);
        buttons_2[3] = findViewById(R.id.l2_shape4);
        buttons_2[4] = findViewById(R.id.l2_shape5);
        buttons_2[5] = findViewById(R.id.l2_shape6);
        buttons_2[6] = findViewById(R.id.l2_shape7);
        buttons_2[7] = findViewById(R.id.l2_shape8);
        buttons_2[8] = findViewById(R.id.l2_shape9);

        // 4x4 container
        Container_4x4 = findViewById(R.id.Container_4x4);
        buttons_3[0] = findViewById(R.id.l3_shape1);
        buttons_3[1] = findViewById(R.id.l3_shape2);
        buttons_3[2] = findViewById(R.id.l3_shape3);
        buttons_3[3] = findViewById(R.id.l3_shape4);
        buttons_3[4] = findViewById(R.id.l3_shape5);
        buttons_3[5] = findViewById(R.id.l3_shape6);
        buttons_3[6] = findViewById(R.id.l3_shape7);
        buttons_3[7] = findViewById(R.id.l3_shape8);
        buttons_3[8] = findViewById(R.id.l3_shape9);
        buttons_3[9] = findViewById(R.id.l3_shape10);
        buttons_3[10] = findViewById(R.id.l3_shape11);
        buttons_3[11] = findViewById(R.id.l3_shape12);
        buttons_3[12] = findViewById(R.id.l3_shape13);
        buttons_3[13] = findViewById(R.id.l3_shape14);
        buttons_3[14] = findViewById(R.id.l3_shape15);
        buttons_3[15] = findViewById(R.id.l3_shape16);

        // back to home
        back = findViewById(R.id.tapColorBackBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                Intent intent = new Intent(TapColorTestActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        initializeTestActivity();

    }

    private void initializeTestActivity() {
        if (ctr <= 4) {
            Container_4x4.setVisibility(View.GONE);
            Container_3x3.setVisibility(View.GONE);
            Container_2x2.setVisibility(View.VISIBLE);
            ctr = ctr + 1;
            Random rand = new Random();
            randomButtonIndex = rand.nextInt(buttons_1.length);

            Random random = new Random();
            index = random.nextInt(easyColorList.size());
            while (index == colorIndex) {
                index = random.nextInt(easyColorList.size());
            }
            colorIndex = index;

            int color = Color.parseColor(easyColorList.get(colorIndex));
            int ans_color = Color.parseColor(easyColorList_d.get(colorIndex));

            for (int i = 0; i < buttons_1.length; i++) {
                GradientDrawable bgShape = (GradientDrawable)buttons_1[i].getBackground();
                bgShape.setColor(color);
                if (i == randomButtonIndex) {
                    bgShape.setColor(ans_color);
                    //ans_color = Color.parseColor("#FF5722"); // set a different color for the random button
                }
                buttons_1[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == buttons_1[randomButtonIndex]) {
                            // handle correct button click
                            countDownTimer.cancel();
                            correct = correct + 1;
                            Log.d("Color", "Correct: " + correct);
                            initializeTestActivity();
                        } else {
                            // handle incorrect button click
                            countDownTimer.cancel();
                            incorrect = incorrect + 1;
                            Log.d("Color", "Incorrect: " + incorrect);
                            initializeTestActivity();
                        }
                    }
                });
            }

            GradientDrawable bgShape_dif = (GradientDrawable)buttons_1[randomButtonIndex].getBackground();
            bgShape_dif.setColor(ans_color);

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

            easyColorList.remove(colorIndex);
            easyColorList_d.remove(colorIndex);

            // set progress
            txtScore.setText("Score: " + correct);
            txtlvl.setText("LEVEL " + ctr);
            progress.setText(ctr + " of " + end);


        } else if (ctr <= 9) {
            Container_2x2.setVisibility(View.GONE);
            Container_4x4.setVisibility(View.GONE);
            Container_3x3.setVisibility(View.VISIBLE);
            ctr = ctr + 1;
            Random rand = new Random();
            randomButtonIndex = rand.nextInt(buttons_2.length);

            Random random = new Random();
            index = random.nextInt(normalColorList.size());
            while (index == colorIndex) {
                index = random.nextInt(normalColorList_d.size());
            }
            colorIndex = index;

            int color = Color.parseColor(normalColorList.get(colorIndex));
            int ans_color = Color.parseColor(normalColorList_d.get(colorIndex));

            for (int i = 0; i < buttons_2.length; i++) {
                GradientDrawable bgShape = (GradientDrawable)buttons_2[i].getBackground();
                bgShape.setColor(color);
                if (i == randomButtonIndex) {
                    bgShape.setColor(ans_color);
                    //ans_color = Color.parseColor("#FF5722"); // set a different color for the random button
                }
                buttons_2[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == buttons_2[randomButtonIndex]) {
                            // handle correct button click
                            countDownTimer.cancel();
                            correct = correct + 1;
                            Log.d("Color", "Correct: " + correct);
                            initializeTestActivity();
                        } else {
                            // handle incorrect button click
                            countDownTimer.cancel();
                            incorrect = incorrect + 1;
                            Log.d("Color", "Incorrect: " + incorrect);
                            initializeTestActivity();
                        }
                    }
                });
            }

            GradientDrawable bgShape_dif = (GradientDrawable)buttons_2[randomButtonIndex].getBackground();
            bgShape_dif.setColor(ans_color);

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

            normalColorList.remove(colorIndex);
            normalColorList_d.remove(colorIndex);

            // set progress
            txtScore.setText("Score: " + correct);
            txtlvl.setText("LEVEL " + ctr);
            progress.setText(ctr + " of " + end);

        } else if (ctr <= 14) {
            Container_2x2.setVisibility(View.GONE);
            Container_3x3.setVisibility(View.GONE);
            Container_4x4.setVisibility(View.VISIBLE);
            ctr = ctr + 1;
            Random rand = new Random();
            randomButtonIndex = rand.nextInt(buttons_3.length);

            Random random = new Random();
            index = random.nextInt(hardColorList.size());
            while (index == colorIndex) {
                index = random.nextInt(hardColorList.size());
            }
            colorIndex = index;

            int color = Color.parseColor(hardColorList.get(colorIndex));
            int ans_color = Color.parseColor(hardColorList_d.get(colorIndex));

            for (int i = 0; i < buttons_3.length; i++) {
                GradientDrawable bgShape = (GradientDrawable)buttons_3[i].getBackground();
                bgShape.setColor(color);
                if (i == randomButtonIndex) {
                    bgShape.setColor(ans_color);
                    //ans_color = Color.parseColor("#FF5722"); // set a different color for the random button
                }
                buttons_3[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == buttons_3[randomButtonIndex]) {
                            // handle correct button click
                            countDownTimer.cancel();
                            correct = correct + 1;
                            Log.d("Color", "Correct: " + correct);
                            initializeTestActivity();
                        } else {
                            // handle incorrect button click
                            countDownTimer.cancel();
                            incorrect = incorrect + 1;
                            Log.d("Color", "Incorrect: " + incorrect);
                            initializeTestActivity();
                        }
                    }
                });
            }

            GradientDrawable bgShape_dif = (GradientDrawable)buttons_3[randomButtonIndex].getBackground();
            bgShape_dif.setColor(ans_color);

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

            hardColorList.remove(colorIndex);
            hardColorList_d.remove(colorIndex);

            // set progress
            txtScore.setText("Score: " + correct);
            txtlvl.setText("LEVEL " + ctr);
            progress.setText(ctr + " of " + end);

        }
        else {
            Log.d("ColorTap", "Test end");
            resultPage();
        }

    }

    private class resultPgaeTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    private void resultPage() {
        Dialog dialog;
        dialog = new Dialog(TapColorTestActivity.this);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.result_tap_color, null);

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
                popupWindow.showAtLocation(tView, Gravity.CENTER, 0, 0);

                //initialize
                TextView tap_score = (TextView) popupView.findViewById(R.id.tap_score);
                TextView tap_result = (TextView) popupView.findViewById(R.id.tap_result);

                int total = 15;
                int percent = 100 * correct / total;
                Log.d("ColorTap", "Percent: "+ percent); // separete the text impressive
                if (correct == total) {
                    String textScore = "<font color=#00FF00>"+correct+"</font> out of <font color=#00FF00>"+total+"</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "impressive eye sight! you have achieve a perfect score of <font color=#00FF00>"+percent+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                } else if (correct >= 0.90 * total && correct <= 0.99 * total) {
                    String textScore = "<font color=#00FF00>"+correct+"</font> out of <font color=#00FF00>"+total+"</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "very good eye sight! you scored higher than <font color=#00FF00>"+percent+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                } else if (correct >= 0.75 * total && correct <= 0.90 * total) {
                    String textScore = "<font color=#00FF00>"+correct+"</font> out of <font color=#00FF00>"+total+"</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "good eye sight! you scored higher than <font color=#00FF00>"+percent+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                } else if (correct >= 0.60 * total && correct <= 0.75 * total) {
                    String textScore = "<font color=#0000FF>"+correct+"</font> out of <font color=#00FF00>"+total+"</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "average eye sight! you scored higher than <font color=#0000FF>"+percent+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                } else if (correct >= 0.45 * total && correct <= 0.60 * total) {
                    String textScore = "<font color=#FF0000>" + correct + "</font> out of <font color=#00FF00>" + total + "</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "bad eye sight! you scored lower than <font color=#FF0000>"+(100-percent)+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                } else if (correct <= 0.45 * total) {
                    String textScore = "<font color=#FF0000>" + correct + "</font> out of <font color=#00FF00>" + total + "</font>";
                    tap_score.setText(Html.fromHtml(textScore));
                    String textRes = "very bad eye sight! you scored lower than <font color=#FF0000>"+(100-percent)+"%</font> of most people in eye color accuracy test";
                    tap_result.setText(Html.fromHtml(textRes));
                }

                AppCompatButton btn_play_again = (AppCompatButton) popupView.findViewById(R.id.tap_play_again);
                btn_play_again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TapColorTestActivity.this, TapColorTestActivity.class);
                        startActivity(intent);
                    }
                });

                AppCompatButton btn_home = (AppCompatButton) popupView.findViewById(R.id.tap_home);
                btn_home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TapColorTestActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

                dialog.dismiss();

            }
        }, 1000);
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                    txtTimer.setText(""+secondsLeft);
                }
                //txtTimer.setText(""+ millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.d("Timer", "skipped ");
                if(ctr <= 4) {
                    easyColorList.remove(colorIndex);
                    easyColorList_d.remove(colorIndex);
                } else if (ctr <= 9) {
                    normalColorList.remove(colorIndex);
                    normalColorList_d.remove(colorIndex);
                } else if (ctr <= 14) {
                    hardColorList.remove(colorIndex);
                    hardColorList_d.remove(colorIndex);
                }

                incorrect = incorrect + 1;
                Log.d("Color", "Incorrect: " + incorrect);
                initializeTestActivity();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}