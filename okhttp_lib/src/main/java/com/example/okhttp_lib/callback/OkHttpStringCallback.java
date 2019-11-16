package com.example.okhttp_lib.callback;

import android.os.Handler;


import com.example.okhttp_lib.OkHttpCallback;
import com.example.okhttp_lib.OkHttpListener;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Cg on 2017/6/1.
 */

public class OkHttpStringCallback extends OkHttpCallback<String> {


    public OkHttpStringCallback(OkHttpListener<String> listener, Handler handler) {
        super(listener, handler);
    }

    @Override
    protected void processResponseBody(ResponseBody response) {
        try {
            onSuccess(response.string());
        } catch (IOException e) {
            onError(e.toString());
        }
    }
}
