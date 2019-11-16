package com.example.okhttp_lib;

/**
 * Created by Cg on 2017/6/1.
 */
public interface OkHttpListener<T> {
    void onSuccess(T response);

    void onError(String msg);

    void onUploadProgress(long current, long total, boolean done);

    void onDownloadProgress(long current, long total, boolean done);

}
