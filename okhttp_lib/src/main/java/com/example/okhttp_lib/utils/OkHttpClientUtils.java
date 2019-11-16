package com.example.okhttp_lib.utils;


import android.util.Log;

import com.example.okhttp_lib.bean.OkHttpRequestEntity;
import com.example.okhttp_lib.bean.OkHttpSyncResponse;
import com.example.okhttp_lib.bean.SyncResponseType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Cg on 2017/11/7.
 */

public class OkHttpClientUtils {
    private static final String TAG = "cj";


    /**
     * 最终的调用方法（异步）
     *
     * @param client
     * @param request
     * @param responseCallback
     */
    public static void requestAsync(OkHttpClient client, Request request, Callback responseCallback) {
        if (client == null || request == null) {
            return;
        }
        client.newCall(request).enqueue(responseCallback);
    }

    /**
     * 重载了上面最终调用方法，通过handlerOkHttpClient方法，把自己定义的OkHttpRequestEntity和client进行融合
     * 就是自己的配的参数都在OkHttpRequestEntity里，最后才会加到client上。
     *
     * @param client
     * @param entity
     * @param request
     * @param responseCallback
     */
    public static void requestAsync(OkHttpClient client, OkHttpRequestEntity entity, Request request, Callback responseCallback) {
        requestAsync(handlerOkHttpClient(client, entity), request, responseCallback);
    }

    /**
     * 最终同步请求，返回Response
     *
     * @param client
     * @param request
     * @return
     * @throws IOException
     */
    public static Response requestSync(OkHttpClient client, Request request) throws IOException {
        if (client == null || request == null) {
            return null;
        }
        return client.newCall(request).execute();
    }

    /**
     * 重载了同步请求，封装client。
     *
     * @param client
     * @param entity
     * @param request
     * @return
     * @throws IOException
     */
    public static Response requestSync(OkHttpClient client, OkHttpRequestEntity entity, Request request) throws IOException {
        return requestSync(handlerOkHttpClient(client, entity), request);
    }

    public static OkHttpSyncResponse requestSync(OkHttpClient client, Request request, SyncResponseType type) {
        OkHttpSyncResponse okHttpSyncResponse = new OkHttpSyncResponse();
        Response response = null;
        try {
            response = requestSync(client, request);
            if (response == null) {
                okHttpSyncResponse.setSuccessful(false);
                okHttpSyncResponse.setError("response is not successful");
            } else if (!response.isSuccessful()) {
                okHttpSyncResponse.setSuccessful(false);
                okHttpSyncResponse.setError(response.message());
            } else {
                okHttpSyncResponse.setSuccessful(true);
                if (SyncResponseType.TypeBytes == type) {
                    okHttpSyncResponse.setResponse(response.body().bytes());
                } else if (SyncResponseType.TypeString == type) {
                    okHttpSyncResponse.setResponse(response.body().string());
                } else if (SyncResponseType.TypeStream == type) {
                    okHttpSyncResponse.setResponse(response.body().byteStream());
                }
            }
        } catch (IOException e) {
            okHttpSyncResponse.setSuccessful(false);
            okHttpSyncResponse.setError(e.getMessage());
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return okHttpSyncResponse;
    }

    public static OkHttpSyncResponse requestSync(OkHttpClient client, OkHttpRequestEntity entity, Request request, SyncResponseType type) {
        return requestSync(handlerOkHttpClient(client, entity), request, type);
    }

    /**
     * 整合OkHttpClient，工厂把自定义参数赋予client，生产出本次请求需要的OkHttpClient。
     * <p>
     * client程序中只有一个即可，通过参数传进来的，在OkHttpRequest类中
     * 两个类的关系是 不分彼此
     * 这个类算是最底层，最后再处理一下client就请求了
     * OkHttpRequest主要是处理request，生产出request
     * <p>
     * client.newCall(request).enqueue(responseCallback);
     * 就这三个对象，分步骤处理好就可以发起请求了
     * client
     * request
     * callback
     * <p>
     * 注意：本以为每次一旦设过超时时间后，之后所有的请求都会变成这个超时时间
     * 但并不是这样，刚哥早就考虑到了这一点。其实如果你不设参数，全局就一个okhttpclient，
     * 一旦设置了参数，就会走下面的newBuilder，这其实是又创建了一个okhttpClient，
     * 根本不是一个对象，所以不用担心会影响client默认的值，🐂🖊。
     *
     * @param client
     * @param entity
     * @return
     */
    private static OkHttpClient handlerOkHttpClient(OkHttpClient client, OkHttpRequestEntity entity) {
        if (client != null && entity != null) {
            if (entity.connectTime > 0 || entity.readTime > 0 || entity.writeTime > 0) {
                return client.newBuilder()
                        .connectTimeout(entity.connectTime > 0 ? entity.connectTime : client.connectTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .readTimeout(entity.readTime > 0 ? entity.readTime : client.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .writeTimeout(entity.writeTime > 0 ? entity.writeTime : client.writeTimeoutMillis(), TimeUnit.MILLISECONDS)
                        .authenticator(entity.authenticator == null ? Authenticator.NONE : entity.authenticator)
                        .build();//新建一个okhttpclient
            }
        }
        return client;
    }


}
