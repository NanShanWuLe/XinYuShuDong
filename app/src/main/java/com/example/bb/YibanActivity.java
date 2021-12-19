package com.example.bb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bb.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.HttpURLConnection;
import java.util.Map;

import cn.yiban.open.Authorize;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YibanActivity extends AppCompatActivity implements View.OnClickListener{

    Button yiban_login_btn;
    TextView yiban_response;
    public static final int SHOW_RESPONSE = 0;
    String App_ID = "e18a69cfa85366da";
    String App_Secret = "6b0b655f37f2b498907e04fdd97bbf656b0b655f37f2b498907e04fdd97bbf65";
    String CALLBACK_URL = "http://2673y53d07.wicp.vip/start";
    String BACK_URL = "http://2673y53d07.wicp.vip/start/autoApp.html";

    OkHttpClient okHttpClient;
    private static final MediaType CONTENT_TYPE = MediaType.parse("multipart/form-data;charset=utf-8");
    @Override
    protected void onResume() {
        super.onResume();
        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        String code = "";
        String text = new String();
        InputStream inputStream = null;
        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = i_getvalue.getData();
            if(uri != null){
                code = uri.getQueryParameter("code");
                Authorize authorize = new Authorize(App_ID, App_Secret);
                Log.d("TAG666", "onResume: code " + code);
                PostToken(App_ID, App_Secret, code, BACK_URL);
                //text = authorize.querytoken(code, CALLBACK_URL);

            }else {
                Toast.makeText(YibanActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yiban);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        yiban_login_btn = (Button)findViewById(R.id.yiban_login_btn);
        yiban_response = (TextView)findViewById(R.id.response);
        okHttpClient = new OkHttpClient().newBuilder().build();
        yiban_login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yiban_login_btn:
                Authorize authorize = new Authorize(App_ID, App_Secret);
                String url = authorize.forwardurl(BACK_URL, "QUERY", Authorize.DISPLAY_TAG_T.MOBILE);
                Uri firstURL = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, firstURL);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void PostToken(String App_ID_P,String App_Secret_P, String code_P, String backUrl_P){
        JSONObject js = new JSONObject();
        try {
            js.put("client_id", App_ID_P);
            js.put("client_secret", App_Secret_P);
            js.put("code", code_P);
            js.put("redirect_uri", backUrl_P);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        FormBody formBody = new FormBody.Builder()
//                .add("client_id", App_ID_P)
//                .add("client_secret", App_Secret_P)
//                .add("code", code_P)
//                .add("redirect_uri", backUrl_P)
//                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client_id", App_ID_P)
                .addFormDataPart("client_secret", App_Secret_P)
                .addFormDataPart("code", code_P)
                .addFormDataPart("redirect_uri", backUrl_P)
                .build();
        Request request = new Request.Builder()
                .url("https://openapi.yiban.cn/oauth/access_token")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result =  response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        yiban_response.setText(result);
                    }
                });
            }
        });
    }
}