package com.example.okhttp_lib.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;


import com.example.okhttp_lib.OkHttpCallback;
import com.example.okhttp_lib.OkHttpListener;

import okhttp3.ResponseBody;

/**
 * Created by Cg on 2017/6/1.
 */

public class OkHttpImageCallback extends OkHttpCallback<Bitmap> {

    public OkHttpImageCallback(OkHttpListener<Bitmap> listener, Handler handler) {
        super(listener, handler);
    }

    @Override
    protected void processResponseBody(ResponseBody response) {
        onSuccess(BitmapFactory.decodeStream(response.byteStream()));
    }
}
