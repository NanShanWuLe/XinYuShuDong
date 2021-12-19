package com.example.bb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiaryActivity extends AppCompatActivity {

    private List<DayView> dayViewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ImageView LogOut = (ImageView) findViewById(R.id.setting);
        TextView yearTextView = (TextView) findViewById(R.id.yearTextView);
        TextView monthTextView = (TextView) findViewById(R.id.monthTextView);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        ImageView bottle_btn = (ImageView) findViewById(R.id.bottle_btn);
        initDays(getInt(yearTextView.getText().toString()), getInt(monthTextView.getText().toString()));
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        DayAdapter adapter = new DayAdapter(dayViewList);
        adapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(DiaryActivity.this, WriteActivity.class);
                intent.putExtra("YEAR", yearTextView.getText().toString());
                intent.putExtra("MONTH", monthTextView.getText().toString());
                intent.putExtra("DAY", position + 1);
                startActivity(intent);
                Toast.makeText(DiaryActivity.this, monthTextView.getText().toString() + dayViewList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        monthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        yearTextView.setText(year + "");
                        monthTextView.setText(month + 1  + "月");
                        initDays(year, month + 1);
                        adapter.notifyDataSetChanged();
                    }
                },year,month,day).show();
            }
        });
        bottle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this, BottleActivity.class);
                startActivity(intent);
            }
        });
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(DiaryActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_clearAll:
                                MySQLiteHelper dbHelper = new MySQLiteHelper(DiaryActivity.this, "DiaryStore.db", null, 1);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                                String username = preferences.getString("CurrentUsername", "0");
                                db.delete("Diary", "user=?", new String[]{username});
                                Toast.makeText(DiaryActivity.this, "已删除所有数据", Toast.LENGTH_SHORT).show();
                                break;
                            case  R.id.menu_logout:
                                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                editor.putBoolean("LogState", false);
                                editor.apply();
                                Intent intent = new Intent(DiaryActivity.this, MainActivity.class);
                                startActivity(intent);
                                DiaryActivity.this.finish();
                                break;
                            case  R.id.menu_check:
                                SharedPreferences preferences_check = getSharedPreferences("data", MODE_PRIVATE);
                                String username_check = preferences_check.getString("CurrentUsername", "0");
                                MySQLiteHelper sqLiteHelper = new MySQLiteHelper(DiaryActivity.this, "DiaryStore.db", null, 1);
                                SQLiteDatabase db_check = sqLiteHelper.getWritableDatabase();
                                String selection = "user=?";
                                String[] selectionArgs={username_check};
                                Cursor cursor = db_check.query("Diary", null, selection, selectionArgs, null, null, null, null);
                                int i = 0;
                                String[] DiaryData = new String[50];
                                boolean[] Selected = new boolean[50];
                                if (cursor.moveToFirst()){
                                    do {
                                        DiaryData[i] = cursor.getString(cursor.getColumnIndex("date"));
                                        i++;
                                    }while (cursor.moveToNext());
                                }
                                String[] mDiaryData = new String[i];
                                boolean[] mSelected  = new boolean[i];
                                for (int m = 0; m < i; m++){
                                    mDiaryData[m] = DiaryData[m];
                                    mSelected[m] = Selected[m];
                                }
                                cursor.close();
                                db_check.close();
                                sqLiteHelper.close();
                                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivity.this);
                                builder.setTitle("日记列表");
                                builder.setMultiChoiceItems(mDiaryData, mSelected, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        //Selected[which] = isChecked;
                                        mSelected[which] = isChecked;
                                    }
                                });
                                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MySQLiteHelper dbHelper = new MySQLiteHelper(DiaryActivity.this, "DiaryStore.db", null, 1);
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                                        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                                        String username = preferences.getString("CurrentUsername", "0");
                                        //Toast.makeText(DiaryActivity.this, "" + mSelected[0] + mSelected[1] + mSelected[2], Toast.LENGTH_SHORT).show();
                                        for (int m = 0; m < mDiaryData.length; m++){
                                            if (mSelected[m] == true){
                                                db.delete("Diary", "user=? and date=?", new String[]{username, mDiaryData[m]});
                                            }
                                        }
                                        db.close();
                                        dbHelper.close();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void initDays(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDay = calendar.get(Calendar.DATE);
        dayViewList.clear();
        for (int i = 0; i < maxDay; i++){
            DayView dayView = new DayView((i + 1) + "日");
            dayViewList.add(dayView);
        }
    }

    private int getInt(String numString){
        numString = numString.replaceAll("[^\\d]", "");
        return Integer.parseInt(numString);
    }
}