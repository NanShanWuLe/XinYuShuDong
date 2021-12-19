package com.example.bb;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        EditText logon_username_edit = (EditText) findViewById(R.id.logon_username_edit);
        EditText logon_password_edit = (EditText)findViewById(R.id.logon_password_edit);
        TextView register_btn = (TextView) findViewById(R.id.register_btn);
        TextView YiBan_login_btn = (TextView)findViewById(R.id.YiBan_login);
        ImageButton logon_btn = (ImageButton)findViewById(R.id.logon_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        logon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                if(preferences.getString(logon_username_edit.getText().toString(), "null").equals(logon_password_edit.getText().toString())){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("LogState", true);
                    editor.putString("CurrentUsername", logon_username_edit.getText().toString());
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }else{
                    Toast.makeText(MainActivity.this, "输入的用户名或密码有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        YiBan_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YibanActivity.class);
                startActivity(intent);
            }
        });
    }
}