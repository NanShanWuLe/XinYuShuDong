package com.example.bb;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.StackView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottleActivity extends AppCompatActivity implements View.OnClickListener {

    private StackView mStackView = null;
    private Button mNextBtn = null;
    private Button mDeleteBtn = null;
    private int[] mImageIds = {
            R.drawable.snow, R.drawable.rain, R.drawable.sun, R.drawable.icon
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottle);
        mStackView = (StackView)findViewById(R.id.stack_view);
        mNextBtn = (Button)findViewById(R.id.next_btn);
        mDeleteBtn = (Button)findViewById(R.id.delete_btn);
        mNextBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mStackView = (StackView) findViewById(R.id.stack_view);
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("iamge", mImageIds[i]);
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                listItems,
                R.layout.bottle_item,
                new String[]{"iamge"},
                new int[]{});
        mStackView.setAdapter(simpleAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_btn:
                mStackView.showNext();
                break;
            case R.id.delete_btn:
                mStackView.showPrevious();
                break;
            default:
                break;
        }
    }
}