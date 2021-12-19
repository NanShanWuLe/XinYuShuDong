package com.example.bb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    public MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView register_username = (TextView)findViewById(R.id.register_username);
        TextView register_password = (TextView)findViewById(R.id.register_password);
        ImageView register_success_btn = (ImageView) findViewById(R.id.register_success_btn);
        register_success_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register_password.getText().toString().length() <= 6 || register_username.getText().toString().length() <= 6){
                    Toast.makeText(RegisterActivity.this, "用户名和密码不能少于6位", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString(register_username.getText().toString(), register_password.getText().toString());
                    editor.apply();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                }
            }
        });
    }
}