package com.example.okhttp_lib.listener;


import com.example.okhttp_lib.OkHttpListener;

/**
 * Created by Cg on 2017/6/1.
 */
public abstract class OkHttpRequestListener<T> implements OkHttpListener<T> {
    @Override
    public void onUploadProgress(long current, long total, boolean done) {

    }

    @Override
    public void onDownloadProgress(long current, long total, boolean done) {

    }
}
