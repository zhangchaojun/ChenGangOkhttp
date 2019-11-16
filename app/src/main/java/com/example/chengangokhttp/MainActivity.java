package com.example.chengangokhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.okhttp_lib.OkHttpRequest;
import com.example.okhttp_lib.bean.OkHttpRequestEntity;
import com.example.okhttp_lib.listener.OkHttpStringRequestListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "cj";
    private Button bt_get;
    private Button bt_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        bt_get = findViewById(R.id.bt_get);
        bt_post = findViewById(R.id.bt_post);

        bt_get.setOnClickListener(this);
        bt_post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get:

                String url = "https://wanandroid.com/wxarticle/chapters/json";
                OkHttpRequestEntity okHttpRequestEntity = new OkHttpRequestEntity(url, url);
                okHttpRequestEntity.connectTime(20000);
                OkHttpRequest.getInstance().getRequest(okHttpRequestEntity, new OkHttpStringRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "onSuccess: " + response);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: " + msg);
                    }
                });

                break;
            case R.id.bt_post:

                String url2 = "https://www.wanandroid.com/user/login";
                OkHttpRequestEntity okHttpRequestEntity2 = new OkHttpRequestEntity(url2, url2);
                okHttpRequestEntity2.addParam("username", "hellowwww");
                okHttpRequestEntity2.addParam("password", "zhang123");
                OkHttpRequest.getInstance().postRequest(okHttpRequestEntity2, new OkHttpStringRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e(TAG, "onSuccess: " + response);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e(TAG, "onError: " + msg);
                    }
                });
                break;
        }
    }
}
