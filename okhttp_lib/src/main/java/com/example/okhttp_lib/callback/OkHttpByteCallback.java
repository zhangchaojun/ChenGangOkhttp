package com.example.okhttp_lib.callback;

import android.os.Handler;


import com.example.okhttp_lib.OkHttpCallback;
import com.example.okhttp_lib.OkHttpListener;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Cg on 2017/6/1.
 */

public class OkHttpByteCallback extends OkHttpCallback<byte[]> {

    public OkHttpByteCallback(OkHttpListener<byte[]> listener, Handler handler) {
        super(listener, handler);
    }

    @Override
    protected void processResponseBody(ResponseBody response) {
        try {
            onSuccess(response.bytes());
        } catch (IOException e) {
            onError(e.getMessage());
        }
    }
}
