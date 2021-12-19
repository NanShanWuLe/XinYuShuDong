package com.example.bb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener{


    public static final String ACTION_UPDATE_UI = "action.updateUI";
    UpdateUIBroadcastReceiver broadcastReceiver;

    TextView write_done;
    TextView year_view;
    TextView month_view;
    TextView day_view;
    EditText content;
    ImageView weather_img;
    String currentWeather = "无";
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Intent intent = getIntent();
        String year = intent.getStringExtra("YEAR");
        String month = intent.getStringExtra("MONTH");
        int day = intent.getIntExtra("DAY", 0);
        year_view = (TextView)findViewById(R.id.year_write);
        month_view = (TextView)findViewById(R.id.month_write);
        day_view = (TextView)findViewById(R.id.day_write);
        write_done = (TextView) findViewById(R.id.write_done);
        write_done.setOnClickListener(this);
        content = (EditText)findViewById(R.id.content);
        weather_img = (ImageView)findViewById(R.id.weather_img);
        weather_img.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_UI);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);

        Intent readEditTextIntent = new Intent(this, MyIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("param", "oper1");
        readEditTextIntent.putExtras(bundle);
        startService(readEditTextIntent);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                year_view.setTextColor(getResources().getColor(R.color.black));
                month_view.setTextColor(getResources().getColor(R.color.black));
                day_view.setTextColor(getResources().getColor(R.color.black));
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {
                if (content.getText().toString() == ""){
                    year_view.setTextColor(R.color.write_page);
                    month_view.setTextColor(R.color.write_page);
                    day_view.setTextColor(R.color.write_page);
                }
            }
        });
        year_view.setText(year);
        month_view.setText(month);
        day_view.setText(String.valueOf(day));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.write_done:
                if (!content.getText().toString().equals("")){
                    Intent saveEditTextIntent = new Intent(this, MyIntentService.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("param", "oper2");
                    saveEditTextIntent.putExtras(bundle);
                    startService(saveEditTextIntent);
                }else{
                    MySQLiteHelper dbHelper = new MySQLiteHelper(this, "DiaryStore.db", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                    String username = preferences.getString("CurrentUsername", "0");
                    db.delete("Diary", "user=? and date=?", new String[]{username, year_view.getText().toString() + "-" + month_view.getText().toString() + "-" + day_view.getText().toString() + "日"});
                }
                Intent intent = new Intent(WriteActivity.this, DiaryActivity.class);
                startActivity(intent);
                WriteActivity.this.finish();
                break;
            case R.id.weather_img:
                String[] weather_text = {"晴天", "雨天", "雪天"};
                int[] weather_icon = {R.drawable.sun, R.drawable.rain, R.drawable.snow};
                List<Map<String, Object>>listItem;
                listItem = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < weather_text.length; i++){
                    Map<String, Object>map = new HashMap<>();
                    map.put("text", weather_text[i]);
                    map.put("icon", weather_icon[i]);
                    listItem.add(map);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItem, R.layout.weather_items, new String[]{"text", "icon"}, new int[]{R.id.text_weather_item, R.id.ui_weather_item});
                AlertDialog.Builder builder = new AlertDialog.Builder(this).setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        weather_img.setImageResource(weather_icon[which]);
                        currentWeather = weather_text[which];
                    }
                });
                builder.create().show();
                break;
            default:
                break;
        }
    }

    private class UpdateUIBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String operation = intent.getStringExtra("operations");
            MySQLiteHelper sqLiteHelper = new MySQLiteHelper(context, "DiaryStore.db", null, 1);
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
            String username = preferences.getString("CurrentUsername", "0");
            String date = year_view.getText().toString() + "-" + month_view.getText().toString() + "-" + day_view.getText().toString() + "日";
            switch (operation){
                case "1":
                    String[] columns = {"content", "icon"};
                    String selection = "user=? and date=?";
                    String[] selectionArgs={username, date};
                    Cursor cursor = db.query("Diary", columns, selection, selectionArgs, null, null, null, null);
                    if (cursor.moveToFirst()){
                        do {
                            String dbContent = cursor.getString(cursor.getColumnIndex("content"));
                            String dbIcon = cursor.getString(cursor.getColumnIndex("icon"));
                            content.setText(dbContent);
                            switch (dbIcon){
                                case "晴天":
                                    weather_img.setImageResource(R.drawable.sun);
                                    break;
                                case  "雨天":
                                    weather_img.setImageResource(R.drawable.rain);
                                    break;
                                case  "雪天":
                                    weather_img.setImageResource(R.drawable.snow);
                                    break;
                                default:
                                    break;
                            }
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                    db.close();
                    sqLiteHelper.close();
                    break;
                case "2":
                    String[] columns_input = {"content"};
                    String selection_input = "user=? and date=?";
                    String[] selectionArgs_input = {username, date};
                    Cursor cursor1 = db.query("Diary", columns_input, selection_input, selectionArgs_input, null, null, null, null);
                    ContentValues values = new ContentValues();
                    if (cursor1.moveToFirst()){
                        do {
                            values.put("content", content.getText().toString());
                            values.put("icon", currentWeather);
                            db.update("Diary", values, "user=? and date=?", new String[]{username, date});
                        }while (cursor1.moveToNext());
                    }else {
                        values.put("user", username);
                        values.put("date", date);
                        values.put("icon", currentWeather);
                        values.put("content", content.getText().toString());
                        db.insert("Diary", null, values);
                    }
                    values.clear();
                    cursor1.close();
                    db.close();
                    break;
                default:
                    break;
            }
        }
    }
}