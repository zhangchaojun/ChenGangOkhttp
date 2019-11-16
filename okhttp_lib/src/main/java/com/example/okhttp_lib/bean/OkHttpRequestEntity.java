package com.example.okhttp_lib.bean;

import java.util.LinkedHashMap;

import okhttp3.Authenticator;

/**
 * Created by Cg on 2017/5/22.
 */

public class OkHttpRequestEntity {
    public final String tag;
    public final LinkedHashMap<String, String> header;
    public final LinkedHashMap<String, String> param;
    public String url;
    public long connectTime = 0;
    public long readTime = 0;
    public long writeTime = 0;
    public boolean needUploadProgress = false;
    public boolean needDownloadProgress = false;
    public boolean isBackOnUiThread = true;
    public Authenticator authenticator;

    public OkHttpRequestEntity(String tag) {
        this.tag = tag;
        this.header = new LinkedHashMap<>();
        this.param = new LinkedHashMap<>();
    }

    public OkHttpRequestEntity(String tag, String url) {
        this.tag = tag;
        this.url = url;
        this.header = new LinkedHashMap<>();
        this.param = new LinkedHashMap<>();
    }

    public OkHttpRequestEntity url(String url) {
        this.url = url;
        return this;
    }

    public OkHttpRequestEntity addHead(String key, String value) {
        if (key == null || value == null) {
            return this;
        }
        header.put(key, value);
        return this;
    }

    public OkHttpRequestEntity addParam(String key, String value) {
        if (key == null || value == null) {
            return this;
        }
        param.put(key, value);
        return this;
    }

    public OkHttpRequestEntity needUploadProgress(boolean needUploadProgress) {
        this.needUploadProgress = needUploadProgress;
        return this;
    }

    public OkHttpRequestEntity needDownloadProgress(boolean needDownloadProgress) {
        this.needDownloadProgress = needDownloadProgress;
        return this;
    }

    public OkHttpRequestEntity isBackOnUiThread(boolean backOnUiThread) {
        isBackOnUiThread = backOnUiThread;
        return this;
    }

    public OkHttpRequestEntity connectTime(long connectTime) {
        this.connectTime = connectTime;
        return this;
    }

    public OkHttpRequestEntity readTime(long readTime) {
        this.readTime = readTime;
        return this;
    }

    public OkHttpRequestEntity writeTime(long writeTime) {
        this.writeTime = writeTime;
        return this;
    }

    public OkHttpRequestEntity authenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }
}
