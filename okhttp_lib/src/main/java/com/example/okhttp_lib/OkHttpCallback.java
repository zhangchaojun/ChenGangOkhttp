package com.example.okhttp_lib;

import android.os.Handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Cg on 2017/6/1.
 */

public abstract class OkHttpCallback<T> implements Callback {

    protected OkHttpListener<T> mOkHttpListener;

    private Handler mHandle;

    public OkHttpCallback(OkHttpListener<T> listener, Handler handler) {
        this.mOkHttpListener = listener;
        this.mHandle = handler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onError(e.getMessage());
    }

    @Override
    public void onResponse(Call call, Response response) {
        OkHttpRequest.getInstance().removeLoadListener(String.valueOf(call.request().tag()));
        if (call.isCanceled()) {
            return;
        }
        if (!response.isSuccessful()) {
            onError(response.message());
        } else {
            processResponseBody(response.body());
        }
        response.body().close();
    }

    protected void onError(final String error) {
        if (mHandle != null) {
            mHandle.post(new Runnable() {
                @Override
                public void run() {
                    if (mOkHttpListener != null) {
                        mOkHttpListener.onError(error);
                    }
                }
            });
        } else {
            if (mOkHttpListener != null) {
                mOkHttpListener.onError(error);
            }
        }
    }

    protected void onSuccess(final T response) {
        if (mHandle != null) {
            mHandle.post(new Runnable() {
                @Override
                public void run() {
                    if (mOkHttpListener != null) {
                        mOkHttpListener.onSuccess(response);
                    }
                }
            });
        } else {
            if (mOkHttpListener != null) {
                mOkHttpListener.onSuccess(response);
            }
        }
    }

    protected abstract void processResponseBody(ResponseBody response);
}
