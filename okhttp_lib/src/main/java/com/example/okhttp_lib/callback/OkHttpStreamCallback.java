package com.example.okhttp_lib.callback;

import android.os.Handler;


import com.example.okhttp_lib.OkHttpCallback;
import com.example.okhttp_lib.OkHttpListener;

import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by Cg on 2017/6/1.
 */

public class OkHttpStreamCallback extends OkHttpCallback<InputStream> {

    public OkHttpStreamCallback(OkHttpListener<InputStream> listener, Handler handler) {
        super(listener, handler);
    }

    @Override
    protected void processResponseBody(ResponseBody response) {
        onSuccess(response.byteStream());
    }
}
