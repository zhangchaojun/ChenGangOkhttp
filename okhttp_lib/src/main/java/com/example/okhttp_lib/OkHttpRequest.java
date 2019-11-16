package com.example.okhttp_lib;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;


import com.example.okhttp_lib.bean.OkHttpRequestEntity;
import com.example.okhttp_lib.bean.OkHttpSyncResponse;
import com.example.okhttp_lib.bean.ResponseMethod;
import com.example.okhttp_lib.bean.SyncResponseType;
import com.example.okhttp_lib.callback.OkHttpByteCallback;
import com.example.okhttp_lib.callback.OkHttpImageCallback;
import com.example.okhttp_lib.callback.OkHttpStreamCallback;
import com.example.okhttp_lib.callback.OkHttpStringCallback;
import com.example.okhttp_lib.listener.OkHttpByteRequestListener;
import com.example.okhttp_lib.listener.OkHttpImageRequestListener;
import com.example.okhttp_lib.listener.OkHttpRequestListener;
import com.example.okhttp_lib.listener.OkHttpStreamRequestListener;
import com.example.okhttp_lib.listener.OkHttpStringRequestListener;
import com.example.okhttp_lib.request.OkHttpRequestBody;
import com.example.okhttp_lib.response.OkHttpResponseBody;
import com.example.okhttp_lib.utils.OKHttpSafeUtils;
import com.example.okhttp_lib.utils.OkHttpClientUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Cg on 2017/5/22.
 */

public class OkHttpRequest {

    private static final String ContentType = "Content-Type";
    private static final String ContentTypePlain = "text/plain;charset=utf-8";
    private static final String ContentTypeJson = "application/json;charset=utf-8";
    private static final String ContentTypeStream = "application/octet-stream";
    private static OkHttpRequest instance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Map<String, OkHttpRequestListener> mUploadListenerMap = new HashMap<>();
    private Map<String, OkHttpRequestListener> mDownloadListenerMap = new HashMap<>();

    public OkHttpRequest() {
        mOkHttpClient = new OkHttpClient().newBuilder()
                .sslSocketFactory(OKHttpSafeUtils.getUnSafeSocketFactory(), OKHttpSafeUtils.UnSafeTrustManager)
                .hostnameVerifier(OKHttpSafeUtils.UnSafeHostnameVerifier)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Response originalResponse = chain.proceed(originalRequest);
                        String tag = String.valueOf(originalRequest.tag());
                        if (mDownloadListenerMap.containsKey(tag) && !(originalResponse.body() instanceof OkHttpResponseBody)) {
                            return originalResponse.newBuilder()
                                    .body(new OkHttpResponseBody(originalResponse.body(), mDownloadListenerMap.get(tag)))
                                    .build();
                        } else {
                            return originalResponse;
                        }
                    }
                }).build();
        mOkHttpClient.dispatcher().setMaxRequests(32);
    }

    public static OkHttpRequest getInstance() {
        if (instance == null) {
            synchronized (OkHttpRequest.class) {
                if (instance == null) {
                    instance = new OkHttpRequest();
                }
            }
        }
        return instance;
    }

    public void requestAsync(Request request, Callback responseCallback) {
        OkHttpClientUtils.requestAsync(mOkHttpClient, request, responseCallback);
    }

    public Response requestSync(Request request) throws IOException {
        return OkHttpClientUtils.requestSync(mOkHttpClient, request);
    }

    private void request(OkHttpRequestEntity entity, ResponseMethod method, OkHttpRequestListener listener) {
        OkHttpCallback callback = createOkHttpCallback(entity, listener);
        if (callback != null) {
            handlerRequestListener(entity, listener);
        }
        try {
            OkHttpClientUtils.requestAsync(mOkHttpClient, entity, createRequest(entity, method), callback);
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    public void request(OkHttpRequestEntity entity, ResponseMethod method, OkHttpCallback callback) {
        try {
            OkHttpClientUtils.requestAsync(mOkHttpClient, entity, createRequest(entity, method), callback);
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e.getMessage());
            }
        }
    }

    private OkHttpSyncResponse request(OkHttpRequestEntity entity, ResponseMethod method, SyncResponseType type) {
        try {
            return OkHttpClientUtils.requestSync(mOkHttpClient, entity, createRequest(entity, method), type);
        } catch (Exception e) {
            OkHttpSyncResponse response = new OkHttpSyncResponse();
            response.setSuccessful(false);
            response.setError(e.getMessage());
            return response;
        }
    }

    public void getRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.GET, listener);
    }

    public OkHttpSyncResponse getRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.GET, type);
    }

    public void headRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.HEAD, listener);
    }

    public OkHttpSyncResponse headRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.HEAD, type);
    }

    public void postRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.POST, listener);
    }

    public OkHttpSyncResponse postRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.POST, type);
    }

    public void putRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.PUT, listener);
    }

    public OkHttpSyncResponse putRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.PUT, type);
    }

    public void patchRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.PATCH, listener);
    }

    public OkHttpSyncResponse patchRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.PATCH, type);
    }

    public void deleteRequest(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        request(entity, ResponseMethod.DELETE, listener);
    }

    public OkHttpSyncResponse deleteRequest(OkHttpRequestEntity entity, SyncResponseType type) {
        return request(entity, ResponseMethod.DELETE, type);
    }

    private void handlerRequestListener(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        if (entity.needUploadProgress) {
            mUploadListenerMap.put(entity.tag, listener);
        }
        if (entity.needDownloadProgress) {
            mDownloadListenerMap.put(entity.tag, listener);
        }
    }

    private OkHttpCallback createOkHttpCallback(OkHttpRequestEntity entity, OkHttpRequestListener listener) {
        Handler handler = entity.isBackOnUiThread ? mHandler : null;
        if (listener instanceof OkHttpStringRequestListener) {
            return new OkHttpStringCallback(listener, handler);
        }
        if (listener instanceof OkHttpImageRequestListener) {
            return new OkHttpImageCallback(listener, handler);
        }
        if (listener instanceof OkHttpStreamRequestListener) {
            return new OkHttpStreamCallback(listener, handler);
        }
        if (listener instanceof OkHttpByteRequestListener) {
            return new OkHttpByteCallback(listener, handler);
        }
        return null;
    }

    public Request createRequest(OkHttpRequestEntity entity, ResponseMethod method) {
        Request.Builder requestBuilder = null;
        if (method == ResponseMethod.GET) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(createUrl(entity.url, entity.param)).get();
        }
        if (method == ResponseMethod.HEAD) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(createUrl(entity.url, entity.param)).head();
        }
        if (method == ResponseMethod.POST) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(entity.url).post(createRequestBody(entity));
        }
        if (method == ResponseMethod.PUT) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(entity.url).put(createRequestBody(entity));
        }
        if (method == ResponseMethod.DELETE) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(entity.url).delete(createRequestBody(entity));
        }
        if (method == ResponseMethod.PATCH) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(entity.url).patch(createRequestBody(entity));
        }
        if (requestBuilder == null) {
            requestBuilder = new Request.Builder().tag(entity.tag).url(entity.url).post(createRequestBody(entity));
        }
        Iterator<Map.Entry<String, String>> headIterator = entity.header.entrySet().iterator();
        while (headIterator.hasNext()) {
            Map.Entry<String, String> entry = headIterator.next();
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        return requestBuilder.build();
    }

    private String createUrl(String url, LinkedHashMap<String, String> params) {
        Uri.Builder urlBuilder = Uri.parse(url).buildUpon();
        Iterator<Map.Entry<String, String>> paramIterator = params.entrySet().iterator();
        while (paramIterator.hasNext()) {
            Map.Entry<String, String> entry = paramIterator.next();
            urlBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return urlBuilder.build().toString();
    }

    private RequestBody createRequestBody(OkHttpRequestEntity entity) {
        RequestBody requestBody = null;
        if (entity.header.containsKey(ContentType)) {
            String contentType = entity.header.get(ContentType);
            if ("text/plain".equals(contentType)) {
                if (entity.param.containsKey("string")) {
                    requestBody = RequestBody.create(MediaType.parse(ContentTypePlain), entity.param.get("string"));
                }
            } else if ("application/json".equals(contentType) || "application/json;charset=UTF-8".equals(contentType)) {
                if (entity.param.containsKey("json")) {
                    requestBody = RequestBody.create(MediaType.parse(ContentTypeJson), entity.param.get("json"));
                }
            } else if ("multipart/form-data".equals(contentType) || "image/jpeg".equals(contentType)) {
                if (entity.param.containsKey("file")) {
                    requestBody = RequestBody.create(MediaType.parse(ContentTypeStream), new File(entity.param.get("file")));
                }
            }
        }
        if (requestBody == null) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            Iterator<Map.Entry<String, String>> paramIterator = entity.param.entrySet().iterator();
            while (paramIterator.hasNext()) {
                Map.Entry<String, String> entry = paramIterator.next();
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBody = bodyBuilder.build();
        }
        if (entity.needUploadProgress) {
            return new OkHttpRequestBody(requestBody, mUploadListenerMap.get(entity.tag));
        } else {
            return requestBody;
        }
    }

    public void cancelRequest(String tag) {
        removeLoadListener(tag);
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void removeLoadListener(String tag) {
        mUploadListenerMap.remove(tag);
        mDownloadListenerMap.remove(tag);
    }

}
