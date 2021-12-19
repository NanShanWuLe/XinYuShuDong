package com.example.bb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Timer timer = new Timer();
        TimerTask timerTask  = new TimerTask() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                if (!preferences.getBoolean("LogState", false)){
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    StartActivity.this.finish();
                }else {
                    Intent intent = new Intent(StartActivity.this, DiaryActivity.class);
                    startActivity(intent);
                    StartActivity.this.finish();
                }
            }
        };
        timer.schedule(timerTask,1000*2);
    }
}